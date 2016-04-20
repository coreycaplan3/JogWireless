package database;

import java.sql.*;

/**
 * A class used to establish Database connections and retrieve information
 */
final class DatabaseApi {

    private static DatabaseApi sDatabaseApi;

    private Database database;

    /**
     * Should be called when the program is first started to initialize the {@link DatabaseApi} class.
     *
     * @param username The username necessary to login to <i>edgar1</i> (the CSE 341 sunlab machine).
     * @param password The password neceesary to login the corresponding username on <i>edgar1</i>.
     * @return An instance of {@link DatabaseApi}.
     * @throws IllegalStateException If this method gets called a second time.
     * @see #getInstance()
     */
    static boolean initializeInstance(String username, String password) {
        sDatabaseApi = new DatabaseApi(username, password);
        return sDatabaseApi.connectToDatabase();
    }

    /**
     * @return An instance of {@link DatabaseApi} after a call to {@link #initializeInstance(String, String)}}.
     */
    static DatabaseApi getInstance() {
        if (sDatabaseApi == null) {
            throw new IllegalStateException("The DatabaseApi should have been initialized with a call to " +
                    "initializeInstance()");
        }
        return sDatabaseApi;
    }

    void logout() {
        try {
            database.databaseConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private DatabaseApi(String username, String password) {
        database = new Database(username, password);
    }

    /**
     * Attempts to establish a connection with the database.
     *
     * @return True if the connection is successful or false if it was not.
     */
    private boolean connectToDatabase() {
        return database.establishConnection();
    }

    /**
     * Attempts to execute a query in the database.
     *
     * @param query The query that should be executed.
     * @return Always true.
     * @throws SQLException
     */
    ResultSet executeQuery(String query) throws SQLException {
        Statement statement = database.databaseConnection.createStatement();
        return statement.executeQuery(query);
    }

    /**
     * Attempts to execute a stored procedure in the database.
     *
     * @param procedure The procedure that should be executed, including its parameters.
     * @return Always false.
     * @throws SQLException
     */
    boolean executeProcedure(String procedure) throws SQLException {
        CallableStatement statement = database.databaseConnection.prepareCall(procedure);
        return statement.execute();
    }

    /**
     * A static inner class to protect the fragility of database access and reduce the ability of ouutside classes
     * from having direct access to this class's content.
     */
    private static class Database {

        private static final String CSE_URL = "jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241";

        private final String username;
        private final String password;

        private Connection databaseConnection;

        private Database(String username, String password) {
            this.username = username;
            this.password = password;
        }

        private boolean establishConnection() {
            try {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                databaseConnection = DriverManager.getConnection(CSE_URL, username, password);
                return true;
            } catch (SQLException e) {
//                e.printStackTrace();
                return false;
            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
                return false;
            }
        }

    }

}