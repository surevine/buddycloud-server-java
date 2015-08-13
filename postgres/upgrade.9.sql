BEGIN TRANSACTION;

-- Make a new nodes table with an integral pkey.

-- Move old table out of the way.
ALTER TABLE nodes RENAME TO nodes_old;

CREATE TABLE nodes (
  node_id SERIAL PRIMARY KEY,
  node TEXT NOT NULL,
  CONSTRAINT nodes_node UNIQUE (node)
);

-- Populate new table.
INSERT INTO nodes (node) SELECT node FROM nodes_old;

-- Four tables now have a foreign key with cascade deletion to the old table;
-- we need to change the column to a foreign key into the new table.
-- In each case, we remove the old constraint, create a new nullable column,
-- populate it, add the constraints, and remove the old column.
ALTER TABLE affiliations DROP CONSTRAINT affiliations_node_nodes_node;
ALTER TABLE affiliations ADD COLUMN node_id INTEGER;
UPDATE affiliations SET node_id=(SELECT node_id FROM nodes WHERE node=affiliations.node);
ALTER TABLE affiliations ALTER COLUMN node_id SET NOT NULL;
ALTER TABLE affiliations ADD FOREIGN KEY (node_id) REFERENCES nodes ON DELETE CASCADE;
ALTER TABLE affiliations DROP COLUMN node CASCADE;
ALTER TABLE affiliations ADD PRIMARY KEY (node_id, "user");
CREATE INDEX affiliations_user_node ON affiliations ("user", node_id);

DROP VIEW open_nodes;

ALTER TABLE node_config DROP CONSTRAINT node_config_node_nodes_node;
ALTER TABLE node_config ADD COLUMN node_id INTEGER;
UPDATE node_config SET node_id=(SELECT node_id FROM nodes WHERE node=node_config.node);
ALTER TABLE node_config ALTER COLUMN node_id SET NOT NULL;
ALTER TABLE node_config ADD FOREIGN KEY (node_id) REFERENCES nodes ON DELETE CASCADE;
ALTER TABLE node_config DROP COLUMN node CASCADE;
ALTER TABLE node_config ADD PRIMARY KEY (node_id, "key");

CREATE VIEW open_nodes AS
       SELECT DISTINCT nodes.node FROM nodes JOIN node_config
               ON node_config.node_id=nodes.node_id
               WHERE "key"='accessModel'
          AND "value"='open';

ALTER TABLE subscriptions DROP CONSTRAINT subscriptions_node_nodes_node;
ALTER TABLE subscriptions ADD COLUMN node_id INTEGER;
UPDATE subscriptions SET node_id=(SELECT node_id FROM nodes WHERE node=subscriptions.node);
ALTER TABLE subscriptions ALTER COLUMN node_id SET NOT NULL;
ALTER TABLE subscriptions ADD FOREIGN KEY (node_id) REFERENCES nodes ON DELETE CASCADE;
ALTER TABLE subscriptions DROP COLUMN node;
ALTER TABLE subscriptions ADD PRIMARY KEY (node_id, "user");
CREATE INDEX subscriptions_user_node ON subscriptions("user", node_id);

ALTER TABLE items DROP CONSTRAINT items_node_nodes_node;
ALTER TABLE items ADD COLUMN node_id INTEGER;
UPDATE items SET node_id=(SELECT node_id FROM nodes WHERE node=items.node);
ALTER TABLE items ALTER COLUMN node_id SET NOT NULL;
ALTER TABLE items ADD FOREIGN KEY (node_id) REFERENCES nodes ON DELETE CASCADE;
ALTER TABLE items DROP COLUMN node;
ALTER TABLE items ADD PRIMARY KEY (node_id, id);

-- At this point the old nodes table can be safely removed; we use TRUNCATE
-- to abort if the table still has foreign keys to it.
TRUNCATE nodes_old;
DROP TABLE nodes_old;

-- Now extract threads to a first-class object.
CREATE TABLE threads (
  thread_id SERIAL PRIMARY KEY,
  item_id TEXT NOT NULL,
  updated TIMESTAMP WITH TIME ZONE,
  node_id INTEGER REFERENCES nodes,
  CONSTRAINT threads_item_id UNIQUE (node_id,item_id)
);
CREATE INDEX threads_node_updated ON threads(node_id,updated);
INSERT INTO threads (item_id, updated, node_id) SELECT COALESCE(in_reply_to,id) AS thread_id, MAX(updated), node_id FROM items GROUP BY node_id, thread_id;

-- items.thread_id now becomes the method to locate a thread within the items.
ALTER TABLE items ADD COLUMN thread_id INTEGER;
UPDATE items SET thread_id=(SELECT thread_id FROM threads WHERE items.node_id = threads.node_id AND item_id = COALESCE(items.in_reply_to,items.id));
ALTER TABLE items ALTER COLUMN thread_id SET NOT NULL;
ALTER TABLE items ADD FOREIGN KEY (thread_id) REFERENCES threads;
CREATE INDEX items_thread_id ON items(thread_id);

INSERT INTO schema_version (version, "when", description)
       VALUES (9, now(), 'Renormalized nodes, threads, and item db_ids');

COMMIT;
