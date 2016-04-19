package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class NewCustomerDatabase {

    private DatabaseApi databaseApi;

    private ArrayList<Integer> customerIds;

    public NewCustomerDatabase() {
        databaseApi = DatabaseApi.getInstance();
    }

    /**
     * Prints all of the IDs that match the given name.
     *
     * @param name The name for which to search.
     * @return True if the database retrieval was successful or false if it was not.
     */
    public boolean getCustomerIdsForName(String name) {
        if (name.length() > 2) {
            name = name.substring(1, name.length() - 1);
        }
        String sql = "SELECT * FROM CUSTOMER WHERE NAME LIKE \'%" + name + "%\' ORDER BY NAME";
        try {
            ResultSet resultSet = databaseApi.executeQuery(sql);
            if (!ResultSetHelper.isResultSetValid(resultSet)) {
                return false;
            } else {
                List<String> columnNames = new ArrayList<>();
                columnNames.add(TableConstants.Customer.ID);
                columnNames.add(TableConstants.Customer.NAME);

                List<ColumnTypes> columnTypes = new ArrayList<>();
                columnTypes.add(TableConstants.Customer.ID_TYPE);
                columnTypes.add(TableConstants.Customer.NAME_TYPE);

                ResultSetHelper resultSetHelper = new ResultSetHelper(resultSet, columnNames, columnTypes);
                Object[][] results = resultSetHelper.printResults(25);
                customerIds = new ArrayList<>();
                for (Object[] result : results) {
                    if (result != null) {
                        customerIds.add((Integer) result[0]);
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving results!");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if the entered ID matches one of the IDs returned from the previous query.
     *
     * @param id The ID that should be checked.
     * @return True if it's a valid ID or false if it's not.
     */
    public boolean isValidCustomerId(int id) {
        for (Integer customerId : customerIds) {
            if (customerId == id) {
                return true;
            }
        }
        return false;
    }

    public String getNameFromCustomerId(String customerId) {
        String query = "SELECT NAME FROM CUSTOMER WHERE C_ID = " + customerId;
        try {
            ResultSet resultSet = databaseApi.executeQuery(query);
            return resultSet.getString(TableConstants.Customer.NAME);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getAddressFromCustomerId(String customerId) {
        String query = "SELECT ADDRESS FROM CUSTOMER WHERE C_ID = " + customerId;
        try {
            ResultSet resultSet = databaseApi.executeQuery(query);
            return resultSet.getString(TableConstants.Customer.ADDRESS);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
