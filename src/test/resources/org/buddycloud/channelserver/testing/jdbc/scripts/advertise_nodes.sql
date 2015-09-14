INSERT INTO "nodes" ("node") VALUES ('/user/advertised@server2/posts');
INSERT INTO "nodes" ("node") VALUES ('/user/not-advertised@server2/posts');
INSERT INTO "nodes" ("node") VALUES ('/user/undetermined-advertised@server2/posts');

INSERT INTO "nodes" ("node") VALUES ('/user/advertised@server1/posts');
INSERT INTO "nodes" ("node") VALUES ('/user/not-advertised@server1/posts');
INSERT INTO "nodes" ("node") VALUES ('/user/undetermined-advertised@server1/posts');

INSERT INTO "node_config" ("node_id", "key", "value", "updated") SELECT "node_id", 'buddycloud#advertise_node', 'true', now() FROM "nodes" WHERE "node"='/user/advertised@server1/posts';

INSERT INTO "node_config" ("node_id", "key", "value", "updated") SELECT "node_id", 'buddycloud#advertise_node', 'false', now() FROM "nodes" WHERE "node"='/user/not-advertised@server1/posts';

INSERT INTO "node_config" ("node_id", "key", "value", "updated") SELECT "node_id", 'buddycloud#advertise_node', 'true', now() FROM "nodes" WHERE "node"='/user/advertised@server2/posts';

INSERT INTO "node_config" ("node_id", "key", "value", "updated") SELECT "node_id", 'buddycloud#advertise_node', 'false', now() FROM "nodes" WHERE "node"='/user/not-advertised@server2/posts';
