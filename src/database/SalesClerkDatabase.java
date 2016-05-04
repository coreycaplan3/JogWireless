package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static database.ColumnTypes.*;

/**
 * Created by coreycaplan on 4/24/16.
 * <p></p>
 * A class representing the transactions that a sales clerk or manager may have.
 */
public class SalesClerkDatabase {

    private DatabaseApi databaseApi;

    public SalesClerkDatabase() {
        databaseApi = DatabaseApi.getInstance();
    }

    public Object[][] getPhoneModels() {
        String query = "SELECT\n" +
                "  PHONE_ID,\n" +
                "  MANUFACTURER,\n" +
                "  MODEL\n" +
                "FROM PHONE_MODEL";
        try {
            ResultSet resultSet = databaseApi.executeQuery(query);
            ArrayList<String> columnNames = ResultSetHelper.makeColumnNames("PHONE_ID", "MANUFACTURER", "MODEL");
            ArrayList<ColumnTypes> columnTypes = ResultSetHelper.makeColumnTypes(INTEGER, STRING, STRING);

            ResultSetHelper resultSetHelper = new ResultSetHelper(resultSet, columnNames, columnTypes);
            return resultSetHelper.printResults(50);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            databaseApi.logout();
        }
    }

    /**
     * Gets the inventory for a given phone model at a given store.
     *
     * @param storeNumber The store number at which inventory should be queried.
     * @param phoneId     The ID of the phone that should be checked.
     */
    public void getInventory(int storeNumber, int phoneId) {
        String query = "SELECT\n" +
                "  MODEL,\n" +
                "  MANUFACTURER,\n" +
                "  QUANTITY\n" +
                "FROM STOCKS\n" +
                "NATURAL JOIN PHONE_MODEL\n" +
                "WHERE STORE_NUMBER = " + storeNumber + "\n" +
                "      AND PHONE_ID = " + phoneId + "\n" +
                "ORDER BY PHONE_ID ASC";
        try {

            ResultSet resultSet = databaseApi.executeQuery(query);
            if (!ResultSetHelper.isResultSetValid(resultSet, "No inventory was found!")) {
                return;
            }
            resultSet.next();
            String column1 = "MODEL";
            String column2 = "MANUFACTURER";
            String column3 = "QUANTITY";
            System.out.printf("%-50s %-50s %-50s\n", column1, column2, column3);
            System.out.printf("%-50s %-50s %-50d\n", resultSet.getString(column1), resultSet.getString(column2),
                    resultSet.getInt(column3));
            System.out.println();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Oops an error occurred querying the database.");
        } finally {
            databaseApi.logout();
        }
    }

    /**
     * Buys more inventory for the given store number.
     *
     * @param storeNumber The store number that needs more inventory.
     * @param phoneId     The ID of the phone that needs to be ordered.
     * @param quantity    The quantity of the phone that should be replenished.
     */
    public void buyMoreInventory(int storeNumber, int phoneId, int quantity) {
        String procedure = "{CALL BUY_MORE_INVENTORY(" + storeNumber + ", " + phoneId + ", " + quantity + ")}";
        try {
            databaseApi.executeProcedure(procedure);
            System.out.println("Successfully refilled store number " + storeNumber + "\'s inventory!");
            System.out.println();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("There was an issue refilling store number " + storeNumber + "\'s inventory!");
        } finally {
            databaseApi.logout();
        }
    }


}
