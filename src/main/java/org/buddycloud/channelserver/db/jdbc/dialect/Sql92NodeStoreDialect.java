package org.buddycloud.channelserver.db.jdbc.dialect;

import org.buddycloud.channelserver.db.jdbc.JDBCNodeStore.NodeStoreSQLDialect;

public class Sql92NodeStoreDialect implements NodeStoreSQLDialect {
    private static final String INSERT_NODE = "INSERT INTO \"nodes\" ( \"node\" ) VALUES ( ? )";

    private static final String INSERT_CONF = "INSERT INTO \"node_config\" ( \"node_id\", \"key\", \"value\", \"updated\" )"
            + " SELECT \"node_id\", ?, ?, now() FROM \"nodes\" WHERE \"node\" = ?";

    private static final String DELETE_CONF_FROM_NODE =
    "DELETE FROM \"node_config\" WHERE \"node_id\" = (SELECT \"node_id\" FROM \"nodes\" WHERE \"node\" = ?)";

    private static final String UPDATE_CONF = "UPDATE \"node_config\" SET \"value\" = ?, \"updated\" = now()"
            + " WHERE \"node_id\" = (SELECT \"node_id\" FROM \"nodes\" WHERE \"node\" = ?) AND \"key\" = ?";

    private static final String SELECT_SINGLE_NODE_CONF_VALUE = "SELECT \"value\" FROM \"node_config\""
            + " JOIN \"nodes\" ON \"nodes\".\"node_id\"=\"node_config\".\"node_id\""
            + " WHERE \"node\" = ? AND \"key\" = ?";

    private static final String SELECT_NODE_CONF = "SELECT \"key\", \"value\" FROM \"node_config\""
            + " JOIN \"nodes\" ON \"nodes\".\"node_id\"=\"node_config\".\"node_id\""
            + " WHERE \"node\" = ? ORDER BY \"key\" ASC";

    private static final String SELECT_AFFILIATION = "SELECT \"affiliation\", \"updated\" FROM \"affiliations\" "
            + "JOIN \"nodes\" on \"nodes\".\"node_id\" = \"affiliations\".\"node_id\" "
            + " WHERE \"node\" = ? AND \"user\" = ?";

    private static final String SELECT_AFFILIATIONS_FOR_USER = "SELECT \"node\", \"user\", \"affiliation\", \"updated\" "
            + "FROM \"affiliations\""
            + "JOIN \"nodes\" on \"nodes\".\"node_id\" = \"affiliations\".\"node_id\" "
            + "WHERE \"user\" = ? ORDER BY \"updated\" ASC";

    private static final String SELECT_NODE_OWNERS = "SELECT \"user\" FROM \"affiliations\" "
            + "JOIN \"nodes\" on \"nodes\".\"node_id\" = \"affiliations\".\"node_id\" "
            + "WHERE \"node\" = ? AND \"affiliation\" = 'owner';";

    private static final String SELECT_AFFILIATION_CHANGES = "" + "SELECT \"nodes\".\"node\", \"user\", \"affiliation\", \"updated\" FROM \"affiliations\" "
            + "JOIN \"nodes\" ON \"nodes\".\"node_id\" = \"affiliations\".\"node_id\" "
            + "WHERE \"updated\" >= ? AND \"updated\" <= ? AND \"affiliations\".\"node_id\" IN "
            + "(SELECT \"subscriptions\".\"node_id\" FROM \"subscriptions\", \"affiliations\" " + "WHERE \"subscriptions\".\"user\" = ? AND "
            + "\"subscriptions\".\"subscription\" = 'subscribed' AND " + "\"affiliations\".\"node_id\" = \"subscriptions\".\"node_id\" "
            + "AND \"subscriptions\".\"user\" = \"affiliations\".\"user\" " + "AND \"affiliations\".\"affiliation\" != 'banned'  "
            + "AND \"affiliations\".\"affiliation\" != 'outcast') " + "ORDER BY \"updated\" ASC;";

    private static final String SELECT_AFFILIATIONS_FOR_USER_AFTER_NODE_ID = "SELECT \"node\", \"user\", \"affiliation\", \"updated\""
            + " FROM \"affiliations\""
            + "JOIN \"nodes\" on \"nodes\".\"node_id\" = \"affiliations\".\"node_id\" "
            + " WHERE \"user\" = ? AND "
            + "\"updated\" > (SELECT \"updated\" FROM \"affiliations\" "
            + "JOIN \"nodes\" on \"nodes\".\"node_id\" = \"affiliations\".\"node_id\" "
            + " WHERE \"user\" = ? AND \"node\" = ?) " + "ORDER BY \"updated\" ASC LIMIT ?";

    private static final String COUNT_AFFILIATIONS_FOR_USER = "SELECT COUNT(*)" + " FROM \"affiliations\" WHERE \"user\" = ?";

    private static final String SELECT_AFFILIATIONS_FOR_NODE = "SELECT \"node\", \"user\", \"affiliation\", \"updated\""
            + " FROM \"affiliations\" "
            + "JOIN \"nodes\" on \"nodes\".\"node_id\" = \"affiliations\".\"node_id\" "
            + "WHERE \"node\" = ? AND \"affiliation\" != 'outcast' ORDER BY \"updated\" ASC";

    private static final String SELECT_AFFILIATIONS_TO_NODE_FOR_OWNER = "SELECT \"node\", \"user\", \"affiliation\", \"updated\""
            + " FROM \"affiliations\" "
            + "JOIN \"nodes\" on \"nodes\".\"node_id\" = \"affiliations\".\"node_id\" "
            + "WHERE \"node\" = ? ORDER BY \"updated\" ASC";

    private static final String SELECT_AFFILIATIONS_FOR_NODE_AFTER_JID = "SELECT \"node\", \"user\", \"affiliation\", \"updated\""
            + " FROM \"affiliations\" "
            + "JOIN \"nodes\" on \"nodes\".\"node_id\" = \"affiliations\".\"node_id\" "
            + "WHERE \"node\" = ? AND \"affiliation\" != 'outcast' AND "
            + "\"updated\" > (SELECT \"updated\" FROM \"affiliations\" "
            + "JOIN \"nodes\" on \"nodes\".\"node_id\" = \"affiliations\".\"node_id\" "
            + "WHERE \"node\" = ? AND \"user\" = ?) " + "ORDER BY \"updated\" ASC LIMIT ?";

    private static final String SELECT_AFFILIATIONS_TO_NODE_FOR_OWNER_AFTER_JID = "SELECT \"node\", \"user\", \"affiliation\", \"updated\""
            + " FROM \"affiliations\""
            + "JOIN \"nodes\" on \"nodes\".\"node_id\" = \"affiliations\".\"node_id\" "
            + " WHERE \"node\" = ? AND "
            + "\"updated\" > (SELECT \"updated\" FROM \"affiliations\""
            + "JOIN \"nodes\" on \"nodes\".\"node_id\" = \"affiliations\".\"node_id\" "
            + " WHERE \"node\" = ? AND \"user\" = ?) " + "ORDER BY \"updated\" ASC LIMIT ?";

    private static final String COUNT_AFFILIATIONS_FOR_NODE = "SELECT COUNT(*)"
            + " FROM \"affiliations\""
            + "JOIN \"nodes\" on \"nodes\".\"node_id\" = \"affiliations\".\"node_id\" "
            + " WHERE \"node\" = ? AND \"affiliation\" != 'outcast';";

    private static final String COUNT_AFFILIATIONS_TO_NODE_FOR_OWNER = "SELECT COUNT(*)" + " FROM \"affiliations\""
            + "JOIN \"nodes\" on \"nodes\".\"node_id\" = \"affiliations\".\"node_id\" "
            + " WHERE \"node\" = ?";

    private static final String INSERT_AFFILIATION = "INSERT INTO \"affiliations\" ( \"node_id\", \"user\", \"affiliation\", \"updated\" )"
            + " SELECT \"node_id\", ?, ?, now() FROM \"nodes\" WHERE \"node\" = ?";

    private static final String UPDATE_AFFILIATION = "UPDATE \"affiliations\"" + " SET \"affiliation\" = ?, \"updated\" = now()"
            + "WHERE \"node_id\" = (SELECT \"node_id\" FROM \"nodes\" "
            + "WHERE \"node\" = ?) AND \"user\" = ?";

    private static final String DELETE_AFFILIATION = "DELETE FROM \"affiliations\" WHERE "
            + "\"node_id\" = (SELECT \"node_id\" FROM \"nodes\" WHERE \"node\" = ?) AND \"user\" = ?;";

    private static final String SELECT_SUBSCRIPTION = "SELECT \"node\", \"user\", \"listener\", \"subscription\", \"updated\""
            + " FROM \"subscriptions\" JOIN \"nodes\" ON \"nodes\".\"node_id\" = \"subscriptions\".\"node_id\""
            + " WHERE \"node\" = ? AND (\"user\" = ? OR \"listener\" = ? ) ORDER BY \"updated\" ASC";

    private static final String SELECT_SUBSCRIPTIONS_FOR_USER = "SELECT \"node\", \"user\", \"listener\", \"subscription\", \"updated\""
            + " FROM \"subscriptions\" JOIN \"nodes\" ON \"nodes\".\"node_id\" = \"subscriptions\".\"node_id\""
            + " WHERE \"user\" = ? OR \"listener\" = ? ORDER BY \"updated\" ASC";

    private static final String SELECT_SUBSCRIPTIONS_FOR_USER_AFTER_NODE = "SELECT \"node\", \"user\", \"listener\", \"subscription\", \"updated\""
            + " FROM \"subscriptions\" JOIN \"nodes\" ON \"nodes\".\"node_id\" = \"subscriptions\".\"node_id\""
            + " WHERE (\"user\" = ? OR \"listener\" = ?) AND "
            + "\"updated\" > (SELECT \"updated\" FROM \"affiliations\""
            + "JOIN \"nodes\" on \"nodes\".\"node_id\" = \"affiliations\".\"node_id\" "
            + " WHERE \"node\" = ? AND \"user\" = ?) " + "ORDER BY \"updated\" ASC LIMIT ?";

    private static final String SELECT_SUBSCRIPTION_CHANGES = ""
            + "SELECT \"nodes\".\"node\", \"subscriptions\".\"user\", "
            + "\"subscriptions\".\"listener\", \"subscriptions\".\"subscription\", "
            + "\"subscriptions\".\"invited_by\", \"subscriptions\".\"updated\" "
            + "FROM \"subscriptions\" "
            + "JOIN \"nodes\" ON \"nodes\".\"node_id\" = \"subscriptions\".\"node_id\" "
            + "JOIN \"subscriptions\" AS \"mysubs\" ON \"mysubs\".\"node_id\"=\"nodes\".\"node_id\" "
            + "AND \"mysubs\".\"user\" = ? "
            + "LEFT JOIN \"affiliations\" ON \"affiliations\".\"node_id\"=\"nodes\".\"node_id\" "
            + "AND \"affiliations\".\"user\" = \"mysubs\".\"user\" "
            + "WHERE \"subscriptions\".\"updated\" >= ? AND \"subscriptions\".\"updated\" <= ? "
            + "AND \"affiliations\".\"affiliation\" != 'banned'  "
            + "AND \"affiliations\".\"affiliation\" != 'outcast' "
            + "ORDER BY \"subscriptions\".\"updated\" ASC;";

    private static final String SELECT_SUBSCRIPTIONS_FOR_NODE =
            "SELECT \"node\", \"s\".\"user\", \"s\".\"listener\", \"s\".\"subscription\", \"s\".\"updated\""
                    + " FROM \"subscriptions\" AS \"s\" JOIN \"nodes\" ON \"nodes\".\"node_id\" = \"subscriptions\".\"node_id\""
                    + " JOIN \"affiliations\" AS \"a\" ON \"s\".\"node_id\" = \"a\".\"node_id\""
                    + " AND \"s\".\"user\" = \"a\".\"user\" "
                    + "WHERE \"nodes\".\"node\" = ? "
                    + "AND \"a\".\"affiliation\" != 'outcast' " + "ORDER BY \"s\".\"updated\" ASC";

    private static final String SELECT_SUBSCRIPTIONS_TO_NODE_FOR_OWNER = "SELECT \"node\", \"user\", \"listener\", \"subscription\", \"updated\""
            + " FROM \"subscriptions\" WHERE \"node\" = ? ORDER BY \"updated\" ASC";

    private static final String SELECT_SUBSCRIPTIONS_FOR_NODE_AFTER_JID = "SELECT \"node\", \"user\", \"listener\", \"subscription\", \"updated\""
            + " FROM \"subscriptions\" WHERE \"node\" = ? AND "
            + "\"updated\" > (SELECT \"updated\" FROM \"subscriptions\" WHERE \"node\" = ? AND \"user\" = ?) " + "ORDER BY \"updated\" ASC LIMIT ?";

    private static final String INSERT_SUBSCRIPTION =
            "INSERT INTO \"subscriptions\" ( \"node_id\", \"user\", \"listener\", \"subscription\", \"invited_by\", \"updated\" )"
                    + " SELECT \"node_id\", ?, ?, ?, ?, now() FROM \"nodes\" WHERE \"node\" = ?";

    private static final String UPDATE_SUBSCRIPTION = "UPDATE \"subscriptions\"" + " SET \"subscription\" = ?, \"updated\" = now(), \"listener\" = ?"
            + " WHERE \"node_id\" = (SELECT \"node_id\" FROM \"nodes\" WHERE \"node\" = ?) AND \"user\" = ?";

    private static final String DELETE_SUBSCRIPTION =
            "DELETE FROM \"subscriptions\" WHERE \"node_id\" = (SELECT \"node_id\" FROM \"nodes\" WHERE \"node\" = ?) AND \"user\" = ?";

    private static final String NODE_EXISTS = "SELECT \"node\" FROM \"nodes\" WHERE \"node\" = ?";

    private static final String SELECT_SINGLE_ITEM = "SELECT \"node\", \"id\", \"updated\", \"xml\", \"in_reply_to\", \"created\""
            + " FROM \"items\" JOIN \"nodes\" ON \"items\".\"node_id\"=\"nodes\".\"node_id\" "
            + "WHERE \"node\" = ? AND \"id\" = ?";

    private static final String SELECT_ITEMS_FOR_NODE = "SELECT \"node\", \"id\", \"updated\", \"xml\", \"in_reply_to\", \"created\" "
            + "FROM \"items\" "
            + "JOIN \"nodes\" ON \"nodes\".\"node_id\" = \"items\".\"node_id\" "
            + "WHERE \"node\" = ? %parentOnly% ORDER BY \"updated\" DESC, \"id\" ASC";

    private static final String SELECT_ITEMS_FOR_NODE_AFTER_DATE = "SELECT \"node\", \"id\", \"updated\", \"xml\", \"in_reply_to\", \"created\""
            + " FROM \"items\" "
            + "JOIN \"nodes\" ON \"nodes\".\"node_id\" = \"items\".\"node_id\" "
            + "WHERE \"node\" = ? AND ( \"updated\" > ? OR ( \"updated\" = ? AND \"id\" > ? ) )"
            + " ORDER BY \"updated\" ASC, \"id\" DESC";

    private static final String SELECT_ITEMS_FOR_NODE_BEFORE_DATE = "SELECT \"node\", \"id\", \"updated\", \"xml\", \"in_reply_to\", \"created\""
            + " FROM \"items\" "
            + "JOIN \"nodes\" ON \"nodes\".\"node_id\" = \"items\".\"node_id\" "
            + "WHERE \"node\" = ? AND ( \"updated\" < ? OR ( \"updated\" = ? AND \"id\" < ? ) ) %parentOnly%"
            + " ORDER BY \"updated\" DESC, \"id\" ASC";

    private static final String SELECT_ITEMS_FOR_USER_BETWEEN_DATES = ""
            + "SELECT \"node\", \"id\", \"items\".\"updated\", \"xml\", \"in_reply_to\", \"created\""
            + " FROM \"items\" "
            + "JOIN \"nodes\" ON \"nodes\".\"node_id\" = \"items\".\"node_id\" "
            + "JOIN \"subscriptions\" ON \"subscriptions\".\"node_id\" = \"nodes\".\"node_id\""
            + " AND \"subscriptions\".\"user\" = ? "
            + "LEFT JOIN \"affiliations\" ON \"affiliations\".\"node_id\" = \"nodes\".\"node_id\""
            + " AND \"affiliations\".\"user\" = \"subscriptions\".\"user\" "
            + "WHERE \"items\".\"updated\" >= ? AND \"items\".\"updated\" <= ? "
            + "AND \"affiliations\".\"affiliation\" != 'banned'  "
            + "AND \"affiliations\".\"affiliation\" != 'outcast' "
            + "ORDER BY \"items\".\"updated\" ASC";

    private static final String SELECT_RECENT_ITEM_PARTS = "" + "(SELECT \"id\", \"node\", \"xml\", \"updated\", \"in_reply_to\", \"created\" "
            + "FROM \"items\" "
            + "JOIN \"nodes\" ON \"nodes\".\"node_id\" = \"items\".\"node_id\" "
            + "WHERE \"node\" = ? " + "AND \"updated\" > ? " + "%parentOnly% "
            + "ORDER BY \"updated\" DESC, \"id\" ASC LIMIT ?) ";

    private static final String SELECT_COUNT_RECENT_ITEM_PARTS = "" + "(SELECT COUNT(\"id\") " + "FROM \"items\" "
            + "JOIN \"nodes\" ON \"nodes\".\"node_id\" = \"items\".\"node_id\" "
            + "WHERE \"node\" = ? "
            + "AND \"updated\" > ? " + "%parentOnly% )";

    private static final String COUNT_ITEMS_FOR_NODE = "SELECT COUNT(*) "
            + "FROM \"items\" "
            + "JOIN \"nodes\" ON \"nodes\".\"node_id\" = \"items\".\"node_id\" "
            + "WHERE \"node\" = ? %parentOnly%";

    private static final String SELECT_ITEM_REPLIES = "" + "SELECT \"id\", \"node\", \"xml\", \"items\".\"updated\", \"in_reply_to\", \"created\" "
            + "FROM \"items\" "
            + "JOIN \"threads\" ON \"threads\".\"thread_id\" = \"items\".\"thread_id\" "
            + "JOIN \"nodes\" ON \"nodes\".\"node_id\" = \"threads\".\"node_id\" "
            + " WHERE \"node\" = ? AND \"item_id\" = ? "
            + " AND \"in_reply_to\" IS NOT NULL "
            + "AND \"items\".\"updated\" %beforeAfter% ? ORDER BY \"items\".\"updated\" DESC";

    private static final String SELECT_ITEM_THREAD = "" + "SELECT \"id\", \"node\", \"xml\", \"items\".\"updated\", \"in_reply_to\", \"created\" "
            + "FROM \"items\" "
            + "JOIN \"threads\" ON \"threads\".\"thread_id\"=\"items\".\"thread_id\" "
            + "JOIN \"nodes\" on \"nodes\".\"node_id\" = \"threads\".\"node_id\" "
            + " WHERE \"node\" = ? " + "AND \"item_id\" = ? "
            + "AND \"items\".\"updated\" > ? ORDER BY \"items\".\"updated\" DESC";

    private static final String SELECT_COUNT_ITEM_REPLIES = "" + "SELECT COUNT(*) "
            + "FROM \"items\" "
            + "JOIN \"threads\" ON \"threads\".\"thread_id\" = \"items\".\"thread_id\" "
            + "JOIN \"nodes\" ON \"nodes\".\"node_id\" = \"threads\".\"node_id\" "
            + " WHERE \"node\" = ? AND \"item_id\" = ? "
            + " AND \"in_reply_to\" IS NOT NULL ";

    private static final String SELECT_COUNT_ITEM_THREAD = "" + "SELECT COUNT(*) "
            + "FROM \"items\""
            + "JOIN \"threads\" ON \"threads\".\"thread_id\" = \"items\".\"thread_id\" "
            + "JOIN \"nodes\" ON \"nodes\".\"node_id\" = \"threads\".\"node_id\" "
            + " WHERE \"node\" = ? AND \"item_id\" = ?";

    private static final String SELECT_LOCAL_NODES =
        "SELECT \"nodes\".\"node\", \"config\".\"value\" AS \"value\" " +
        "FROM \"nodes\" " +
        "LEFT JOIN \"node_config\" AS \"config\" " +
        "ON \"config\".\"node_id\" = \"nodes\".\"node_id\" AND " +
        "\"config\".\"key\" = 'buddycloud#advertise_node' " +
        "WHERE \"nodes\".\"node\" ~ ? AND " +
        "(\"value\" = 'true' OR \"value\" IS NULL);";

    private static final String SELECT_REMOTE_NODES =
            "SELECT \"nodes\".\"node\", \"config\".\"value\" AS \"value\" " +
            "FROM \"nodes\" " +
            "LEFT JOIN \"node_config\" AS \"config\" " +
            "ON \"config\".\"node_id\" = \"nodes\".\"node_id\" AND " +
            "\"config\".\"key\" = 'buddycloud#advertise_node' " +
            "WHERE \"nodes\".\"node\" !~ ? AND " +
            "(\"value\" = 'true' OR \"value\" IS NULL);";

    private static final String SELECT_ITEMS_FROM_LOCAL_NODES_BEFORE_DATE =
            "SELECT \"nodes\".\"node\", \"id\", \"items\".\"updated\", \"xml\", \"in_reply_to\", \"created\" " +
            "FROM \"items\" " +
            "JOIN \"node_config\" ON \"items\".\"node_id\" = \"node_config\".\"node_id\" " +
            "JOIN \"nodes\" ON \"nodes\".\"node_id\" = \"items\".\"node_id\" " +
            "WHERE \"items\".\"updated\" < ? " +
            "AND \"key\" = ? " +
            "AND ((" +
              "NOT ? AND " +
                "(\"value\" LIKE ?) OR " +
                "(\"value\" LIKE ? AND \"nodes\".\"node\" ~ ?)) " +
            "OR ?) " +
            "AND \"nodes\".\"node\" ~ ? " +
            "ORDER BY \"updated\" DESC, \"id\" ASC LIMIT ?";

    private static final String COUNT_SUBSCRIPTIONS_FOR_NODE = "SELECT COUNT(*) "
            + "FROM \"subscriptions\" "
            + "JOIN \"affiliations\" ON \"affiliations\".\"node_id\" = \"subscriptions\".\"node_id\" "
            + "AND \"affiliations\".\"user\" = \"subscriptions\".\"user\" "
            + "AND   \"affiliations\".\"affiliation\" != 'outcast' "
            + "JOIN \"nodes\" ON \"nodes\".\"node_id\" = \"subscriptions\".\"node_id\" "
            + "WHERE \"node\" = ?";

    private static final String COUNT_ITEMS_FROM_LOCAL_NODES =
            "SELECT COUNT(*) " +
            "FROM \"items\" " +
            "JOIN \"node_config\" ON \"items\".\"node_id\" = \"node_config\".\"node_id\" " +
            "JOIN \"nodes\" ON \"items\".\"node_id\" = \"nodes\".\"node_id\" " +
            "WHERE \"key\" = ? " +
            "AND ((" +
              "NOT ? AND " +
                "(\"value\" LIKE ?) OR " +
                "(\"value\" LIKE ? AND \"nodes\".\"node\" ~ ?)) " +
            "OR ?) " +
            "AND \"nodes\".\"node\" ~ ?";

    private static final String COUNT_SUBSCRIPTIONS_TO_NODE_FOR_OWNER = "SELECT COUNT(*) "
            + "FROM \"subscriptions\" "
            + "JOIN \"nodes\" ON \"nodes\".\"node_id\" = \"subscriptions\".\"node_id\" "
            + "WHERE \"nodes\".\"node\" = ?;";

    private static final String COUNT_ITEMS_FOR_JID = "SELECT COUNT(*)" + " FROM \"subscriptions\" WHERE \"user\" = ?";

    private static final String INSERT_THREAD = "INSERT INTO \"threads\" "
            + "(\"node_id\", \"updated\", \"item_id\") "
            + "SELECT \"node_id\", ?, ? FROM \"nodes\" WHERE \"node\" = ?";
    private static final String INSERT_ITEM = "INSERT INTO \"items\" ( \"node_id\", \"thread_id\", \"id\", \"updated\", \"xml\", \"in_reply_to\", \"created\") "
            + " SELECT \"nodes\".\"node_id\", \"thread_id\", ?, ?, ?, ?, NOW()"
            + " FROM \"nodes\" JOIN \"threads\" ON \"nodes\".\"node_id\" = \"threads\".\"node_id\""
            + " WHERE \"threads\".\"item_id\" = ?"
            + " AND \"nodes\".\"node\" = ?";

    private static final String UPDATE_ITEM = "UPDATE \"items\" SET \"updated\" = ?, \"xml\" = ?"
            + " WHERE \"node_id\" = (SELECT \"node_id\" FROM \"nodes\" WHERE \"node\" = ?)"
            + " AND \"id\" = ?";

    private static final String UPDATE_THREAD = ""
            + "UPDATE \"threads\" SET \"updated\"= ?"
            + " WHERE \"node_id\" = (SELECT \"node_id\" FROM \"nodes\" WHERE \"node\" = ?)"
            + " AND \"item_id\" = ?";

    private static final String DELETE_ITEM = "DELETE FROM \"items\""
            + " WHERE \"node_id\" = (SELECT \"node_id\" FROM \"nodes\" WHERE \"node\" = ?)"
            + " AND \"id\" = ?";

    private static final String SELECT_SUBSCRIPTION_LISTENERS_FOR_NODE =
            "SELECT DISTINCT ON (\"listener\") \"listener\", \"node\", \"subscription\", \"updated\""
                    + " FROM \"subscriptions\" WHERE \"node\" = ? AND \"subscription\" = 'subscribed' ORDER BY \"listener\", \"updated\"";

    private static final String SELECT_SUBSCRIPTION_LISTENERS = "SELECT DISTINCT ON (\"listener\") \"listener\", \"node\", \"subscription\", \"updated\""
            + " FROM \"subscriptions\" "
            + "JOIN \"nodes\" ON \"nodes\".\"node_id\" = \"subscriptions\".\"node_id\" "
            + "ORDER BY \"listener\", \"updated\"";

    private static final String DELETE_NODE = "DELETE FROM \"nodes\" WHERE \"node\" = ?;";

    private static final String SELECT_NODE_LIST =
        "SELECT \"nodes\".\"node\", \"config\".\"value\" AS \"value\" " +
        "FROM \"nodes\" " +
        "LEFT JOIN \"node_config\" AS \"config\" " +
        "ON \"config\".\"node_id\" = \"nodes\".\"node_id\" AND " +
        "\"config\".\"key\" = 'buddycloud#advertise_node' " +
        "WHERE (\"value\" = 'true' OR \"value\" IS NULL);";

    private static final String DELETE_ITEMS = "DELETE FROM \"items\" WHERE \"node_id\" = (SELECT \"node_id\" FROM \"nodes\" WHERE \"node\" = ?)";

    private static final String SELECT_USER_ITEMS = "SELECT \"node\", \"id\", \"updated\", \"xml\", \"in_reply_to\", \"created\""
            + " FROM \"items\" WHERE (CAST(xpath('//atom:author/atom:name/text()', xmlparse(document \"xml\"),"
            + " ARRAY[ARRAY['atom', 'http://www.w3.org/2005/Atom']]) AS TEXT[]))[1] = ?";

    private static final String DELETE_USER_ITEMS = "DELETE"
            + " FROM \"items\" WHERE (CAST(xpath('//atom:author/atom:name/text()', xmlparse(document \"xml\"),"
            + " ARRAY[ARRAY['atom', 'http://www.w3.org/2005/Atom']]) AS TEXT[]))[1] = ?";

    private static final String DELETE_USER_AFFILIATIONS = "DELETE FROM \"affiliations\" WHERE \"user\" = ?";

    private static final String DELETE_USER_SUBSCRIPTIONS = "DELETE FROM \"subscriptions\" WHERE \"user\" = ?";

/*
    private static final String SELECT_NODE_THREADS = "SELECT \"node\", \"id\", \"updated\", \"xml\", \"in_reply_to\", "
            + "\"thread_id\", \"thread_updated\", \"created\" FROM \"items\"," + "(SELECT MAX(\"updated\") AS \"thread_updated\", \"thread_id\" FROM "
            + "(SELECT \"updated\", COALESCE(\"in_reply_to\",\"id\") AS \"thread_id\" "
            + "FROM \"items\" WHERE \"node\" = ?) AS \"_items\" " + "GROUP BY \"thread_id\" " + "HAVING MAX(\"updated\") < ? "
            + "ORDER BY \"thread_updated\" DESC LIMIT ?) AS \"threads\" " + "WHERE COALESCE(\"in_reply_to\", \"id\") = \"thread_id\" "
            + "ORDER BY \"thread_updated\" DESC, \"updated\"";
            */
    private static final String SELECT_NODE_THREADS = "SELECT \"node\", \"id\", "
            + " \"items\".\"updated\", \"xml\", \"in_reply_to\", \"item_id\", "
            + " \"t\".\"updated\" AS \"thread_updated\", \"created\" FROM \"items\" "
            + " JOIN ( "
            + "  SELECT \"node\", \"thread_id\", \"item_id\", \"updated\" FROM \"threads\" "
            + "  JOIN \"nodes\" ON \"nodes\".\"node_id\" = \"threads\".\"node_id\" "
            + "  WHERE \"node\" = ? AND \"updated\" < ? "
            + "  ORDER BY \"updated\" DESC LIMIT ? "
            + " ) AS \"t\" ON \"t\".\"thread_id\" = \"items\".\"thread_id\" "
            + " ORDER BY \"thread_updated\" DESC, \"updated\"";

/*
    private static final String COUNT_NODE_THREADS = "SELECT COUNT(DISTINCT \"thread_id\") "
            + "FROM (SELECT \"node\", (CASE WHEN (\"in_reply_to\" IS NULL) THEN \"id\" ELSE \"in_reply_to\" END) AS \"thread_id\" "
            + "FROM \"items\" WHERE \"node\" = ?) AS \"_items\"";
            */
    private static final String COUNT_NODE_THREADS = "SELECT COUNT(*) FROM \"threads\" "
            + "JOIN \"nodes\" ON \"nodes\".\"node_id\" = \"threads\".\"node_id\" "
            + "WHERE\"node\" = ?";


    private static final String SELECT_USER_POST_RATING = "SELECT \"node\", \"id\", \"updated\", \"xml\" " + "FROM \"items\""
            + "JOIN \"nodes\" ON \"nodes\".\"node_id\" = \"items\".\"node_id\" "
            + " WHERE " + "\"node\" = ? "
            + "AND \"xml\" LIKE ? " + "AND \"xml\" LIKE ? " + "AND \"xml\" LIKE '%<activity:verb>rated</activity:verb>%';";

    private static final String SELECT_NODE_MEMBERSHIP = "SELECT "
            + "\"nodes\".\"node\", "
            + "\"subscriptions\".\"user\", "
            + "COALESCE(\"subscriptions\".\"listener\", \"subscriptions\".\"user\") AS \"listener\", "
            + "\"subscriptions\".\"subscription\", "
            + "COALESCE(\"affiliations\".\"affiliation\", 'none') AS \"affiliation\", "
            + "\"subscriptions\".\"invited_by\", "
            + "CASE WHEN \"affiliations\".\"updated\" > \"subscriptions\".\"updated\" "
            + "THEN \"affiliations\".\"updated\" "
            + "ELSE \"subscriptions\".\"updated\" " + "END AS \"updated\" "
            + "FROM \"nodes\" JOIN \"subscriptions\" ON \"subscriptions\".\"node_id\" = \"nodes\".\"node_id\" "
            + "LEFT JOIN \"affiliations\" ON \"affiliations\".\"node_id\" = \"subscriptions\".\"node_id\" "
            + "AND \"affiliations\".\"user\" = \"subscriptions\".\"user\" "
            + "WHERE \"subscriptions\".\"user\" = ? AND \"nodes\".\"node\" = ?";

/*
    private static final String SELECT_USER_MEMBERSHIPS_FILTERED_BY_EPHEMERAL = "" + "SELECT " + "CASE WHEN \"subscriptions\".\"node\" != '' "
            + "THEN \"subscriptions\".\"node\" " + "ELSE \"affiliations\".\"node\" " + "END AS \"node\"," + "CASE WHEN \"subscriptions\".\"user\" != '' "
            + "THEN \"subscriptions\".\"user\" " + "ELSE \"affiliations\".\"user\" " + "END AS \"user\", " + "CASE "
            + "WHEN \"subscriptions\".\"listener\" != '' THEN \"subscriptions\".\"listener\" "
            + "WHEN \"subscriptions\".\"user\" != '' THEN \"subscriptions\".\"user\" " + "ELSE \"affiliations\".\"user\" " + "END AS \"listener\", "
            + "CASE WHEN \"subscriptions\".\"subscription\" != '' " + "THEN \"subscriptions\".\"subscription\" " + "ELSE 'none' "
            + "END AS \"subscription\", " + "CASE WHEN \"affiliations\".\"affiliation\" != '' " + "THEN \"affiliations\".\"affiliation\" "
            + "ELSE 'none' " + "END AS \"affiliation\", " + "\"subscriptions\".\"invited_by\" AS \"invited_by\","
            + "CASE WHEN \"affiliations\".\"updated\" > \"subscriptions\".\"updated\" " + "THEN \"affiliations\".\"updated\" "
            + "ELSE \"subscriptions\".\"updated\" " + "END AS \"updated\" " + "FROM \"subscriptions\" "
            + "LEFT JOIN \"node_config\" ON (\"node_config\".\"node\" = \"subscriptions\".\"node\" AND \"node_config\".\"key\" = 'buddycloud#ephemeral') "

            + "LEFT JOIN \"affiliations\" "
            + "ON \"subscriptions\".\"node\" = \"affiliations\".\"node\" AND \"affiliations\".\"user\" = \"subscriptions\".\"user\" " + "WHERE "
            + "(\"subscriptions\".\"user\" = ?) "
            + "AND (\"node_config\".\"value\" %equals%)"
            + "ORDER BY \"updated\" DESC; "; */
    private static final String SELECT_USER_MEMBERSHIPS_FILTERED_BY_EPHEMERAL = "SELECT "
            + "\"nodes\".\"node\", "
            + "\"subscriptions\".\"user\", "
            + "COALESCE(\"subscriptions\".\"listener\", \"subscriptions\".\"user\") AS \"listener\", "
            + "\"subscriptions\".\"subscription\", "
            + "COALESCE(\"affiliations\".\"affiliation\", 'none') AS \"affiliation\", "
            + "\"subscriptions\".\"invited_by\", "
            + "CASE WHEN \"affiliations\".\"updated\" > \"subscriptions\".\"updated\" "
            + "THEN \"affiliations\".\"updated\" "
            + "ELSE \"subscriptions\".\"updated\" " + "END AS \"updated\" "
            + "FROM \"nodes\" JOIN \"subscriptions\" ON \"subscriptions\".\"node_id\" = \"nodes\".\"node_id\" "
            + "LEFT JOIN \"affiliations\" ON \"affiliations\".\"node_id\" = \"subscriptions\".\"node_id\" "
            + "AND \"affiliations\".\"user\" = \"subscriptions\".\"user\" "
            + "LEFT JOIN \"node_config\" ON \"node_config\".\"node_id\"=\"nodes\".\"node_id\" "
            + " AND \"node_config\".\"key\" = 'buddycloud#ephemeral' "
            + "WHERE \"subscriptions\".\"user\" = ? "
            + "AND COALESCE(\"value\", 'false') = ?"
            + "AND COALESCE(\"affiliations\".\"affiliation\",'none') != 'outcast'";

/*
    private static final String SELECT_USER_MEMBERSHIPS = "" + "SELECT " + "CASE WHEN \"subscriptions\".\"node\" != '' "
            + "THEN \"subscriptions\".\"node\" " + "ELSE \"affiliations\".\"node\" " + "END AS \"node\"," + "CASE WHEN \"subscriptions\".\"user\" != '' "
            + "THEN \"subscriptions\".\"user\" " + "ELSE \"affiliations\".\"user\" " + "END AS \"user\", " + "CASE "
            + "WHEN \"subscriptions\".\"listener\" != '' THEN \"subscriptions\".\"listener\" "
            + "WHEN \"subscriptions\".\"user\" != '' THEN \"subscriptions\".\"user\" " + "ELSE \"affiliations\".\"user\" " + "END AS \"listener\", "
            + "CASE WHEN \"subscriptions\".\"subscription\" != '' " + "THEN \"subscriptions\".\"subscription\" " + "ELSE 'none' "
            + "END AS \"subscription\", " + "CASE WHEN \"affiliations\".\"affiliation\" != '' " + "THEN \"affiliations\".\"affiliation\" "
            + "ELSE 'none' " + "END AS \"affiliation\", " + "\"subscriptions\".\"invited_by\" AS \"invited_by\","
            + "CASE WHEN \"affiliations\".\"updated\" > \"subscriptions\".\"updated\" " + "THEN \"affiliations\".\"updated\" "
            + "ELSE \"subscriptions\".\"updated\" " + "END AS \"updated\" " + "FROM \"subscriptions\" "

            + "LEFT JOIN \"affiliations\" "
            + "ON \"subscriptions\".\"node\" = \"affiliations\".\"node\" AND \"affiliations\".\"user\" = \"subscriptions\".\"user\" " + "WHERE "
            + "(\"subscriptions\".\"user\" = ?) "
            + "ORDER BY \"updated\" DESC; ";
*/
    private static final String SELECT_USER_MEMBERSHIPS = "SELECT "
            + "\"nodes\".\"node\", "
            + "\"subscriptions\".\"user\", "
            + "COALESCE(\"subscriptions\".\"listener\", \"subscriptions\".\"user\") AS \"listener\", "
            + "\"subscriptions\".\"subscription\", "
            + "COALESCE(\"affiliations\".\"affiliation\", 'none') AS \"affiliation\", "
            + "\"subscriptions\".\"invited_by\", "
            + "CASE WHEN \"affiliations\".\"updated\" > \"subscriptions\".\"updated\" "
            + "THEN \"affiliations\".\"updated\" "
            + "ELSE \"subscriptions\".\"updated\" " + "END AS \"updated\" "
            + "FROM \"nodes\" JOIN \"subscriptions\" ON \"subscriptions\".\"node_id\" = \"nodes\".\"node_id\" "
            + "LEFT JOIN \"affiliations\" ON \"affiliations\".\"node_id\" = \"subscriptions\".\"node_id\" "
            + "AND \"affiliations\".\"user\" = \"subscriptions\".\"user\" "
            + "WHERE \"subscriptions\".\"user\" = ? "
            + "AND COALESCE(\"affiliations\".\"affiliation\",'none') != 'outcast'";

/*
    private static final String SELECT_USER_MEMBERSHIPS_WITH_CONFIGURATION = "SELECT " +
            "CASE WHEN \"subscriptions\".\"node\" != '' THEN " +
                "\"subscriptions\".\"node\" " +
            "ELSE " +
                "\"affiliations\".\"node\" " +
            "END AS \"node\"," +

            "CASE WHEN \"subscriptions\".\"user\" != '' THEN " +
                "\"subscriptions\".\"user\" " +
            "ELSE " +
                "\"affiliations\".\"user\" " +
            "END AS \"user\", " +

            "CASE WHEN \"subscriptions\".\"listener\" != '' THEN " +
                "\"subscriptions\".\"listener\" " +
            "WHEN \"subscriptions\".\"user\" != '' THEN " +
                "\"subscriptions\".\"user\" " +
            "ELSE " +
                "\"affiliations\".\"user\" " +
            "END AS \"listener\", " +

            "CASE WHEN \"subscriptions\".\"subscription\" != '' THEN " +
                "\"subscriptions\".\"subscription\" " +
            "ELSE " +
                "'none' " +
            "END AS \"subscription\", " +

            "CASE WHEN \"affiliations\".\"affiliation\" != '' THEN " +
                "\"affiliations\".\"affiliation\" " +
            "ELSE " +
                "'none' " +
            "END AS \"affiliation\", " +

            "\"subscriptions\".\"invited_by\" AS \"invited_by\", " +

            "CASE WHEN \"affiliations\".\"updated\" > \"subscriptions\".\"updated\" THEN " +
                "\"affiliations\".\"updated\" " +
            "ELSE " +
                "\"subscriptions\".\"updated\" " +
            "END AS \"updated\", " +

            "\"node_config\".\"key\" AS \"config_key\", " +

            "\"node_config\".\"value\" AS \"config_value\" " +

            "FROM (SELECT * FROM \"subscriptions\" WHERE (" +

                "SELECT COUNT(*) FROM \"node_config\" WHERE " +
                "(\"node_config\".\"key\" || ';' || \"node_config\".\"value\") IN (%subscriptionFilter%) AND " +
                "\"node_config\".\"node\" = \"subscriptions\".\"node\" " +

            ") = ? ) AS \"subscriptions\" " +

            "LEFT JOIN \"node_config\" ON \"node_config\".\"node\" = \"subscriptions\".\"node\" AND (" +
                "CASE WHEN ? != 0 THEN " +
                    "\"node_config\".\"key\" IN (%configFilter%) " +
                "ELSE " +
                    "TRUE " +
                "END) " +

            "LEFT JOIN \"affiliations\" ON \"subscriptions\".\"node\" = \"affiliations\".\"node\" AND " +
                "\"affiliations\".\"user\" = \"subscriptions\".\"user\" " +

            "WHERE (\"subscriptions\".\"user\" = ?) " +

            "ORDER BY \"updated\" DESC; ";

/*
    private static final String SELECT_NODE_MEMBERSHIPS = "" + "SELECT " + "CASE WHEN \"subscriptions\".\"node\" != '' "
            + "THEN \"subscriptions\".\"node\" " + "ELSE \"affiliations\".\"node\" " + "END AS \"node\"," + "CASE WHEN \"subscriptions\".\"user\" != '' "
            + "THEN \"subscriptions\".\"user\" " + "ELSE \"affiliations\".\"user\" " + "END AS \"user\", " + "CASE "
            + "WHEN \"subscriptions\".\"listener\" != '' THEN \"subscriptions\".\"listener\" "
            + "WHEN \"subscriptions\".\"user\" != '' THEN \"subscriptions\".\"user\" " + "ELSE \"affiliations\".\"user\" " + "END AS \"listener\", "
            + "CASE WHEN \"subscriptions\".\"subscription\" != '' " + "THEN \"subscriptions\".\"subscription\" " + "ELSE 'none' "
            + "END AS \"subscription\", " + "CASE WHEN \"affiliations\".\"affiliation\" != '' " + "THEN \"affiliations\".\"affiliation\" "
            + "ELSE 'none' " + "END AS \"affiliation\", " + "\"subscriptions\".\"invited_by\" AS \"invited_by\","
            + "CASE WHEN \"affiliations\".\"updated\" > \"subscriptions\".\"updated\" " + "THEN \"affiliations\".\"updated\" "
            + "ELSE \"subscriptions\".\"updated\" " + "END AS \"updated\" " + "FROM \"subscriptions\" " + "FULL JOIN \"affiliations\" "
            + "ON \"subscriptions\".\"node\" = \"affiliations\".\"node\" AND \"affiliations\".\"user\" = \"subscriptions\".\"user\" " + "WHERE "
            + "(\"subscriptions\".\"node\" = ?) " + "ORDER BY \"updated\" DESC; ";
            */
    private static final String SELECT_NODE_MEMBERSHIPS = "SELECT "
            + "\"nodes\".\"node\", "
            + "\"subscriptions\".\"user\", "
            + "COALESCE(\"subscriptions\".\"listener\", \"subscriptions\".\"user\") AS \"listener\", "
            + "\"subscriptions\".\"subscription\", "
            + "COALESCE(\"affiliations\".\"affiliation\", 'none') AS \"affiliation\", "
            + "\"subscriptions\".\"invited_by\", "
            + "CASE WHEN \"affiliations\".\"updated\" > \"subscriptions\".\"updated\" "
            + "THEN \"affiliations\".\"updated\" "
            + "ELSE \"subscriptions\".\"updated\" " + "END AS \"updated\" "
            + "FROM \"nodes\" JOIN \"subscriptions\" ON \"subscriptions\".\"node_id\" = \"nodes\".\"node_id\" "
            + "LEFT JOIN \"affiliations\" ON \"affiliations\".\"node_id\" = \"subscriptions\".\"node_id\" "
            + "AND \"affiliations\".\"user\" = \"subscriptions\".\"user\" "
            + "WHERE \"nodes\".\"node\" = ? ORDER BY \"updated\" DESC";

    private static final String DELETE_ONLINE_JID = "DELETE FROM \"online_users\" WHERE \"user\" = ?;";

    private static final String INSERT_ONLINE_JID = "INSERT INTO \"online_users\" (\"user\", \"updated\") VALUES (?, NOW());";

    private static final String SELECT_ONLINE_RESOURCES = "SELECT \"user\", \"updated\" " + "FROM \"online_users\" " + "WHERE \"user\" LIKE ? "
            + "ORDER BY \"updated\" DESC;";

    private static final String SELECT_USER_FEED_ITEMS = "" + "SELECT \"node\", \"id\", \"items\".\"updated\", \"xml\", \"in_reply_to\" " + "FROM \"items\" "
            + "JOIN \"nodes\" ON \"nodes\".\"node_id\" = \"items\".\"node_id\" "
            + "JOIN \"subscriptions\" ON \"subscriptions\".\"node_id\" = \"nodes\".\"node_id\" "
            + "WHERE  \"subscription\" = 'subscribed' AND \"user\" = ? AND \"items\".\"updated\" > ? "
            + "%parent%" + "%after%" + "ORDER BY \"items\".\"updated\" DESC, \"id\" DESC" + "%limit%;";

    private static final String SELECT_COUNT_USER_FEED_ITEMS = "" + "SELECT COUNT(\"id\") AS \"count\" " + "FROM \"items\" "
            + "JOIN \"nodes\" ON \"nodes\".\"node_id\" = \"items\".\"node_id\" "
            + "JOIN \"subscriptions\" ON \"subscriptions\".\"node_id\" = \"nodes\".\"node_id\" "
            + "WHERE  \"subscription\" = 'subscribed' AND \"user\" = ? AND \"items\".\"updated\" > ? "
            + "%parent%;";

    private static final String SELECT_COUNT_VALID_LOCAL_SUBSCRIPTIONS_TO_NODE = ""
        + "SELECT COUNT(*) AS \"count\" "
        + "FROM \"subscriptions\" "
        + "JOIN \"nodes\" ON \"nodes\".\"node_id\"=\"subscriptions\".\"node_id\" "
        + "WHERE \"node\" = ? AND "
        + "\"subscription\" = 'subscribed' AND "
        + "\"user\" LIKE ?;";

    @Override
    public String insertNode() {
        return INSERT_NODE;
    }

    @Override
    public String insertConf() {
        return INSERT_CONF;
    }

    @Override
    public String deleteConfFromNode() {
        return DELETE_CONF_FROM_NODE;
    }

    @Override
    public String updateNodeConf() {
        return UPDATE_CONF;
    }

    @Override
    public String selectSingleNodeConfValue() {
        return SELECT_SINGLE_NODE_CONF_VALUE;
    }

    @Override
    public String selectNodeConf() {
        return SELECT_NODE_CONF;
    }

    @Override
    public String selectAffiliation() {
        return SELECT_AFFILIATION;
    }

    @Override
    public String selectAffiliationsForUser() {
        return SELECT_AFFILIATIONS_FOR_USER;
    }

    @Override
    public String selectNodeOwners() {
        return SELECT_NODE_OWNERS;
    }

    @Override
    public String selectAffiliationChanges() {
        return SELECT_AFFILIATION_CHANGES;
    }

    @Override
    public String selectAffiliationsForUserAfterNodeId() {
        return SELECT_AFFILIATIONS_FOR_USER_AFTER_NODE_ID;
    }

    @Override
    public String countUserAffiliations() {
        return COUNT_AFFILIATIONS_FOR_USER;
    }

    @Override
    public String selectAffiliationsForNode() {
        return SELECT_AFFILIATIONS_FOR_NODE;
    }

    @Override
    public String selectAffiliationsToNodeForOwner() {
        return SELECT_AFFILIATIONS_TO_NODE_FOR_OWNER;
    }

    @Override
    public String selectAffiliationsForNodeAfterJid() {
        return SELECT_AFFILIATIONS_FOR_NODE_AFTER_JID;
    }

    @Override
    public String selectAffiliationsToNodeForOwnerAfterJid() {
        return SELECT_AFFILIATIONS_TO_NODE_FOR_OWNER_AFTER_JID;
    }

    @Override
    public String countNodeAffiliations() {
        return COUNT_AFFILIATIONS_FOR_NODE;
    }

    @Override
    public String countNodeAffiliationsForOwner() {
        return COUNT_AFFILIATIONS_TO_NODE_FOR_OWNER;
    }

    @Override
    public String insertAffiliation() {
        return INSERT_AFFILIATION;
    }

    @Override
    public String updateAffiliation() {
        return UPDATE_AFFILIATION;
    }

    @Override
    public String deleteAffiliation() {
        return DELETE_AFFILIATION;
    }

    @Override
    public String selectSubscriptionsForUser() {
        return SELECT_SUBSCRIPTIONS_FOR_USER;
    }

    @Override
    public String selectSubscriptionsForUserAfterNode() {
        return SELECT_SUBSCRIPTIONS_FOR_USER_AFTER_NODE;
    }

    @Override
    public String getSubscriptionChanges() {
        return SELECT_SUBSCRIPTION_CHANGES;
    }

    @Override
    public String selectSubscriptionsForNode() {
        return SELECT_SUBSCRIPTIONS_FOR_NODE;
    }

    @Override
    public String selectSubscriptionsToNodeForOwner() {
        return SELECT_SUBSCRIPTIONS_TO_NODE_FOR_OWNER;
    }

    @Override
    public String selectSubscriptionsForNodeAfterJid() {
        return SELECT_SUBSCRIPTIONS_FOR_NODE_AFTER_JID;
    }

    public String countSubscriptionsForJid() {
        return COUNT_ITEMS_FOR_JID;
    }

    @Override
    public String countSubscriptionsForNode() {
        return COUNT_SUBSCRIPTIONS_FOR_NODE;
    }

    @Override
    public String countSubscriptionsToNodeForOwner() {
        return COUNT_SUBSCRIPTIONS_TO_NODE_FOR_OWNER;
    }

    @Override
    public String insertSubscription() {
        return INSERT_SUBSCRIPTION;
    }

    @Override
    public String updateSubscription() {
        return UPDATE_SUBSCRIPTION;
    }

    @Override
    public String deleteSubscription() {
        return DELETE_SUBSCRIPTION;
    }

    @Override
    public String nodeExists() {
        return NODE_EXISTS;
    }

    @Override
    public String selectSingleItem() {
        return SELECT_SINGLE_ITEM;
    }

    @Override
    public String selectItemsForNode() {
        return SELECT_ITEMS_FOR_NODE;
    }

    @Override
    public String selectItemsForNodeAfterDate() {
        return SELECT_ITEMS_FOR_NODE_AFTER_DATE;
    }

    @Override
    public String selectItemsForNodeBeforeDate() {
        return SELECT_ITEMS_FOR_NODE_BEFORE_DATE;
    }

    @Override
    public String selectItemsForUsersNodesBetweenDates() {
        return SELECT_ITEMS_FOR_USER_BETWEEN_DATES;
    }

    @Override
    public String selectItemReplies() {
        return SELECT_ITEM_REPLIES;
    }

    @Override
    public String selectCountItemReplies() {
        return SELECT_COUNT_ITEM_REPLIES;
    }

    @Override
    public String selectItemThread() {
        return SELECT_ITEM_THREAD;
    }

    @Override
    public String selectCountItemThread() {
        return SELECT_COUNT_ITEM_THREAD;
    }

    @Override
    public String countItemsForNode() {
        return COUNT_ITEMS_FOR_NODE;
    }

    @Override
    public String insertThread() {
        return INSERT_THREAD;
    }

    @Override
    public String insertItem() {
        return INSERT_ITEM;
    }

    @Override
    public String updateItem() {
        return UPDATE_ITEM;
    }

    @Override
    public String deleteItem() {
        return DELETE_ITEM;
    }

    @Override
    public String updateThread() {
        return UPDATE_THREAD;
    }

    @Override
    public String selectSubscriptionListenersForNode() {
        return SELECT_SUBSCRIPTION_LISTENERS_FOR_NODE;
    }

    @Override
    public String selectSubscriptionListeners() {
        return SELECT_SUBSCRIPTION_LISTENERS;
    }

    @Override
    public String deleteNode() {
        return DELETE_NODE;
    }

    @Override
    public String deleteItems() {
        return DELETE_ITEMS;
    }

    @Override
    public String selectNodeList() {
        return SELECT_NODE_LIST;
    }

    @Override
    public String selectRecentItemParts() {
        return SELECT_RECENT_ITEM_PARTS;
    }

    @Override
    public String selectCountRecentItemParts() {
        return SELECT_COUNT_RECENT_ITEM_PARTS;
    }

    @Override
    public String selectItemsForLocalNodesBeforeDate() {
        return SELECT_ITEMS_FROM_LOCAL_NODES_BEFORE_DATE;
    }

    @Override
    public String selectUserRatingsForAPost() {
        return SELECT_USER_POST_RATING;
    }

    @Override
    public String countItemsForLocalNodes() {
        return COUNT_ITEMS_FROM_LOCAL_NODES;
    }

    @Override
    public String getUserItems() {
        return SELECT_USER_ITEMS;
    }

    @Override
    public String deleteUserItems() {
        return DELETE_USER_ITEMS;
    }

    @Override
    public String deleteUserAffiliations() {
        return DELETE_USER_AFFILIATIONS;
    }

    @Override
    public String selectMembership() {
        return SELECT_NODE_MEMBERSHIP;
    }

    @Override
    public String selectUserMembershipsFilteredByEphemeral() {
        return SELECT_USER_MEMBERSHIPS_FILTERED_BY_EPHEMERAL;
    }

    @Override
    public String selectUserMemberships() {
        return SELECT_USER_MEMBERSHIPS;
    }

    @Override
    public String selectNodeMemberships() {
        return SELECT_NODE_MEMBERSHIPS;
    }

    @Override
    public String deleteUserSubscriptions() {
        return DELETE_USER_SUBSCRIPTIONS;
    }

    @Override
    public String selectNodeThreads() {
        return SELECT_NODE_THREADS;
    }

    @Override
    public String countNodeThreads() {
        return COUNT_NODE_THREADS;
    }

    @Override
    public String deleteOnlineJid() {
        return DELETE_ONLINE_JID;
    }

    @Override
    public String selectOnlineResources() {
        return SELECT_ONLINE_RESOURCES;
    }

    @Override
    public String addOnlineJid() {
        return INSERT_ONLINE_JID;
    }

    public String selectUserFeedItems() {
        return SELECT_USER_FEED_ITEMS;
    }

    @Override
    public String selectCountUserFeedItems() {
        return SELECT_COUNT_USER_FEED_ITEMS;
    }

    @Override
    public String selectRemoteNodes() {
        return SELECT_REMOTE_NODES;
    }

    @Override
    public String selectLocalNodes() {
        return SELECT_LOCAL_NODES;
    }

    @Override
    public String countLocalValidSubscriptionsForNode() {
      return SELECT_COUNT_VALID_LOCAL_SUBSCRIPTIONS_TO_NODE;
    }

}
