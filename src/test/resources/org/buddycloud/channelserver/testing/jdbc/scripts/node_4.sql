
UPDATE "threads" SET "updated" = TIMESTAMP '2010-01-08 11:45:12' WHERE "node_id" = (SELECT "node_id" FROM "nodes" WHERE "node" =  'users/node1@server1/posts') AND "item_id"='a1'
INSERT INTO "items" ("node_id", "thread_id", "id", "updated", "xml", "in_reply_to") SELECT "nodes"."node_id", "thread_id", 'a6', "updated", '<entry xmlns="http://www.w3.org/2005/Atom" xmlns:activity="http://activitystrea.ms/spec/1.0/">
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
             </entry>', "item_id" FROM "nodes" JOIN "threads" ON "threads"."node_id"="nodes"."node_id" WHERE "node"='users/node1@server1/posts' AND "item_id" = 'a1';

UPDATE "threads" SET "updated" = TIMESTAMP '2010-01-06 22:32:12' WHERE "node_id" = (SELECT "node_id" FROM "nodes" WHERE "node" =  'users/node1@server1/posts') AND "item_id"='a1'
INSERT INTO "items" ("node_id", "thread_id", "id", "updated", "xml", "in_reply_to") SELECT "nodes"."node_id", "thread_id", 'a7', "updated", '<entry xmlns="http://www.w3.org/2005/Atom" xmlns:activity="http://activitystrea.ms/spec/1.0/">
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
             </entry>', 'a1' FROM "nodes" JOIN "threads" ON "threads"."node_id"="nodes"."node_id" WHERE "node"='users/node1@server1/posts' AND "item_id" = 'a1';

INSERT INTO "threads" ("item_id", "updated", "node_id") SELECT 'a8', TIMESTAMP '2010-01-06 22:32:12', "node_id" FROM "nodes" WHERE "node" = 'users/node1@server1/posts';
INSERT INTO "items" ("node_id", "thread_id", "id", "updated", "xml", "in_reply_to") SELECT "nodes"."node_id", "threads"."thread_id", "threads"."item_id", "threads"."updated", '<entry xmlns="http://www.w3.org/2005/Atom" xmlns:activity="http://activitystrea.ms/spec/1.0/">
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
             </entry>', NULL FROM "nodes" JOIN "threads" ON "threads"."node_id" = "nodes"."node_id" WHERE "node"='users/node1@server1/posts' AND "item_id" = 'a8';
