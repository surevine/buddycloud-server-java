BEGIN TRANSACTION;

ALTER TABLE items ADD COLUMN label TEXT;

CREATE TABLE clearances (
       jid        TEXT PRIMARY KEY,
       clearance  TEXT
);

INSERT INTO schema_version (version, "when", description)
       VALUES (10, now(), 'Add SIO stuff');

COMMIT;
