package database;

import java.sql.ResultSet;
import java.sql.SQLException;

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

    /**
     * Gets the inventory for a given phone model at a given store.
     *
     * @param storeNumber The store number at which inventory should be queried.
     * @param phoneId     The ID of the phone that should be checked.
     */
    public void getInventory(int storeNumber, int phoneId) {
        String query = "SELECT count(*) \n" +
                "FROM STOCKS\n" +
                "  NATURAL JOIN PHONE_PRODUCT\n" +
                "WHERE STORE_NUMBER = " + storeNumber + "\n" +
                "      AND PHONE_ID = " + phoneId;
        try {
            ResultSet resultSet = databaseApi.executeQuery(query);
            System.out.printf("Total Inventory for phone ID %d: ", phoneId);
            System.out.println(resultSet.getInt(0));
            System.out.println();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Oops an error occurred querying the database.");
        }
    }

    public void buyMoreInventory(int storeNumber, int phoneId) {

    }

}
