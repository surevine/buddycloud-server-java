INSERT INTO "nodes" ("node") VALUES ('/users/subscribed@server1/posts');
INSERT INTO "nodes" ("node") VALUES ('/users/unsubscribed@server1/posts');
INSERT INTO "nodes" ("node") VALUES ('/users/pending@server1/posts');
INSERT INTO "nodes" ("node") VALUES ('/users/subscribed@server1/status');

INSERT INTO "subscriptions" ("node_id", "user", "listener", "subscription", "updated")
    SELECT "node_id", 'user1@server1', 'user1@server1', 'subscribed', current_timestamp - interval '4' second FROM "nodes" WHERE "node" = '/users/subscribed@server1/posts';
INSERT INTO "subscriptions" ("node_id", "user", "listener", "subscription", "updated")
    SELECT "node_id", 'user2@server1', 'user2@server1', 'subscribed', current_timestamp - interval '3' second FROM "nodes" WHERE "node" = '/users/subscribed@server1/status';
INSERT INTO "subscriptions" ("node_id", "user", "listener", "subscription", "updated")
    SELECT "node_id", 'user2@server1', 'user2@server1', 'pending', current_timestamp - interval '3' second FROM "nodes" WHERE "node" = '/users/pending@server1/posts';

-- The strange order of insertion of the items is deliberate

-- author@server1
-- not-author@server1
INSERT INTO "threads" ("node_id", "item_id", "updated") SELECT "node_id", 'a1', TIMESTAMP '2010-01-08 11:45:12'  FROM "nodes" WHERE "node" = '/users/subscribed@server1/posts';
INSERT INTO "items" ("thread_id", "node_id", "id", "updated", "xml")
SELECT "thread_id", "nodes"."node_id", "item_id", "updated",
               '<entry xmlns="http://www.w3.org/2005/Atom" xmlns:activity="http://activitystrea.ms/spec/1.0/">
                <published>2010-01-08T11:45:12Z</published>
                <author>
                   <name>author@server1</name>
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
             </entry>' FROM "threads"
             JOIN "nodes" ON "nodes"."node_id" = "threads"."node_id"
             WHERE "node" = '/users/subscribed@server1/posts' AND "item_id" = 'a1';

INSERT INTO "threads" ("node_id", "item_id", "updated") SELECT "node_id", 'a2', TIMESTAMP '2010-01-08 11:45:12'  FROM "nodes" WHERE "node" = '/users/unsubscribed@server1/posts';
INSERT INTO "items" ("thread_id", "node_id", "id", "updated", "xml")
SELECT "thread_id", "nodes"."node_id", "item_id", "updated",
               '<entry xmlns="http://www.w3.org/2005/Atom" xmlns:activity="http://activitystrea.ms/spec/1.0/">
                <published>2010-01-08T11:45:12Z</published>
                <author>
                   <name>author@server1</name>
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
             </entry>' FROM "threads"
JOIN "nodes" ON "nodes"."node_id" = "threads"."node_id"
WHERE "node" = '/users/unsubscribed@server1/posts' AND "item_id" = 'a2';

INSERT INTO "threads" ("node_id", "item_id", "updated") SELECT "node_id", 'a3', TIMESTAMP '2010-01-08 11:45:12'  FROM "nodes" WHERE "node" = '/users/pending@server1/posts';
INSERT INTO "items" ("thread_id", "node_id", "id", "updated", "xml")
SELECT "thread_id", "nodes"."node_id", "item_id", "updated",
               '<entry xmlns="http://www.w3.org/2005/Atom" xmlns:activity="http://activitystrea.ms/spec/1.0/">
                <published>2010-01-08T11:45:12Z</published>
                <author>
                   <name>author@server1</name>
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
             </entry>' FROM "threads"
JOIN "nodes" ON "nodes"."node_id" = "threads"."node_id"
WHERE "node" = '/users/pending@server1/posts' AND "item_id" = 'a3';

INSERT INTO "threads" ("node_id", "item_id", "updated") SELECT "node_id", 'a4', TIMESTAMP '2010-01-08 11:45:12'  FROM "nodes" WHERE "node" = '/users/subscribed@server1/status';
INSERT INTO "items" ("thread_id", "node_id", "id", "updated", "xml")
SELECT "thread_id", "nodes"."node_id", "item_id", "updated",
               '<entry xmlns="http://www.w3.org/2005/Atom" xmlns:activity="http://activitystrea.ms/spec/1.0/">
                <published>2010-01-08T11:45:12Z</published>
                <author>
                   <name>author@server1</name>
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
             </entry>' FROM "threads"
JOIN "nodes" ON "nodes"."node_id" = "threads"."node_id"
WHERE "node" = '/users/subscribed@server1/status' AND "item_id" = 'a4';
