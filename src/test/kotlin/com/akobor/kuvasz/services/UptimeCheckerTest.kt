package com.akobor.kuvasz.services

import com.akobor.kuvasz.DatabaseBehaviorSpec
import com.akobor.kuvasz.mocks.createMonitor
import com.akobor.kuvasz.events.MonitorDownEvent
import com.akobor.kuvasz.events.MonitorUpEvent
import com.akobor.kuvasz.events.RedirectEvent
import com.akobor.kuvasz.repositories.MonitorRepository
import com.akobor.kuvasz.util.toUri
import com.akobor.kuvasz.utils.toSubscriber
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpStatus
import io.micronaut.http.simple.SimpleHttpResponseFactory
import io.micronaut.test.annotation.MicronautTest
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.spyk
import io.reactivex.Flowable.fromArray
import io.reactivex.subscribers.TestSubscriber
import java.net.URI

@MicronautTest
class UptimeCheckerTest(
    uptimeChecker: UptimeChecker,
    private val monitorRepository: MonitorRepository,
    private val eventDispatcher: EventDispatcher
) : DatabaseBehaviorSpec() {
    init {
        val uptimeCheckerSpy = spyk(uptimeChecker, recordPrivateCalls = true)

        given("the UptimeChecker service") {
            `when`("it checks a monitor that is UP") {
                val monitor = createMonitor(monitorRepository)
                val subscriber = TestSubscriber<MonitorUpEvent>()
                eventDispatcher.subscribeToMonitorUpEvents { it.toSubscriber(subscriber) }
                mockHttpResponse(uptimeCheckerSpy, HttpStatus.OK)

                uptimeCheckerSpy.check(monitor)

                then("it should dispatch a MonitorUpEvent") {
                    val expectedEvent = subscriber.values().first()

                    subscriber.valueCount() shouldBe 1
                    expectedEvent.status shouldBe HttpStatus.OK
                    expectedEvent.monitor.id shouldBe monitor.id
                }
            }

            `when`("it checks a monitor that is DOWN") {
                val monitor = createMonitor(monitorRepository)
                val subscriber = TestSubscriber<MonitorDownEvent>()
                eventDispatcher.subscribeToMonitorDownEvents { it.toSubscriber(subscriber) }
                mockHttpResponse(uptimeCheckerSpy, HttpStatus.INTERNAL_SERVER_ERROR)

                uptimeCheckerSpy.check(monitor)

                then("it should dispatch a MonitorDownEvent") {
                    val expectedEvent = subscriber.values().first()

                    subscriber.valueCount() shouldBe 1
                    expectedEvent.status shouldBe HttpStatus.INTERNAL_SERVER_ERROR
                    expectedEvent.monitor.id shouldBe monitor.id
                }
            }

            `when`("it checks a monitor that is redirected without a Location header") {
                val monitor = createMonitor(monitorRepository)
                val subscriber = TestSubscriber<MonitorDownEvent>()
                eventDispatcher.subscribeToMonitorDownEvents { it.toSubscriber(subscriber) }
                mockHttpResponse(uptimeCheckerSpy, HttpStatus.PERMANENT_REDIRECT)

                uptimeCheckerSpy.check(monitor)

                then("it should dispatch a MonitorDownEvent") {
                    val expectedEvent = subscriber.values().first()

                    subscriber.valueCount() shouldBe 1
                    expectedEvent.status shouldBe HttpStatus.PERMANENT_REDIRECT
                    expectedEvent.monitor.id shouldBe monitor.id
                }
            }

            `when`("it checks a monitor that is redirected with a Location header, but it's DOWN") {
                val monitor = createMonitor(monitorRepository)
                val redirectSubscriber = TestSubscriber<RedirectEvent>()
                val monitorDownSubscriber = TestSubscriber<MonitorDownEvent>()
                val redirectLocation = "http://redirected-bad.loc"
                val headers = mapOf(HttpHeaders.LOCATION to redirectLocation)

                eventDispatcher.subscribeToRedirectEvents { it.toSubscriber(redirectSubscriber) }
                eventDispatcher.subscribeToMonitorDownEvents { it.toSubscriber(monitorDownSubscriber) }
                mockHttpResponse(uptimeCheckerSpy, HttpStatus.PERMANENT_REDIRECT, monitor.url.toUri(), headers)
                mockHttpResponse(uptimeCheckerSpy, HttpStatus.INTERNAL_SERVER_ERROR, redirectLocation.toUri())

                uptimeCheckerSpy.check(monitor)

                then("it should dispatch a RedirectEvent and then a MonitorDownEvent") {
                    val expectedRedirectEvent = redirectSubscriber.values().first()
                    val expectedDownEvent = monitorDownSubscriber.values().first()

                    redirectSubscriber.valueCount() shouldBe 1
                    expectedRedirectEvent.redirectLocation shouldBe redirectLocation.toUri()
                    expectedRedirectEvent.monitor.id shouldBe monitor.id

                    monitorDownSubscriber.valueCount() shouldBe 1
                    expectedDownEvent.status shouldBe HttpStatus.INTERNAL_SERVER_ERROR
                    expectedDownEvent.monitor.id shouldBe monitor.id
                }
            }

            `when`("it checks a monitor that is redirected with a Location header, but it's UP") {
                val monitor = createMonitor(monitorRepository)
                val redirectSubscriber = TestSubscriber<RedirectEvent>()
                val monitorUpSubscriber = TestSubscriber<MonitorUpEvent>()
                val redirectLocation = "http://redirected-good.loc"
                val headers = mapOf(HttpHeaders.LOCATION to redirectLocation)

                eventDispatcher.subscribeToRedirectEvents { it.toSubscriber(redirectSubscriber) }
                eventDispatcher.subscribeToMonitorUpEvents { it.toSubscriber(monitorUpSubscriber) }
                mockHttpResponse(uptimeCheckerSpy, HttpStatus.PERMANENT_REDIRECT, monitor.url.toUri(), headers)
                mockHttpResponse(uptimeCheckerSpy, HttpStatus.OK, redirectLocation.toUri())

                uptimeCheckerSpy.check(monitor)

                then("it should dispatch a RedirectEvent and then a MonitorUpEvent") {
                    val expectedRedirectEvent = redirectSubscriber.values().first()
                    val expectedUpEvent = monitorUpSubscriber.values().first()

                    redirectSubscriber.valueCount() shouldBe 1
                    expectedRedirectEvent.redirectLocation shouldBe redirectLocation.toUri()
                    expectedRedirectEvent.monitor.id shouldBe monitor.id

                    monitorUpSubscriber.valueCount() shouldBe 1
                    expectedUpEvent.status shouldBe HttpStatus.OK
                    expectedUpEvent.monitor.id shouldBe monitor.id
                }
            }
        }
    }

    override fun afterTest(testCase: TestCase, result: TestResult) {
        clearAllMocks()
        super.afterTest(testCase, result)
    }

    private fun mockHttpResponse(
        uptimeChecker: UptimeChecker,
        httpStatus: HttpStatus,
        requestUri: URI? = null,
        additionalHeaders: Map<String, String> = emptyMap()
    ) {
        val response = SimpleHttpResponseFactory()
            .status<Any>(httpStatus)
            .headers { headers ->
                additionalHeaders.forEach { (name, value) ->
                    headers.add(name, value)
                }
            }
        every { uptimeChecker["sendHttpRequest"](requestUri ?: allAny<URI>()) } returns fromArray(response)
    }
}
