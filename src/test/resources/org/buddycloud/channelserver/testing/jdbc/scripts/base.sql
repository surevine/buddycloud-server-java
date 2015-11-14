CREATE TABLE "nodes" ("node_id" IDENTITY, "node" TEXT NOT NULL);
CREATE TABLE "node_config" ("node_id" INTEGER NOT NULL REFERENCES "nodes" ("node_id") ON DELETE CASCADE,
       	     		  "key" TEXT,
			  "value" TEXT,
			  "updated" TIMESTAMP,
			  PRIMARY KEY ("node_id", "key"));
CREATE TABLE "threads" (
  "thread_id" IDENTITY,
  "node_id" INTEGER NOT NULL REFERENCES "nodes"("node_id") ON DELETE CASCADE,
  "updated" TIMESTAMP, "item_id" TEXT);
CREATE TABLE "items" ("node_id" INTEGER NOT NULL REFERENCES "nodes" ("node_id") ON DELETE CASCADE,
       	     	    "id" TEXT NOT NULL,
		    "updated" TIMESTAMP,
		    "xml" TEXT,
		    "in_reply_to" TEXT,
            "created" TIMESTAMP DEFAULT NULL,
            "label" TEXT,
            "thread_id" INTEGER NOT NULL REFERENCES "threads",
		    PRIMARY KEY ("node_id", "id"));
CREATE INDEX "items_updated" ON "items" ("updated");
CREATE INDEX "items_in_reply_to" ON "items" ("node_id", "in_reply_to");

CREATE TABLE "subscriptions" ("node_id" INTEGER REFERENCES "nodes" ("node_id") ON DELETE CASCADE,
       	     		    "user" TEXT,
			    "listener" TEXT,
			    "subscription" TEXT,
 			    "updated" TIMESTAMP,
 			    "temporary" BOOLEAN DEFAULT FALSE,
                "invited_by" TEXT,
			    PRIMARY KEY ("node_id", "user"));
CREATE INDEX "subscriptions_updated" ON "subscriptions" ("updated");
CREATE TABLE "affiliations" ("node_id" INTEGER REFERENCES "nodes" ("node_id") ON DELETE CASCADE,
       	     		   "user" TEXT,
			   "affiliation" TEXT,
 			   "updated" TIMESTAMP,
			   PRIMARY KEY ("node_id", "user"));
CREATE INDEX "affiliations_updated" ON "affiliations" ("updated");


--CREATE VIEW "open_nodes" AS
--       SELECT DISTINCT "node" FROM "node_config"
--         JOIN "nodes" ON nodes.node_id=node_config.node_id
--       	      WHERE "key"='accessModel'
--	        AND "value"='open';

CREATE TABLE "online_users" ("user" TEXT NOT NULL,
			  "updated" TIMESTAMP);

-- Upgrade 2

-- MIXED IN ABOVE

-- Upgrade 3

-- MIXED IN ABOVE

-- Upgrade 4

-- MIXED IN ABOVE

-- Upgrade 6

-- MIXED IN ABOVE

-- Upgrade 7

-- MIXED IN ABOVE
