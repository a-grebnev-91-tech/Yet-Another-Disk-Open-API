CREATE TABLE IF NOT EXISTS system_items
(
    id        VARCHAR PRIMARY KEY,
    url       VARCHAR,
    update    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    parent_id VARCHAR REFERENCES system_items (id) ON DELETE CASCADE,
    type      VARCHAR                     NOT NULL,
    size      BIGINT
);