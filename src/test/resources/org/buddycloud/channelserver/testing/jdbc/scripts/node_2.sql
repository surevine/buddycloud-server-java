INSERT INTO "nodes" ("node") VALUES ('users/node2@server1/posts');

INSERT INTO "node_config" ("node_id", "key", "value", "updated") SELECT "node_id", 'config1', 'Value of config1', now() FROM "nodes" WHERE "node"='users/node2@server1/posts';
INSERT INTO "node_config" ("node_id", "key", "value", "updated") SELECT "node_id", 'config2', 'Value of config2', now() FROM "nodes" WHERE "node"='users/node2@server1/posts';

INSERT INTO "affiliations" ("node_id", "user", "affiliation", "updated") SELECT "node_id", 'user2@server1', 'owner', now() FROM "nodes" WHERE "node"='users/node2@server1/posts';
INSERT INTO "affiliations" ("node_id", "user", "affiliation", "updated") SELECT "node_id", 'user1@server1', 'publisher', now() FROM "nodes" WHERE "node"='users/node2@server1/posts';

INSERT INTO "subscriptions" ("node_id", "user", "listener", "subscription", "updated") SELECT "node_id", 'user1@server1', 'user1@server1', 'subscribed', now() FROM "nodes" WHERE "node"='users/node2@server1/posts';
INSERT INTO "subscriptions" ("node_id", "user", "listener", "subscription", "updated") SELECT "node_id", 'user1@server2', 'channels.server2', 'subscribed', now() FROM "nodes" WHERE "node"='users/node2@server1/posts';

INSERT INTO "threads" ("item_id", "updated", "node_id") SELECT 'node2:1', TIMESTAMP '2010-01-08 11:45:12', "node_id" FROM "nodes" WHERE "node" = 'users/node2@server1/posts';
INSERT INTO "items" ("thread_id", "node_id", "id", "updated", "xml") SELECT "thread_id", "node_id", "item_id", "updated", '<entry xmlns="http://www.w3.org/2005/Atom" xmlns:activity="http://activitystrea.ms/spec/1.0/">
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
             </entry>' FROM "threads" WHERE "item_id" = 'node2:1';
