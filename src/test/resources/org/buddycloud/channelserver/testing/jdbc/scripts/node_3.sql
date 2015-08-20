INSERT INTO "nodes" ("node") VALUES ('users/node3@server1/posts');

INSERT INTO "node_config" ("node_id", "key", "value", "updated") SELECT node_id, 'config1', 'Value of config1', now() FROM nodes WHERE node='users/node3@server1/posts';

INSERT INTO "node_config" ("node_id", "key", "value", "updated") SELECT node_id, 'config2', 'Value of config2', now() FROM nodes WHERE node='users/node3@server1/posts';

INSERT INTO "affiliations" ("node_id", "user", "affiliation", "updated") SELECT node_id, 'user1@server1', 'owner', current_timestamp - interval '3' second FROM nodes WHERE node='users/node3@server1/posts';
INSERT INTO "affiliations" ("node_id", "user", "affiliation", "updated") SELECT node_id, 'user2@server1', 'publisher', current_timestamp - interval '2' second FROM nodes WHERE node='users/node3@server1/posts';
INSERT INTO "affiliations" ("node_id", "user", "affiliation", "updated") SELECT node_id, 'user1@server2', 'publisher', current_timestamp - interval '2' second FROM nodes WHERE node='users/node3@server1/posts';
INSERT INTO "affiliations" ("node_id", "user", "affiliation", "updated") SELECT node_id, 'user3@server2', 'member', current_timestamp - interval '1' second FROM nodes WHERE node='users/node3@server1/posts';
INSERT INTO "affiliations" ("node_id", "user", "affiliation", "updated") SELECT node_id, 'outcast@server1', 'outcast', current_timestamp - interval '2' second FROM nodes WHERE node='users/node3@server1/posts';
INSERT INTO "subscriptions" ("node_id", "user", "listener", "subscription", "updated") SELECT node_id, 'user1@server1', 'user1@server1', 'subscribed', current_timestamp - interval '4' second FROM nodes WHERE node='users/node3@server1/posts';
INSERT INTO "subscriptions" ("node_id", "user", "listener", "subscription", "updated") SELECT node_id, 'user2@server1', 'user2@server1', 'subscribed', current_timestamp - interval '3' second FROM nodes WHERE node='users/node3@server1/posts';
INSERT INTO "subscriptions" ("node_id", "user", "listener", "subscription", "updated") SELECT node_id, 'user1@server2', 'channels.server2', 'subscribed', current_timestamp - interval '2' second FROM nodes WHERE node='users/node3@server1/posts';
INSERT INTO "subscriptions" ("node_id", "user", "listener", "subscription", "updated") SELECT node_id, 'user3@server2', 'channels.server2', 'subscribed', current_timestamp - interval '1' second FROM nodes WHERE node='users/node3@server1/posts';
INSERT INTO "subscriptions" ("node_id", "user", "listener", "subscription", "updated") SELECT node_id, 'outcast@server1', 'outcast@server1', 'subscribed', current_timestamp - interval '2' second FROM nodes WHERE node='users/node3@server1/posts';
-- The strange order of insertion of the items is deliberate

INSERT INTO "threads" ("item_id", "updated", "node_id") SELECT 'a5', TIMESTAMP '2010-01-08 11:45:12', "node_id" FROM "nodes" WHERE "node" = 'users/node3@server1/posts';
INSERT INTO "items" ("node_id", "id", "updated", "xml") SSELECT "node_id", "item_id", "updated", '<entry xmlns="http://www.w3.org/2005/Atom" xmlns:activity="http://activitystrea.ms/spec/1.0/">
                <published>2010-01-08T11:45:12Z</published>
                <author>
                   <name>user2@server1</name>
                   <jid xmlns="http://buddycloud.com/atom-elements-0">user2@server1</jid>
                </author>
                <content type="text">Test 5</content>
                <geoloc xmlns="http://jabber.org/protocol/geoloc">
                   <text>London, England</text>
                   <locality>London</locality>
                   <country>England</country>
                </geoloc>

                <activity:verb>post</activity:verb>
                <activity:object>
                  <activity:object-type>note</activity:object-type>
                </activity:object>
             </entry>' FROM "nodes" JOIN "threads" ON "threads"."node_id" = "nodes"."node_id" WHERE "node"='users/node3@server1/posts' AND "item_id" = 'a5';

INSERT INTO "threads" ("item_id", "updated", "node_id") SELECT 'a2', TIMESTAMP '2010-01-06 22:32:12', "node_id" FROM "nodes" WHERE "node" = 'users/node3@server1/posts';
INSERT INTO "items" ("node_id", "id", "updated", "xml") SELECT "node_id", "item_id", "updated", '<entry xmlns="http://www.w3.org/2005/Atom" xmlns:activity="http://activitystrea.ms/spec/1.0/">
                <published>2010-01-06T22:32:12Z</published>
                <author>
                   <name>user1@server1</name>
                   <jid xmlns="http://buddycloud.com/atom-elements-0">user1@server1</jid>
                </author>
                <content type="text">Test 2</content>
                <geoloc xmlns="http://jabber.org/protocol/geoloc">
                   <text>Paris, France</text>
                   <locality>Paris</locality>
                   <country>France</country>
                </geoloc>

                <activity:verb>post</activity:verb>
                <activity:object>
                  <activity:object-type>note</activity:object-type>
                </activity:object>
             </entry>' FROM "nodes" JOIN "threads" ON "threads"."node_id" = "nodes"."node_id" WHERE "node"='users/node3@server1/posts' AND "item_id" = 'a2';

INSERT INTO "threads" ("item_id", "updated", "node_id") SELECT 'a4', TIMESTAMP '2010-01-08 10:14:54', "node_id" FROM "nodes" WHERE "node" = 'users/node3@server1/posts';
INSERT INTO "items" ("node_id", "id", "updated", "xml") SSELECT "node_id", "item_id", "updated", '<entry xmlns="http://www.w3.org/2005/Atom" xmlns:activity="http://activitystrea.ms/spec/1.0/">
                <published>2010-01-08T10:14:54Z</published>
                <author>
                   <name>user1@server1</name>
                   <jid xmlns="http://buddycloud.com/atom-elements-0">user1@server1</jid>
                </author>
                <content type="text">Test 4</content>
                <geoloc xmlns="http://jabber.org/protocol/geoloc">
                   <text>London, England</text>
                   <locality>London</locality>
                   <country>England</country>
                </geoloc>

                <activity:verb>post</activity:verb>
                <activity:object>
                  <activity:object-type>note</activity:object-type>
                </activity:object>
             </entry>' FROM "nodes" JOIN "threads" ON "threads"."node_id" = "nodes"."node_id" WHERE "node"='users/node3@server1/posts' AND "item_id" = 'a4';

INSERT INTO "threads" ("item_id", "updated", "node_id") SELECT 'a3', TIMESTAMP '2010-01-07 13:33:34', "node_id" FROM "nodes" WHERE "node" = 'users/node3@server1/posts';
INSERT INTO "items" ("node_id", "id", "updated", "xml") SELECT "node_id", "item_id", "updated", '<entry xmlns="http://www.w3.org/2005/Atom" xmlns:activity="http://activitystrea.ms/spec/1.0/">
                <published>2010-01-07T13:33:34Z</published>
                <author>
                   <name>user2@server1</name>
                   <jid xmlns="http://buddycloud.com/atom-elements-0">user2@server1</jid>
                </author>
                <content type="text">Test 3</content>
                <geoloc xmlns="http://jabber.org/protocol/geoloc">
                   <text>London, England</text>
                   <locality>London</locality>
                   <country>England</country>
                </geoloc>

                <activity:verb>post</activity:verb>
                <activity:object>
                  <activity:object-type>note</activity:object-type>
                </activity:object>
             </entry>' FROM "nodes" JOIN "threads" ON "threads"."node_id" = "nodes"."node_id" WHERE "node"='users/node3@server1/posts' AND "item_id" = 'a2';


INSERT INTO "threads" ("item_id", "updated", "node_id") SELECT 'a2', TIMESTAMP '2010-01-06 22:32:12', "node_id" FROM "nodes" WHERE "node" = 'users/node3@server1/posts';
INSERT INTO "items" ("node_id", "id", "updated", "xml") SELECT "node_id", "item_id", "updated", '<entry xmlns="http://www.w3.org/2005/Atom" xmlns:activity="http://activitystrea.ms/spec/1.0/">
                <published>2010-01-06T21:41:32Z</published>
                <author>
                   <name>user1@server1</name>
                   <jid xmlns="http://buddycloud.com/atom-elements-0">user1@server1</jid>
                </author>
                <content type="text">Test 1</content>
                <geoloc xmlns="http://jabber.org/protocol/geoloc">
                   <text>Paris, France</text>
                   <locality>Paris</locality>
                   <country>France</country>
                </geoloc>

                <activity:verb>post</activity:verb>
                <activity:object>
                  <activity:object-type>note</activity:object-type>
                </activity:object>
             </entry>' FROM "nodes" JOIN "threads" ON "threads"."node_id" = "nodes"."node_id" WHERE "node"='users/node3@server1/posts' AND "item_id" = 'a3';
