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

INSERT INTO "nodes" ("node") VALUES ('/user/zz-private-never@server1/posts');
INSERT INTO "nodes" ("node") VALUES ('/user/zz-private-joinable@server1/posts');
INSERT INTO "nodes" ("node") VALUES ('/user/zz-private-always@server1/posts');
INSERT INTO "nodes" ("node") VALUES ('/user/zz-private-locally@server1/posts');

INSERT INTO "nodes" ("node") VALUES ('/user/zz-local-never@server1/posts');
INSERT INTO "nodes" ("node") VALUES ('/user/zz-local-joinable@server1/posts');
INSERT INTO "nodes" ("node") VALUES ('/user/zz-local-always@server1/posts');
INSERT INTO "nodes" ("node") VALUES ('/user/zz-local-locally@server1/posts');

INSERT INTO "nodes" ("node") VALUES ('/user/zz-open-never@server1/posts');
INSERT INTO "nodes" ("node") VALUES ('/user/zz-open-joinable@server1/posts');
INSERT INTO "nodes" ("node") VALUES ('/user/zz-open-always@server1/posts');
INSERT INTO "nodes" ("node") VALUES ('/user/zz-open-locally@server1/posts');

INSERT INTO "node_config" ("node_id", "key", "value", "updated")
    SELECT "node_id", 'pubsub#access_model', 'authorize', NOW()
    FROM "nodes" WHERE "node" LIKE '/user/zz-private-%';
INSERT INTO "node_config" ("node_id", "key", "value", "updated")
    SELECT "node_id", 'pubsub#access_model', 'local', NOW()
    FROM "nodes" WHERE "node" LIKE '/user/zz-local-%';
INSERT INTO "node_config" ("node_id", "key", "value", "updated")
    SELECT "node_id", 'pubsub#access_model', 'open', NOW()
    FROM "nodes" WHERE "node" LIKE '/user/zz-open-%';

INSERT INTO "node_config" ("node_id", "key", "value", "updated")
    SELECT "node_id", 'buddycloud#advertise_node', 'false', NOW()
    FROM "nodes" WHERE "node" LIKE '/user/zz-%-never@%/posts';
INSERT INTO "node_config" ("node_id", "key", "value", "updated")
    SELECT "node_id", 'buddycloud#advertise_node', 'joinable', NOW()
    FROM "nodes" WHERE "node" LIKE '/user/zz-%-joinable@%/posts';
INSERT INTO "node_config" ("node_id", "key", "value", "updated")
    SELECT "node_id", 'buddycloud#advertise_node', 'true', NOW()
    FROM "nodes" WHERE "node" LIKE '/user/zz-%-always@%/posts';
INSERT INTO "node_config" ("node_id", "key", "value", "updated")
    SELECT "node_id", 'buddycloud#advertise_node', 'locally', NOW()
    FROM "nodes" WHERE "node" LIKE '/user/zz-%-locally@%/posts';
