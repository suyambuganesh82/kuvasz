/*
 * This file is generated by jOOQ.
 */
package com.kuvaszuptime.kuvasz.tables;


import com.kuvaszuptime.kuvasz.DefaultSchema;
import com.kuvaszuptime.kuvasz.Indexes;
import com.kuvaszuptime.kuvasz.Keys;
import com.kuvaszuptime.kuvasz.tables.records.LatencyLogRecord;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row4;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class LatencyLog extends TableImpl<LatencyLogRecord> {

    private static final long serialVersionUID = 182490023;

    /**
     * The reference instance of <code>latency_log</code>
     */
    public static final LatencyLog LATENCY_LOG = new LatencyLog();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<LatencyLogRecord> getRecordType() {
        return LatencyLogRecord.class;
    }

    /**
     * The column <code>latency_log.id</code>.
     */
    public final TableField<LatencyLogRecord, Integer> ID = createField(DSL.name("id"), org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('kuvasz.latency_log_id_seq'::regclass)", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>latency_log.monitor_id</code>.
     */
    public final TableField<LatencyLogRecord, Integer> MONITOR_ID = createField(DSL.name("monitor_id"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>latency_log.latency</code>. Lateny in ms
     */
    public final TableField<LatencyLogRecord, Integer> LATENCY = createField(DSL.name("latency"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "Lateny in ms");

    /**
     * The column <code>latency_log.created_at</code>.
     */
    public final TableField<LatencyLogRecord, OffsetDateTime> CREATED_AT = createField(DSL.name("created_at"), org.jooq.impl.SQLDataType.TIMESTAMPWITHTIMEZONE.nullable(false).defaultValue(org.jooq.impl.DSL.field("now()", org.jooq.impl.SQLDataType.TIMESTAMPWITHTIMEZONE)), this, "");

    /**
     * Create a <code>latency_log</code> table reference
     */
    public LatencyLog() {
        this(DSL.name("latency_log"), null);
    }

    /**
     * Create an aliased <code>latency_log</code> table reference
     */
    public LatencyLog(String alias) {
        this(DSL.name(alias), LATENCY_LOG);
    }

    /**
     * Create an aliased <code>latency_log</code> table reference
     */
    public LatencyLog(Name alias) {
        this(alias, LATENCY_LOG);
    }

    private LatencyLog(Name alias, Table<LatencyLogRecord> aliased) {
        this(alias, aliased, null);
    }

    private LatencyLog(Name alias, Table<LatencyLogRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> LatencyLog(Table<O> child, ForeignKey<O, LatencyLogRecord> key) {
        super(child, key, LATENCY_LOG);
    }

    @Override
    public Schema getSchema() {
        return DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.LATENCY_LOG_LATENCY_IDX, Indexes.LATENCY_LOG_LATENCY_MONITOR_IDX, Indexes.LATENCY_LOG_MONITOR_IDX);
    }

    @Override
    public Identity<LatencyLogRecord, Integer> getIdentity() {
        return Keys.IDENTITY_LATENCY_LOG;
    }

    @Override
    public UniqueKey<LatencyLogRecord> getPrimaryKey() {
        return Keys.LATENCY_LOG_PKEY;
    }

    @Override
    public List<UniqueKey<LatencyLogRecord>> getKeys() {
        return Arrays.<UniqueKey<LatencyLogRecord>>asList(Keys.LATENCY_LOG_PKEY);
    }

    @Override
    public List<ForeignKey<LatencyLogRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<LatencyLogRecord, ?>>asList(Keys.LATENCY_LOG__LATENCY_LOG_MONITOR_ID_FKEY);
    }

    public Monitor monitor() {
        return new Monitor(this, Keys.LATENCY_LOG__LATENCY_LOG_MONITOR_ID_FKEY);
    }

    @Override
    public LatencyLog as(String alias) {
        return new LatencyLog(DSL.name(alias), this);
    }

    @Override
    public LatencyLog as(Name alias) {
        return new LatencyLog(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public LatencyLog rename(String name) {
        return new LatencyLog(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public LatencyLog rename(Name name) {
        return new LatencyLog(name, null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<Integer, Integer, Integer, OffsetDateTime> fieldsRow() {
        return (Row4) super.fieldsRow();
    }
}
