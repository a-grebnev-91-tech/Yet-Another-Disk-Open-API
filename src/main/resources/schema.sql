CREATE TABLE IF NOT EXISTS system_items
(
    id          VARCHAR PRIMARY KEY,
    url         VARCHAR,
    update_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    parent_id   VARCHAR REFERENCES system_items (id) ON DELETE CASCADE,
    type        VARCHAR                     NOT NULL,
    size        BIGINT
);

CREATE OR REPLACE FUNCTION update_time() RETURNS TRIGGER AS
'
    BEGIN
        IF NEW.parent_id IS NOT NULL THEN
            UPDATE system_items
            SET update_date = (NEW.update_date)
            WHERE id = NEW.parent_id;
            RETURN NEW;
        END IF;
        RETURN NULL;
    END;
' LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS t_system_items ON system_items;

CREATE TRIGGER t_system_items
    AFTER INSERT OR UPDATE
    ON system_items
    FOR EACH ROW
EXECUTE PROCEDURE update_time();