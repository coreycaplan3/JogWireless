package database;

import validation.FormValidation;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * An <i>abstract</i> class used for passing on certain methods to the classes that extend it.
 */
abstract class AbstractCustomerDatabase {

    private String customerId;
    private DatabaseApi databaseApi;

    AbstractCustomerDatabase() {
        databaseApi = DatabaseApi.getInstance();
    }

    String getCustomerId() {
        return customerId;
    }

    DatabaseApi getDatabaseApi() {
        return databaseApi;
    }

    /**
     * Shows the accounts billing information for the given billing period.
     *
     * @param accountId     The account ID of the account whose information should be retrieved.
     * @param billingPeriod The billing period of the bill that should be retrieved. Should be in the format
     *                      "yyyy-MM-dd HH:mm:ss".
     */
    public void showBillingCharges(int accountId, String billingPeriod) {
        try {
            String query = "SELECT A_ID, BILL_PERIOD, IS_PAID, PLAN, ACCUMULATED_CHARGES\n" +
                    "FROM BILL\n" +
                    "WHERE A_ID = " + accountId + " AND BILL_PERIOD = to_date(\'" + billingPeriod + "\', " +
                    "'YYYY-MM-DD HH24:MI:SS')";
            ResultSet resultSet = databaseApi.executeQuery(query);
            if (ResultSetHelper.isResultSetValid(resultSet)) {
                System.out.printf("%-15s %-20s %-15s %-50s %-20s\n", "Account ID", "Bill Period", "Paid Yet?", "Plan",
                        "Accumulated Charges");
                resultSet.next();
                Date date = resultSet.getDate(TableConstants.Bill.BILL_PERIOD);
                String formattedDate = String.format(Locale.US, "%tB, %tY", date, date);
                String isPaid = String.valueOf(resultSet.getInt(TableConstants.Bill.IS_PAID) == 1);
                if (isPaid.charAt(0) == 't') {
                    isPaid = "True";
                } else {
                    isPaid = "False";
                }
                System.out.printf("%-15s %-20s %-15s %-50s %.2f\n", resultSet.getInt(TableConstants.Bill.A_ID), formattedDate, isPaid,
                        resultSet.getString(TableConstants.Bill.PLAN), resultSet.getDouble(TableConstants.Bill.ACCUMULATED_CHARGES));
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println("There was an error executing your transaction.");
        }
    }

    /**
     * @param desiredPlan The plan to which the user would like to switch.
     * @param accountId   The account ID of the account whose plan should be changed.
     * @return True if the change processed successfully or false if it did not.
     */
    public boolean changePlan(String desiredPlan, int accountId) {
        String query = "update ACCOUNT\n" +
                "set CURRENT_PLAN = \'" + desiredPlan + "\'\n" +
                "where A_ID = " + accountId;
        try {
            databaseApi.executeQuery(query);
            return true;
        } catch (SQLException e) {
            System.out.println("There was an error processing your request.");
            return false;
        }
    }

    /**
     * Retrieves the accounts for which the given customer ID is the owner.
     *
     * @param customerId The customer's ID.
     * @return A 2d array of Strings where the 0 column is the account ID, the 1st column is the primary number,
     * and the 2nd column is the current plan. Can be <b>null</b> if there are no accounts that the given customer owns
     * or there was an error processing the transaction.
     */
    public String[][] getAccountsWhereCustomerIsOwner(int customerId) {
        String query = "SELECT\n" +
                "  A_ID,\n" +
                "  PRIMARY_NUMBER,\n" +
                "  CURRENT_PLAN\n" +
                "FROM SUBSCRIBES\n" +
                "  NATURAL JOIN ACCOUNT\n" +
                "WHERE C_ID = " + customerId +
                "      AND IS_OWNER = 1";
        try {
            ResultSet resultSet = databaseApi.executeQuery(query);
            if (!ResultSetHelper.isResultSetValid(resultSet)) {
                return null;
            }

            ArrayList<String> columnNames = ResultSetHelper.makeColumnNames(TableConstants.Account.A_ID, TableConstants.Account.PRIMARY_NUMBER,
                    TableConstants.Account.CURRENT_PLAN);
            ArrayList<ColumnTypes> columnTypes = ResultSetHelper.makeColumnTypes(TableConstants.Account.A_ID_TYPE,
                    TableConstants.Account.PRIMARY_NUMBER_TYPE, TableConstants.Account.CURRENT_PLAN_TYPE);
            ResultSetHelper resultSetHelper = new ResultSetHelper(resultSet, columnNames, columnTypes);
            Object[][] objects = resultSetHelper.printResults(20);

            String[][] accounts = new String[objects.length][objects[0].length];
            for (int i = 0; i < objects.length; i++) {
                for (int j = 0; j < objects[i].length; j++) {
                    accounts[i][j] = objects[i][j] + "";
                }
            }
            return accounts;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("Duplicates")
    private int pickNewPhone() {
        Object[][] phonesForSale = getPhoneModelsForSale();
        while (true) {
            int desiredPhone = FormValidation.getNumericInput("Please enter the Phone ID of the phone you would like to buy:");
            if (desiredPhone >= 1 && desiredPhone <= phonesForSale.length) {
                return desiredPhone;
            } else {
                System.out.println("Please enter a valid phone choice.");
            }
        }
    }

    /**
     * @param customerId The ID of the customer whose address should be retrieved.
     * @return The address of the given customer or <b>null</b> if the customer ID doesn't exist or there was an
     * error performing the transaction.
     */
    public String getAddressFromCustomerId(int customerId) {
        String query = "SELECT ADDRESS FROM CUSTOMER WHERE C_ID = " + customerId;
        try {
            ResultSet resultSet = databaseApi.executeQuery(query);
            return resultSet.getString(0);
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * @param customerId The ID of the customer whose name should be retrieved.
     * @return The customer's name if the transaction was successful or <b>null</b> if the transaction failed or the
     * customer ID doesn't exist.
     */
    public String getNameFromCustomerId(int customerId) {
        String query = "SELECT NAME FROM CUSTOMER WHERE C_ID = " + customerId;
        try {
            ResultSet resultSet = databaseApi.executeQuery(query);
            return resultSet.getString(0);
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Gets all of the accounts that the customer owns.
     *
     * @param customerId The id of the customer whose accounts should be retrieved.
     * @return The result set in the form of a 2d array of objects.
     */
    public Object[][] getCustomerAccounts(String customerId) {
        String query = "SELECT\n" +
                "  A_ID,\n" +
                "  NAME,\n" +
                "  PRIMARY_NUMBER\n" +
                "FROM account\n" +
                "  NATURAL JOIN SUBSCRIBES\n" +
                "  NATURAL JOIN CUSTOMER\n" +
                "WHERE IS_OWNER = 1 AND c_id = " + customerId;
        try {
            ResultSet resultSet = databaseApi.executeQuery(query);
            if (!ResultSetHelper.isResultSetValid(resultSet)) {
                System.out.println("Sorry, you do not own any accounts, so you can\'t add any new customers.");
                return null;
            } else {
                List<String> columnNames = new ArrayList<>();
                columnNames.add(TableConstants.Account.A_ID);
                columnNames.add(TableConstants.Customer.NAME);
                columnNames.add(TableConstants.Account.PRIMARY_NUMBER);

                List<ColumnTypes> columnTypes = new ArrayList<>();
                columnTypes.add(TableConstants.Account.A_ID_TYPE);
                columnTypes.add(TableConstants.Customer.NAME_TYPE);
                columnTypes.add(TableConstants.Account.PRIMARY_NUMBER_TYPE);

                ResultSetHelper resultSetHelper = new ResultSetHelper(resultSet, columnNames, columnTypes);
                return resultSetHelper.printResults(30);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Adds a customer to the given account.
     *
     * @param customerId      The customer's ID if it's an existing customer or <b>-1</b> if the customer is new.
     * @param customerName    The customer's name or <b>null</b> if the customer already exists.
     * @param customerAddress The customer's address or <b>null</b> if the customer already exists.
     * @param desiredPhone    The phone type that the user would like to purchase.
     * @param accountId       The account ID of the account to which the user would like to be added.
     * @param storeNumber     The store number from which the user is performing this transaction.
     * @return True if the transaction occurred successfully or false if there was an error.
     */
    public boolean addCustomerToAccount(int customerId, String customerName, String customerAddress, int desiredPhone,
                                        int accountId, int storeNumber) {
        String query = "{call ADD_CUSTOMER_TO_ACCOUNT(" + customerId + ", \'" + customerName + "\', \'" +
                customerAddress + "\', " + desiredPhone + ", " + accountId + ", " + storeNumber + ")}";
        try {
            databaseApi.executeProcedure(query);
            return true;
        } catch (SQLException e) {
            if (e.getErrorCode() == -20000) {
                System.out.println("There are no more valid phone numbers! Jog needs to buy more numbers!");
            } else if (e.getErrorCode() == -20001) {
                System.out.println("Jog is out of stock of that model phone. Pick a different phone!");
                desiredPhone = pickNewPhone();
                return addCustomerToAccount(customerId, customerName, customerAddress, desiredPhone, accountId,
                        storeNumber);
            } else {
                System.out.println("There was an error adding the customer to your account!");
            }
            return false;
        }
    }

    /**
     * Creates a new account for the user, giving him/her a new phone and phone number.
     *
     * @param name             The user's name.
     * @param address          The user's address.
     * @param desiredPhoneType The desired phone type of the user.
     * @param storeNumber      The store number from which the user is buying the phone.
     * @param customerPlan     The plan on which the customer would like to be.
     * @return True if the account creation was successful.
     */
    public boolean createAccount(String name, String address, int desiredPhoneType, int storeNumber,
                                 String customerPlan) {
        String sql = "{call CREATE_NEW_ACCOUNT(\'" + name + "\', \'" + address + "\', " + desiredPhoneType + ", " +
                storeNumber + ", \'" + customerPlan + "\')}";
        try {
            databaseApi.executeProcedure(sql);
            return true;
        } catch (SQLException e) {
            switch (e.getErrorCode()) {
                case -20000:
                    System.out.println("Error: There are no more valid phone numbers! Jog needs to buy more phone " +
                            "numbers!");
                    break;
                case -20001:
                    System.out.println("Sorry, Jog is out of stock of that phone model. Please pick a different phone!");
                    Object[][] phonesForSale = getPhoneModelsForSale();
                    while (true) {
                        int response = FormValidation.getNumericInput("Please select a phone:");
                        if (response - 1 >= phonesForSale.length || response - 1 < 0) {
                            System.out.println("Please enter a valid phone model.");
                        } else {
                            return createAccount(name, address, response - 1, storeNumber, customerPlan);
                        }
                    }
                default:
                    System.out.println("Oops: An error occurred!");
                    e.printStackTrace();
                    break;
            }
            return false;
        }
    }

    /**
     * Reports the user's phone as either lost or stolen depending upon the selected option.
     *
     * @param meid       The meid of the phone that was stolen.
     * @param reportType 0 if the phone was stolen, 1 if it was lost, or 2 if it was found
     * @return True if the transaction was successful.
     */
    private boolean reportPhone(long meid, int reportType) {
        String query;
        if (reportType == 0) {
            query = "UPDATE PHONE_PRODUCT\n" +
                    "SET P_STATUS = \'STOLEN\'\n" +
                    "WHERE MEID = " + meid;
        } else if (reportType == 1) {
            query = "UPDATE PHONE_PRODUCT\n" +
                    "SET P_STATUS = \'LOST\'\n" +
                    "WHERE MEID = " + meid;
        } else if (reportType == 2) {
            query = "UPDATE PHONE_PRODUCT\n" +
                    "SET P_STATUS = \'IN_USE\'\n" +
                    "WHERE MEID = " + meid;
        } else {
            throw new IllegalArgumentException("Invalid reportType passed! Found: " + reportType);
        }
        try {
            databaseApi.executeQuery(query);
            return true;
        } catch (SQLException e) {
            System.out.println("Error executing update!");
            return false;
        }
    }

    public boolean doesUserOwnPhone(int phoneChoice, Object[][] userPhones) {
        for (Object[] userPhone : userPhones) {
            if (((Integer) (userPhone[4]) == phoneChoice)) {
                return true;
            }
        }
        System.out.println("Please select one of your phones from the list.");
        return false;
    }

    /**
     * Gets a list of the customer's phones.
     *
     * @param customerId The customer's ID
     * @return A 2d array of objects containing the user's phones in the order of [MANUFACTURER], [MODEL],
     * [SELECTION_OPTION]. Can be <b>null</b> if the user has no phones to show.
     */
    public Object[][] getCustomerPhones(String customerId) {
        String query = "SELECT\n" +
                "  MEID,\n" +
                "  MANUFACTURER,\n" +
                "  MODEL,\n" +
                "  PHONE_NUMBER\n" +
                "FROM PHONE_NUMBER\n" +
                "  NATURAL JOIN USED_BY\n" +
                "  NATURAL JOIN PHONE_PRODUCT\n" +
                "  NATURAL JOIN PHONE_MODEL\n" +
                "WHERE C_ID = " + customerId;
        try {
            ResultSet resultSet = databaseApi.executeQuery(query);
            if (ResultSetHelper.isResultSetValid(resultSet)) {
                List<String> columnNames = new ArrayList<>();
                columnNames.add(TableConstants.PhoneProduct.MEID);
                columnNames.add(TableConstants.PhoneModel.MANUFACTURER);
                columnNames.add(TableConstants.PhoneModel.MODEL);
                columnNames.add(TableConstants.PhoneNumber.PHONE_NUMBER);

                List<ColumnTypes> columnTypes = new ArrayList<>();
                columnTypes.add(TableConstants.PhoneProduct.MEID_TYPE);
                columnTypes.add(TableConstants.PhoneModel.MANUFACTURER_TYPE);
                columnTypes.add(TableConstants.PhoneModel.MODEL_TYPE);
                columnTypes.add(TableConstants.PhoneNumber.PHONE_NUMBER_TYPE);

                ResultSetHelper helper = new ResultSetHelper(resultSet, columnNames, columnTypes);

                return helper.printResultsWithOptions(25);
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Unknown error!");
            return null;
        }
    }

    /**
     * Checks if the given user's choice matches one of the phones in the 2d array of objects.
     *
     * @param customerPhones A 2d array of objects that contains the customer's phones.
     * @param userSelection  The selection that the user made, from the <i>options</i> column.
     * @return True if the customer's selection is valid or false if it is not.
     */
    public boolean isCustomerPhoneValid(Object[][] customerPhones, int userSelection) {
        for (Object[] customerPhone : customerPhones) {
            if (((Integer) customerPhone[customerPhone.length - 1]) == userSelection) {
                return true;
            }
        }
        return false;
    }

    /**
     * Reports a given phone as stolen.
     *
     * @param phoneToReport The phone that the user would like to report.
     * @return True if the report was successful.
     */
    public boolean reportStolenPhone(Object[] phoneToReport) {
        return reportPhone((Long) phoneToReport[0], 0);
    }

    /**
     * Reports a given phone as lost.
     *
     * @param phoneToReport The phone that the user would like to report.
     * @return True if the report was successful.
     */
    public boolean reportLostPhone(Object[] phoneToReport) {
        return reportPhone((Long) phoneToReport[0], 1);
    }

    /**
     * Reports a given phone as found.
     *
     * @param phoneToReport The phone that the user would like to report.
     * @return True if the report was successful.
     */
    public boolean reportFoundPhone(Object[] phoneToReport) {
        return reportPhone((Long) phoneToReport[0], 2);
    }

    /**
     * Allows the user to buy a new phone.
     *
     * @param phoneType The type of phone that should be bought, in place of the old phone.
     * @param id        The ID of the customer who is buying a phone.
     * @return True if the purchase was successful.
     */
    public boolean replaceNewPhone(int phoneType, String id, long oldPhoneMeid, long phoneNumber, int storeNumber) {
        String sql = "{call CUSTOMER_REPLACES_NEW_PHONE(" + phoneType + ", " + id + ", " + oldPhoneMeid + ", " +
                phoneNumber + ", " + storeNumber + ")}";
        try {
            databaseApi.executeProcedure(sql);
            return true;
        } catch (SQLException e) {
            switch (e.getErrorCode()) {
                case -20000:
                    System.out.println("Error: Invalid phone entered!");
                    break;
                case -20001:
                    System.out.println("Error: There are no customers with ID " + id + " that have a phone number!");
                    break;
                case -20002:
                    System.out.println("Sorry, there are no more phones of this model in stock.");
                    System.out.println("Jog is a slow moving company and it will be a while before new phones come " +
                            "back in stock.");
                    break;
                default:
                    System.out.println("Oops: An error occurred!");
                    e.printStackTrace();
                    break;
            }
            return false;
        }
    }

    /**
     * Checks if the phoneType the user entered is valid.
     *
     * @param phoneId The ID of the phone the user entered.
     * @return True if it's valid.
     */
    public boolean isPhoneStocked(int phoneId, Object[][] phonesForSale) {
        for (Object[] aPhonesForSale : phonesForSale) {
            if (((Integer) aPhonesForSale[0]) == phoneId) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return The phone models for sale by Jog or <b>NULL</b> if an error occurs during retrieval.
     */
    public Object[][] getPhoneModelsForSale() {
        try {
            String query = "SELECT * FROM PHONE_MODEL";
            ResultSet resultSet = databaseApi.executeQuery(query);

            List<ColumnTypes> columnTypes = new ArrayList<>();
            columnTypes.add(TableConstants.PhoneModel.PHONE_ID_TYPE);
            columnTypes.add(TableConstants.PhoneModel.MANUFACTURER_TYPE);
            columnTypes.add(TableConstants.PhoneModel.MODEL_TYPE);

            List<String> columnNames = new ArrayList<>();
            columnNames.add(TableConstants.PhoneModel.PHONE_ID);
            columnNames.add(TableConstants.PhoneModel.MANUFACTURER);
            columnNames.add(TableConstants.PhoneModel.MODEL);

            ResultSetHelper helper = new ResultSetHelper(resultSet, columnNames, columnTypes);
            return helper.printResults(20);
        } catch (SQLException e) {
            System.out.println("Unknown error getting phone models!");
            return null;
        }
    }

    /**
     * Prints all of the IDs that match the given name.
     *
     * @param name The name for which to search.
     * @return An array list of customer IDs or <b>null</b> if no customers were found or an error occurred during the
     * transaction.
     */
    public ArrayList<Integer> getCustomerIdsForName(String name) {
        if (name.length() > 2) {
            name = name.substring(1, name.length() - 1);
        }
        String sql = "SELECT * FROM CUSTOMER WHERE NAME LIKE \'%" + name + "%\' ORDER BY NAME";
        try {
            ResultSet resultSet = databaseApi.executeQuery(sql);
            if (!ResultSetHelper.isResultSetValid(resultSet)) {
                return null;
            } else {
                List<String> columnNames = new ArrayList<>();
                columnNames.add(TableConstants.Customer.ID);
                columnNames.add(TableConstants.Customer.NAME);

                List<ColumnTypes> columnTypes = new ArrayList<>();
                columnTypes.add(TableConstants.Customer.ID_TYPE);
                columnTypes.add(TableConstants.Customer.NAME_TYPE);

                ResultSetHelper resultSetHelper = new ResultSetHelper(resultSet, columnNames, columnTypes);
                Object[][] results = resultSetHelper.printResults(25);
                ArrayList<Integer> customerIds = new ArrayList<>();
                for (Object[] result : results) {
                    if (result != null) {
                        customerIds.add((Integer) result[0]);
                    }
                }
                return customerIds;
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving results!");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Checks if the entered ID matches one of the IDs returned from the previous query.
     *
     * @param customerIdList The list of customer IDs from which the customer ID should be compared.
     * @param customerId     The ID that should be checked.
     * @return True if it's a valid ID or false if it's not.
     */
    public boolean isValidCustomerId(ArrayList<Integer> customerIdList, int customerId) {
        for (Integer id : customerIdList) {
            if (id == customerId) {
                return true;
            }
        }
        return false;
    }

}
