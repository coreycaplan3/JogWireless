package database;

import database.TableConstants.Bill;
import database.TableConstants.PhoneModel;
import database.TableConstants.Plans;
import sun.reflect.generics.tree.Tree;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

import static database.TableConstants.*;

/**
 * Created by Corey Caplan on 4/14/2016
 * <p></p>
 * A class that handles the typical transactions that occur between a residential or corporate customer and Jog
 * Wireless.
 */
public class CustomerDatabase {

    private DatabaseApi databaseApi;

    public CustomerDatabase() {
        databaseApi = DatabaseApi.getInstance();
    }

    /**
     * Shows the accounts billing information for the given billing period.
     *
     * @param accountId     The account ID of the account whose information should be retrieved.
     * @param billingPeriod The billing period of the bill that should be retrieved. Should be in the format
     *                      "yyyy-MM-dd HH:mm:ss".
     */
    public void showBillingCharges(String accountId, String billingPeriod) {
        try {
            String query = "SELECT A_ID, BILL_PERIOD, IS_PAID, P_NAME, ACCUMULATED_CHARGES\n" +
                    "FROM BILL NATURAL JOIN PLANS\n" +
                    "WHERE A_ID = " + accountId + " AND BILL_PERIOD = to_date(\'" + billingPeriod + "\', " +
                    "\'YYYY-MM-DD HH24:MI:SS\')";
            ResultSet resultSet = databaseApi.executeQuery(query);
            if (ResultSetHelper.isResultSetValid(resultSet, "Sorry, no bills were found for the given billing " +
                    "period.")) {
                System.out.printf("%-15s %-20s %-15s %-50s %-20s\n", "Account ID", "Bill Period", "Paid Yet?", "Plan",
                        "Accumulated Charges");
                resultSet.next();
                Date date = resultSet.getDate(Bill.BILL_PERIOD);
                String formattedDate = String.format(Locale.US, "%tB, %tY", date, date);
                String isPaid = String.valueOf(resultSet.getInt(Bill.IS_PAID) == 1);
                if (isPaid.charAt(0) == 't') {
                    isPaid = "True";
                } else {
                    isPaid = "False";
                }
                System.out.printf("%-15s %-20s %-15s %-50s $%.2f\n", resultSet.getInt(Bill.A_ID), formattedDate, isPaid,
                        resultSet.getString(Plans.P_NAME), resultSet.getDouble(Bill.ACCUMULATED_CHARGES));
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println("There was an error executing your transaction.");
        } finally {
            databaseApi.logout();
        }
    }

    /**
     * @param desiredPlan The plan to which the user would like to switch.
     * @param accountId   The account ID of the account whose plan should be changed.
     */
    public void changePlan(int desiredPlan, String accountId) {
        String query = "update ACCOUNT\n" +
                "set CURRENT_PLAN = " + desiredPlan + "\n" +
                "where A_ID = " + accountId;
        try {
            databaseApi.executeQuery(query);
            System.out.println("Your desired plan has been changed and the effects will take place during the next " +
                    "billing cycle.");
        } catch (SQLException e) {
            System.out.println("There was an error processing your request.");
        } finally {
            databaseApi.logout();
        }
    }

    /**
     * @return A {@link TreeMap} containing all of the accounts that are tied to a customer.
     */
    public TreeMap<Integer, Integer> getCustomerAccounts() {
        String query = "SELECT C_ID, A_ID FROM SUBSCRIBES";
        ArrayList<String> columnNames = ResultSetHelper.makeColumnNames("C_ID", "A_ID");
        TreeMap<Integer, Integer> treeMap = new TreeMap<>();
        try {
            ResultSet resultSet = databaseApi.executeQuery(query);
            while (resultSet.next()) {
                treeMap.put(resultSet.getInt(columnNames.get(0)), resultSet.getInt(columnNames.get(1)));
            }
        } catch (SQLException ignore) {
        }
        return treeMap;
    }

    /**
     * Retrieves the accounts for which the given customer ID is the owner.
     *
     * @param customerId The customer's ID.
     * @return A 2d array of Strings where the 0 column is the account ID, the 1st column is the primary number,
     * and the 2nd column is the current plan. Can be <b>null</b> if there are no accounts that the given customer owns
     * or there was an error processing the transaction.
     */
    public String[][] getAccountsWhereCustomerIsOwner(String customerId, boolean isResidential) {
        int residential = isResidential ? 1 : 0;
        String query = "SELECT\n" +
                "  A_ID,\n" +
                "  PRIMARY_NUMBER,\n" +
                "  P_NAME\n" +
                "FROM SUBSCRIBES\n" +
                "  NATURAL JOIN ACCOUNT\n" +
                "  JOIN PLANS ON PLANS.PLAN_ID = ACCOUNT.CURRENT_PLAN\n" +
                "WHERE C_ID = " + customerId +
                "      AND IS_OWNER = 1\n" +
                "      AND IS_RESIDENTIAL = " + residential;
        try {
            ResultSet resultSet = databaseApi.executeQuery(query);
            System.out.println();
            String errorMessage;
            if (isResidential) {
                errorMessage = "Sorry, you don\'t own any residential accounts!";
            } else {
                errorMessage = "Sorry, you don\'t own any corporate accounts!";
            }
            if (!ResultSetHelper.isResultSetValid(resultSet, errorMessage)) {
                return null;
            }

            ArrayList<String> columnNames = ResultSetHelper.makeColumnNames(Account.A_ID, Account.PRIMARY_NUMBER,
                    Plans.P_NAME);
            ArrayList<ColumnTypes> columnTypes = ResultSetHelper.makeColumnTypes(Account.A_ID_TYPE,
                    Account.PRIMARY_NUMBER_TYPE, Plans.P_NAME_TYPE);
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
        } finally {
            databaseApi.logout();
        }
    }

    /**
     * @param customerId The ID of the customer whose address should be retrieved.
     * @return The address of the given customer or <b>null</b> if the customer ID doesn't exist or there was an
     * error performing the transaction.
     */
    public String getAddressFromCustomerId(int customerId) {
        String query = "SELECT ADDRESS FROM CUSTOMER WHERE C_ID = " + customerId;
        return getFirstResultFromQuery(query);
    }

    private String getFirstResultFromQuery(String query) {
        try {
            ResultSet resultSet = databaseApi.executeQuery(query);
            resultSet.next();
            return resultSet.getString(1);
        } catch (SQLException e) {
            return null;
        } finally {
            databaseApi.logout();
        }
    }

    /**
     * @param customerId The ID of the customer whose name should be retrieved.
     * @return The customer's name if the transaction was successful or <b>null</b> if the transaction failed or the
     * customer ID doesn't exist.
     */
    public String getNameFromCustomerId(int customerId) {
        String query = "SELECT NAME FROM CUSTOMER WHERE C_ID = " + customerId;
        return getFirstResultFromQuery(query);
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
     */
    public void addCustomerToAccount(String customerId, String customerName, String customerAddress, int desiredPhone,
                                     String accountId, int storeNumber) {
        String query = "{call ADD_CUSTOMER_TO_ACCOUNT(" + customerId + ", \'" + customerName + "\', \'" +
                customerAddress + "\', " + desiredPhone + ", " + accountId + ", " + storeNumber + ")}";
        try {
            databaseApi.executeProcedure(query);
            System.out.println("Successfully added " + customerName + " to your account!");
        } catch (SQLException e) {
            if (e.getErrorCode() == 20000) {
                System.out.println("There are no more valid phone numbers! Jog needs to buy more numbers!");
            } else if (e.getErrorCode() == 20001) {
                System.out.println("Could not add " + customerName + ". That person is already listed as a member " +
                        "of your account!");
            } else {
                System.out.println("There was an error adding the customer to your account!");
            }
        } finally {
            databaseApi.logout();
        }
    }

    /**
     * Creates a new account for the user, giving him/her a new phone and phone number.
     *
     * @param name             The user's name.
     * @param address          The user's address.
     * @param desiredPhoneType The desired phone type of the user.
     * @param storeNumber      The store number from which the user is buying the phone.
     * @param planId           The ID of the plan that the customer would like to use.
     * @param customerId       The ID of the customer who is creating the account or -1 if it's a new customer.
     */
    public void createAccount(String name, String address, int desiredPhoneType, int storeNumber,
                              int planId, int customerId) {
        String sql = "{call CREATE_NEW_ACCOUNT(\'" + name + "\', \'" + address + "\', " + desiredPhoneType + ", " +
                storeNumber + ", " + planId + ", " + customerId + ")}";
        try {
            databaseApi.executeProcedure(sql);
            System.out.println("Your account has been successfully created!");
        } catch (SQLException e) {
            switch (e.getErrorCode()) {
                case 20000:
                    System.out.println("Sorry, There are no more valid phone numbers! Jog needs to buy more phone " +
                            "numbers!");
                default:
                    System.out.println("Oops: An error occurred!");
                    e.printStackTrace();
            }
        } finally {
            databaseApi.logout();
        }
    }

    /**
     * Reports the user's phone as either lost or stolen depending upon the selected option.
     *
     * @param meid       The meid of the phone that was stolen.
     * @param reportType 0 if the phone was stolen, 1 if it was lost, or 2 if it was found
     */
    private void reportPhone(long meid, int reportType) {
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
            if (reportType == 0) {
                System.out.println("Your phone as been successfully reported as stolen.");
                System.out.println("We here at Jog are sorry for the inconvenience, but you will " +
                        "still have to pay more money for a new phone!");
            } else if (reportType == 1) {
                System.out.println("Your phone as been successfully reported as lost.");
                System.out.println("We here at Jog are sorry for the inconvenience, but you will " +
                        "still have to pay more money for a new phone!");
            } else {
                System.out.println("Your phone as been successfully reported as found!");
                System.out.println("We here at Jog are glad that you were able to find your phone!");
            }
        } catch (SQLException e) {
            System.out.println("Error executing update!");
        } finally {
            databaseApi.logout();
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
     * @param customerId    The customer's ID
     * @param isResidential True if the phones being retrieved should be for his/her residential accounts.
     * @return A 2d array of objects containing the user's phones in the order of [MANUFACTURER], [MODEL],
     * [SELECTION_OPTION]. Can be <b>null</b> if the user has no phones to show.
     */
    public Object[][] getCustomerPhones(String customerId, boolean isResidential) {
        int residential = isResidential ? 1 : 0;
        String query = "SELECT\n" +
                "  meid,\n" +
                "  manufacturer,\n" +
                "  model,\n" +
                "  phone_number\n" +
                "FROM service\n" +
                "  NATURAL JOIN used_by\n" +
                "  NATURAL JOIN phone_product\n" +
                "  NATURAL JOIN subscribes\n" +
                "  NATURAL JOIN phone_model\n" +
                "  natural join account\n" +
                "  join plans on ACCOUNT.CURRENT_PLAN = PLANS.PLAN_ID\n" +
                "WHERE C_ID = " + customerId + "\n" +
                "and IS_RESIDENTIAL = " + residential;
        return customerPhones(query);
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
                "  meid,\n" +
                "  manufacturer,\n" +
                "  model,\n" +
                "  phone_number\n" +
                "FROM service\n" +
                "  NATURAL JOIN used_by\n" +
                "  NATURAL JOIN phone_product\n" +
                "  NATURAL JOIN subscribes\n" +
                "  NATURAL JOIN phone_model\n" +
                "  natural join account\n" +
                "  join plans on ACCOUNT.CURRENT_PLAN = PLANS.PLAN_ID\n" +
                "WHERE C_ID = " + customerId;
        return customerPhones(query);
    }

    private Object[][] customerPhones(String query) {
        try {
            ResultSet resultSet = databaseApi.executeQuery(query);
            if (ResultSetHelper.isResultSetValid(resultSet, "Sorry, you don\'t own any phones!")) {
                List<String> columnNames = new ArrayList<>();
                columnNames.add(PhoneProduct.MEID);
                columnNames.add(PhoneModel.MANUFACTURER);
                columnNames.add(PhoneModel.MODEL);
                columnNames.add(Service.PHONE_NUMBER);

                List<ColumnTypes> columnTypes = new ArrayList<>();
                columnTypes.add(PhoneProduct.MEID_TYPE);
                columnTypes.add(PhoneModel.MANUFACTURER_TYPE);
                columnTypes.add(PhoneModel.MODEL_TYPE);
                columnTypes.add(Service.PHONE_NUMBER_TYPE);

                ResultSetHelper helper = new ResultSetHelper(resultSet, columnNames, columnTypes);

                return helper.printResultsWithOptions(25);
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Unknown error!");
            return null;
        } finally {
            databaseApi.logout();
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
     */
    public void reportStolenPhone(Object[] phoneToReport) {
        reportPhone((Long) phoneToReport[0], 0);
    }

    /**
     * Reports a given phone as lost.
     *
     * @param phoneToReport The phone that the user would like to report.
     */
    public void reportLostPhone(Object[] phoneToReport) {
        reportPhone((Long) phoneToReport[0], 1);
    }

    /**
     * Reports a given phone as found.
     *
     * @param phoneToReport The phone that the user would like to report.
     */
    public void reportFoundPhone(Object[] phoneToReport) {
        reportPhone((Long) phoneToReport[0], 2);
    }

    /**
     * Allows the user to buy a new phone.
     *
     * @param phoneType  The type of phone that should be bought, in place of the old phone.
     * @param customerId The ID of the customer who is buying a phone.
     */
    public void replaceNewPhone(int phoneType, String customerId, long oldPhoneMeid, long phoneNumber, int storeNumber) {
        String sql = "{call CUSTOMER_REPLACES_NEW_PHONE(" + phoneType + ", " + customerId + ", " + oldPhoneMeid + ", " +
                phoneNumber + ", " + storeNumber + ")}";
        try {
            databaseApi.executeProcedure(sql);
            System.out.println("Your phone was upgraded successfully!");
            System.out.println("Thank you for shopping with Jog!");
        } catch (SQLException e) {
            switch (e.getErrorCode()) {
                case 20000:
                    System.out.println("Error: Invalid phone entered!");
                    break;
                case 20001:
                    System.out.println("Error: There are no customers with ID " + customerId + " that have a phone number!");
                    break;
                case 20002:
                    System.out.println("Sorry, there are no more phones of this model in stock.");
                    System.out.println("Jog is a slow moving company and it will be a while before new phones come " +
                            "back in stock.");
                    break;
                default:
                    System.out.println("Oops: An error occurred!");
                    e.printStackTrace();
                    break;
            }
        } finally {
            databaseApi.logout();
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
    public Object[][] getPhoneModelsForSale(int storeNumber) {
        try {
            String query = "SELECT PHONE_ID, MANUFACTURER, MODEL FROM STOCKS NATURAL JOIN PHONE_MODEL WHERE QUANTITY " +
                    "> 0 AND STORE_NUMBER = " + storeNumber;
            ResultSet resultSet = databaseApi.executeQuery(query);

            List<ColumnTypes> columnTypes = new ArrayList<>();
            columnTypes.add(PhoneModel.PHONE_ID_TYPE);
            columnTypes.add(PhoneModel.MANUFACTURER_TYPE);
            columnTypes.add(PhoneModel.MODEL_TYPE);

            List<String> columnNames = new ArrayList<>();
            columnNames.add(PhoneModel.PHONE_ID);
            columnNames.add(PhoneModel.MANUFACTURER);
            columnNames.add(PhoneModel.MODEL);

            if (!ResultSetHelper.isResultSetValid(resultSet, "We currently have no phones in stock at store number " +
                    storeNumber + ". Instead, we will ship you your phone from our warehouse.")) {
                query = "SELECT PHONE_ID, MANUFACTURER, MODEL FROM STOCKS NATURAL JOIN PHONE_MODEL WHERE QUANTITY " +
                        "> 0 AND STORE_NUMBER = 1";
                resultSet = databaseApi.executeQuery(query);
            }

            System.out.println("Here are the phones that are in stock and available for sale:");
            ResultSetHelper helper = new ResultSetHelper(resultSet, columnNames, columnTypes);
            return helper.printResults(20);
        } catch (SQLException e) {
            System.out.println("Unknown error getting phone models!");
            return null;
        } finally {
            databaseApi.logout();
        }
    }

    /**
     * Prints all of the IDs that match the given name.
     *
     * @param name The name for which to search.
     * @return An array list of customer IDs or <b>null</b> if no customers were found or an error occurred during the
     * transaction.
     */
    public Object[][] getCustomerIdsForName(String name) {
        if (name.length() > 2) {
            name = name.substring(1, name.length() - 1);
        }
        String sql = "SELECT * FROM CUSTOMER WHERE NAME LIKE \'%" + name + "%\' ORDER BY NAME";
        try {
            ResultSet resultSet = databaseApi.executeQuery(sql);
            if (!ResultSetHelper.isResultSetValid(resultSet, "No customers found for that name.")) {
                return null;
            } else {
                List<String> columnNames = new ArrayList<>();
                columnNames.add(Customer.ID);
                columnNames.add(Customer.NAME);

                List<ColumnTypes> columnTypes = new ArrayList<>();
                columnTypes.add(Customer.ID_TYPE);
                columnTypes.add(Customer.NAME_TYPE);

                ResultSetHelper resultSetHelper = new ResultSetHelper(resultSet, columnNames, columnTypes);
                return resultSetHelper.printResults(25);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving results!");
            e.printStackTrace();
            return null;
        } finally {
            databaseApi.logout();
        }
    }

    /**
     * Checks if the entered ID matches one of the IDs returned from the previous query.
     *
     * @param customerIdList The list of customer IDs from which the customer ID should be compared.
     * @param customerId     The ID that should be checked.
     * @return True if it's a valid ID or false if it's not.
     */
    public boolean isValidCustomerId(Object[][] customerIdList, int customerId) {
        for (Object[] id : customerIdList) {
            if ((Integer) id[0] == customerId) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param isResidential True if the plans being requested are residential, or false if they're corporate.
     * @return A 2d array of strings containing the plan name in the 0 column and a description in the 1st. Can be
     * <b>null</b> if there was an error performing the transaction.
     */
    public String[][] getAvailablePlans(boolean isResidential) {
        try {
            int residential = isResidential ? 1 : 0;
            String query = "SELECT *\n" +
                    "FROM PLANS\n" +
                    "WHERE IS_RESIDENTIAL = " + residential;
            ResultSet resultSet = databaseApi.executeQuery(query);
            ArrayList<String> planDescriptions = new ArrayList<>();
            ArrayList<Integer> planIds = new ArrayList<>();

            while (resultSet.next()) {
                String planName = resultSet.getString(Plans.P_NAME);
                int hardLimit = resultSet.getInt(Plans.HARD_LIMIT);
                int limitTexts = resultSet.getInt(Plans.LIMIT_TEXTS);
                int limitCallsSeconds = resultSet.getInt(Plans.LIMIT_CALLS_SECONDS);
                int limitInternetMegabytes = resultSet.getInt(Plans.LIMIT_INTERNET_MB);
                double rateTexts = resultSet.getDouble(Plans.RATE_TEXTS);
                double rateCallsSeconds = resultSet.getDouble(Plans.RATE_CALLS_SECONDS);
                double rateInternet = resultSet.getDouble(Plans.RATE_INTERNET_MB);
                double overdraftRateTexts = resultSet.getDouble(Plans.OVERDRAFT_RATE_TEXTS);
                double overdraftRateCalls = resultSet.getDouble(Plans.OVERDRAFT_RATE_CALLS_SECONDS);
                double overdraftRateInternet = resultSet.getDouble(Plans.OVERDRAFT_RATE_INTERNET_MB);
                double baseRate = resultSet.getDouble(Plans.BASE_RATE);
                int planId = resultSet.getInt(Plans.PLAN_ID);

                PlanParser planParser = new PlanParser(planName, hardLimit, limitTexts, limitCallsSeconds / 60,
                        limitInternetMegabytes / 1024, rateTexts, rateCallsSeconds * 60, rateInternet * 1024,
                        overdraftRateTexts, overdraftRateCalls * 60, overdraftRateInternet * 1024, baseRate);
                planIds.add(planId);
                planDescriptions.add(planParser.parse());
            }

            String[][] planInformation = new String[planDescriptions.size()][3];
            for (int i = 0; i < planInformation.length; i++) {
                planInformation[i][0] = planIds.get(i) + "";
                planInformation[i][1] = planDescriptions.get(i);
            }
            return planInformation;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            databaseApi.logout();
        }
    }

    /**
     * Gets all of the given account's unpaid bills, if there are any.
     *
     * @param accountId The account ID of the account whose unpaid bills should be retrieved.
     * @return A 2d array of objects containing the results of the query. The bill id will be in the 0 column. This
     * method can return <b>null</b> if the query fails to execute or there are no results found.
     */
    public Object[][] getUnpaidBills(String accountId) {
        ArrayList<String> columnNames = new ArrayList<>();
        columnNames.add(Bill.BILL_ID);
        columnNames.add(Bill.BILL_PERIOD);
        columnNames.add(Plans.P_NAME);
        columnNames.add(Bill.ACCUMULATED_CHARGES);
        String query = "SELECT " + columnNames.get(0) + ", " + columnNames.get(1) + ", " + columnNames.get(2) +
                ", " + columnNames.get(3) + " " +
                "FROM BILL NATURAL JOIN PLANS " +
                "WHERE A_ID = " + accountId + " " +
                "AND IS_PAID = 0";
        try {
            ResultSet resultSet = databaseApi.executeQuery(query);
            ArrayList<ColumnTypes> columnTypes = new ArrayList<>();
            columnTypes.add(Bill.BILL_ID_TYPE);
            columnTypes.add(Bill.BILL_PERIOD_TYPE);
            columnTypes.add(Plans.P_NAME_TYPE);
            columnTypes.add(Bill.ACCUMULATED_CHARGES_TYPE);

            ResultSetHelper resultSetHelper = new ResultSetHelper(resultSet, columnNames, columnTypes);
            System.out.println();
            if (!ResultSetHelper.isResultSetValid(resultSet, "You have no unpaid bills!")) {
                return null;
            } else {
                return resultSetHelper.printResults(20);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("There was an error retrieving your unpaid bills!");
            return null;
        } finally {
            databaseApi.logout();
        }
    }

    /**
     * Pays a customer's unpaid bill.
     *
     * @param billId The ID of the bill that should be paid.
     */
    public void payBill(int billId) {
        String query = "update bill set is_paid = 1 where bill_id = " + billId;
        try {
            databaseApi.executeQuery(query);
            System.out.println("You successfully paid your bill!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("There was an error paying your bill!");
        } finally {
            databaseApi.logout();
        }
    }

    /**
     * @return A {@link TreeMap} that contains all of the customers that are on a business account.
     */
    public TreeMap<Integer, Integer> getCustomersOnBusinessAccounts() {
        String query = "SELECT C_ID\n" +
                "FROM plans\n" +
                "  JOIN account ON PLANS.PLAN_ID = ACCOUNT.CURRENT_PLAN\n" +
                "  NATURAL JOIN SUBSCRIBES\n" +
                "WHERE IS_RESIDENTIAL = 0";
        return getBusinessCustomers(query);
    }

    private TreeMap<Integer, Integer> getBusinessCustomers(String query) {
        TreeMap<Integer, Integer> treeMap = new TreeMap<>();
        try {
            ResultSet resultSet = databaseApi.executeQuery(query);
            while (resultSet.next()) {
                treeMap.put(resultSet.getInt("C_ID"), 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return treeMap;
    }


}
