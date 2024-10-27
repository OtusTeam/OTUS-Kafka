ATTACH TABLE _ UUID 'ecf9b571-35e1-48c1-8acb-f31dea1bc0d3'
(
    `before.id` Nullable(Int32),
    `before.name` Nullable(String),
    `after.id` Nullable(Int32),
    `after.name` Nullable(String),
    `source.version` Nullable(String),
    `source.connector` Nullable(String),
    `source.name` Nullable(String),
    `source.ts_ms` Nullable(UInt64),
    `source.snapshot` Nullable(String),
    `source.db` Nullable(String),
    `source.sequence` Nullable(String),
    `source.schema` Nullable(String),
    `source.table` Nullable(String),
    `source.txId` Nullable(UInt64),
    `source.lsn` Nullable(UInt64),
    `source.xmin` Nullable(UInt64),
    `op` LowCardinality(String),
    `ts_ms` Nullable(UInt64),
    `transaction.id` Nullable(UInt64),
    `transaction.total_order` Nullable(UInt64),
    `transaction.data_collection_order` Nullable(UInt64)
)
ENGINE = ReplacingMergeTree
ORDER BY tuple()
SETTINGS index_granularity = 8192
