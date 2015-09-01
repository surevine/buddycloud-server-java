package org.buddycloud.channelserver.db.jdbc;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class DatabaseTester {

    public class Assertions {
        private DatabaseTester tester;

        protected Assertions(final DatabaseTester tester) {
            this.tester = tester;
        }

        /**
         * Asserts that the table contains exactly one row with field=>value pairs
         * matching the given map.
         * @param tableName the table to interrogate.
         * @param values a map of field=>value of the expected values
         * @throws SQLException
         */
        public void assertTableContains(final String tableName, final Map<String, Object> values, final String nodeName) throws SQLException {
            assertTableContains(tableName, values, nodeName, 1);
        }
        public void assertTableContains(final String tableName, final Map<String, Object> values) throws SQLException {
          assertTableContains(tableName, values, null, 1);
        }

        /**
         * Asserts that the table contains the specified number of rows with field=>value pairs
         * matching the given map.
         * @param tableName the table to interrogate.
         * @param values a map of field=>value of the expected values
         * @param expectedRows the number of rows we expect to match exactly
         * @throws SQLException
         */
        public void assertTableContains(final String tableName, final Map<String, Object> values, final String nodeName, final int expectedRows) throws SQLException {
            Connection conn = tester.getConnection();

            // We will rebuild the values as a list so we can have guaranteed ordering
            List<Object> valueList = new ArrayList<Object>();

            StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM \"");
            sql.append(tableName);
            sql.append("\" ");
            if (nodeName != null) {
              sql.append(" JOIN \"nodes\" ON \"nodes\".\"node_id\" = \"");
              sql.append(tableName);
              sql.append("\".\"node_id\" ");
              sql.append("WHERE \"nodes\".\"node\" = ?");
            } else {
              sql.append("WHERE TRUE");
            }

            for (Entry<String, Object> field : values.entrySet()) {
                valueList.add(field.getValue());
                sql.append(" AND \"");
                sql.append(field.getKey());
                sql.append("\" = ?");
            }

            sql.append(";");

            PreparedStatement stmt = conn.prepareStatement(sql.toString());
            int offset = 1;
            if (nodeName != null) {
              stmt.setString(1, nodeName);
              offset = 2;
            }
            for (int i = 0; i < valueList.size(); ++i) {
                stmt.setObject(i + offset, valueList.get(i));
            }

            ResultSet rs = stmt.executeQuery();

            rs.next();
            assertEquals("Expected query " + sql.toString() + " to return " + expectedRows, expectedRows, rs.getInt(1));
        }
        public void assertTableContains(final String tableName, final Map<String, Object> values, final int expectedRows) throws SQLException {
          assertTableContains(tableName, values, null, expectedRows);
       }
    }

    private Connection conn;

    public DatabaseTester() throws SQLException, IOException, ClassNotFoundException {
        Class.forName("org.hsqldb.jdbcDriver");
        Class.forName("net.sf.log4jdbc.DriverSpy");
        createSchema(getConnection());
    }

    public void initialise() throws SQLException, IOException {
        close();
        createSchema(getConnection());
    }

    public void close() throws SQLException {
        if (conn != null) {
            executeDDL(conn, "SHUTDOWN");
            conn = null;
        }
    }

    public Connection getConnection() throws SQLException {
        if (conn == null) {
            final Connection originalConn = DriverManager.getConnection("jdbc:log4jdbc:hsqldb:mem:test", "sa", "");
            conn = Mockito.spy(originalConn);
            Mockito.doAnswer(new Answer<PreparedStatement>() {
                @Override
                public PreparedStatement answer(InvocationOnMock invocation)
                        throws Throwable {
                    String originalSQL = (String) invocation.getArguments()[0];
                    String replacedSQL = originalSQL.replaceAll("(\\S+) ~ \\?", "regexp_matches($1, ?)");
                    replacedSQL = replacedSQL.replaceAll("(\\S+) !~ \\?", "regexp_matches($1, ?) = FALSE");
                    return originalConn.prepareStatement(replacedSQL);
                }
            }).when(conn).prepareStatement(Mockito.anyString());
            executeDDL(conn, "drop schema public cascade;");
        }
        return conn;
    }

    private void createSchema(final Connection conn) throws SQLException, IOException {
        executeDDL(conn, "SET DATABASE SQL SYNTAX PGS TRUE;");
        executeDDL(conn, "SET DATABASE SQL REFERENCES TRUE;");
        loadData("base");
    }

    public void loadData(final String scriptName) throws SQLException, IOException {
        URL url = getClass().getResource("/org/buddycloud/channelserver/testing/jdbc/scripts/" + scriptName + ".sql");
        runScript(conn, new InputStreamReader(url.openStream()));
    }

    private void executeDDL(final Connection conn, final String ddl)
            throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(ddl);
        stmt.close();
    }

    private void runScript(final Connection conn, final Reader script) throws SQLException, IOException {
        // Now read line bye line
        BufferedReader d = new BufferedReader(script);
        String thisLine, sqlQuery;
        Statement stmt = conn.createStatement();
        sqlQuery = "";
        while ((thisLine = d.readLine()) != null) {
            // Skip comments and empty lines
            if (thisLine.length() > 0 && thisLine.charAt(0) == '-'
                    || thisLine.length() == 0) {
              continue;
            }
            sqlQuery = sqlQuery + " " + thisLine;
            // If one command complete
            if (sqlQuery.charAt(sqlQuery.length() - 1) == ';') {
                sqlQuery = sqlQuery.replace(';', ' '); // Remove the ; since
                                                        // jdbc complains
                    stmt.execute(sqlQuery);
                sqlQuery = "";
            }
        }
    }

    public Assertions assertions() {
        return new Assertions(this);
    }
}
