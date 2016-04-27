package database;

import database.TableConstants.Bill;
import interfaces.BaseInterface;
import interfaces.ChiefExecutiveInterface;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by coreycaplan on 4/22/16.
 * <p></p>
 * A class that represents database transactions that occur between a CEO or accountant and the database.
 */
public class ChiefExecutiveDatabase {

    private DatabaseApi databaseApi;

    public ChiefExecutiveDatabase() {
        databaseApi = DatabaseApi.getInstance();
    }

    /**
     * Shows Jog Wireless's total accounts receivable.
     */
    public void getAccountsReceivable() {
        String query = "SELECT SUM(ACCUMULATED_CHARGES) " +
                "FROM BILL " +
                "WHERE IS_PAID = 0";
        try {
            ResultSet resultSet = databaseApi.executeQuery(query);
            System.out.print("Total Accounts Receivable: $" + resultSet.getDouble(0));
            System.out.println();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            databaseApi.logout();
        }
    }

    /**
     * Shows Jog Wireless's total cash collected.
     */
    public void getCashCollected() {
        String query = "SELECT SUM(ACCUMULATED_CHARGES) " +
                "FROM BILL";
        try {
            ResultSet resultSet = databaseApi.executeQuery(query);
            System.out.println("Total Cash Collected: $" + resultSet.getDouble(0));
            System.out.println();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            databaseApi.logout();
        }
    }

    /**
     * Shows all of the customers whose bills are past due.
     *
     * @param currentDate The current date, used as a reference for deciding which bills should be viewed as past due.
     */
    public void getBillsPastDue(String currentDate) {
        String query = "SELECT A_ID, BILL_PERIOD, ACCUMULATED_CHARGES " +
                "FROM BILL " +
                "WHERE extract(MONTH FROM to_date(\'" + currentDate + "\', \'yyyy-MM-dd HH24:mi:ss\')) > " +
                "extract(MONTH FROM BILL_PERIOD) and " +
                "extract(YEAR FROM to_date(\'" + currentDate + "\', \'yyyy-MM-dd HH24:mi:ss\')) > " +
                "extract(YEAR FROM BILL_PERIOD) AND " +
                "IS_PAID = 0";
        try {
            ResultSet resultSet = databaseApi.executeQuery(query);
            if (!ResultSetHelper.isResultSetValid(resultSet, "Couldn\'t find any past due bills for the given date!")) {
                return;
            }
            ArrayList<ColumnTypes> columnTypes = new ArrayList<>();
            columnTypes.add(Bill.A_ID_TYPE);
            columnTypes.add(Bill.BILL_PERIOD_TYPE);
            columnTypes.add(Bill.ACCUMULATED_CHARGES_TYPE);

            ArrayList<String> columnNames = new ArrayList<>();
            columnNames.add(Bill.A_ID);
            columnNames.add(Bill.BILL_PERIOD);
            columnNames.add(Bill.ACCUMULATED_CHARGES);

            ResultSetHelper resultSetHelper = new ResultSetHelper(resultSet, columnNames, columnTypes);
            resultSetHelper.printResults(20);
            System.out.println();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            databaseApi.logout();
        }
    }

    /**
     * Creates a new billing plan that will be adopted by all of Jog Wireless.
     *
     * @param planName               The name of the new billing plan.
     * @param isHardLimit            True if there should be a hard limit on all services: texts, calls, and internet.
     * @param limitTexts             The numerical limit of how many texts can be sent and received during the billing period.
     * @param limitCallsSeconds      The numerical limit of the total call duration a person may make or receive during the
     *                               billing period.
     * @param limitInternetMegabytes The numerical limit of how many megabytes a person can use during a billing period.
     * @param rateTexts              The rate at which the customer should be charged for each text (could be an overdraft
     *                               charge if limit texts is 0).
     * @param rateCallsSeconds       The rate at which the customer should be charged for calls in seconds (could be an
     *                               overdraft charge if limit calls is 0).
     * @param rateInternetMegabytes  The rate at which the customer should be charged for each megabyte of data (could be an
     *                               overdraft charge if limit internet is 0).
     * @param overdraftRateTexts     The rate at which the customer will be charged for exceeding the limit for text
     *                               messages in a given month. This rate will only be used if {@code isHardLimit} is
     *                               set to false.
     * @param overdraftRateCalls     The rate at which the customer will be charged for exceeding the limit for calls
     *                               (seconds) in a given month. This rate will only be used if {@code isHardLimit} is
     *                               set to false.
     * @param overdraftRateInternet  The rate at which the customer will be charged for exceeding the limit for internet
     *                               usage in a given month. This rate will only be used if {@code isHardLimit} is
     *                               set to false.
     * @param baseRate               The base rate at which a customer should be evenly charged throughout the month.
     * @param isResidential          True if the plan is for residential customers or false if it should be for corporate
     *                               customers.
     */
    public void createNewBillingPlan(String planName, boolean isHardLimit, int limitTexts, int limitCallsSeconds,
                                     int limitInternetMegabytes, double rateTexts, double rateCallsSeconds,
                                     double rateInternetMegabytes, double overdraftRateTexts, double overdraftRateCalls,
                                     double overdraftRateInternet, double baseRate, boolean isResidential) {
        int hardLimit = isHardLimit ? 1 : 0;
        int residential = isResidential ? 1 : 0;
        String query = "INSERT INTO PLANS (P_TYPE, HARD_LIMIT, LIMIT_TEXTS, LIMIT_CALLS_SECONDS, " +
                "LIMIT_INTERNET_MEGABYTES, RATE_TEXTS, RATE_CALLS_SECONDS, RATE_INTERNET_MEGABYTES, " +
                "OVERDRAFT_TEXTS, OVERDRAFT_CALLS, OVERDRAFT_INTERNET, BASE_RATE, IS_RESIDENTIAL " +
                "VALUES(\'" + planName + "\', " + hardLimit + ", " + limitTexts + ", " + limitCallsSeconds + ", " +
                limitInternetMegabytes + ", " + rateTexts + ", " + rateCallsSeconds + ", " + rateInternetMegabytes +
                ", " + overdraftRateTexts + ", " + overdraftRateCalls + ", " + overdraftRateInternet + ", " + baseRate
                + ", " + residential + ")";
        try {
            databaseApi.executeInsertOrUpdate(query);
            System.out.println("Successfully created your new billing plan!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("There was an error creating your new billing plan.");
        } finally {
            databaseApi.logout();
        }
    }

    /**
     * Gets a running count of Jog Wireless's accounts receivable by breaking down the total A/R into sub totals that
     * are grouped by account.
     *
     * @return A string array containing the list of columns returned by the query. Can return <b>null</b> if the
     * transaction encounters an error or there are no results returned.
     */
    public String[] getAccountsReceivableByCustomer() {
        return getFinancialInformationByCustomer(false);
    }

    /**
     * Gets a running count of Jog Wireless's cash collected by breaking down the total amount of cash collected into
     * sub totals that are grouped by account.
     *
     * @return A string array containing the list of columns returned by the query. Can return <b>null</b> if the
     * transaction encounters an error or there are no results returned.
     */
    public String[] getCashCollectedByCustomer() {
        return getFinancialInformationByCustomer(true);
    }

    private String[] getFinancialInformationByCustomer(boolean isCashCollected) {
        String[] columns = new String[3];
        columns[0] = "ACCOUNT_ID";
        columns[1] = "BILLING_PERIOD";
        columns[2] = isCashCollected ? "TOTAL_COLLECTED" : "TOTAL_UNPAID";
        int isPaid = isCashCollected ? 1 : 0;

        String query = "SELECT\n" +
                "  decode(GROUPING(A_ID), 1, \'ALL_ACCOUNTS\', A_ID) AS " + columns[0] + "," +
                "  decode(GROUPING(BILL_PERIOD), 1, \'ALL_BILLING_PERIODS\', to_char(BILL_PERIOD, " +
                "\'DD-MON-YYYY\')) AS " + columns[1] + ", sum(ACCUMULATED_CHARGES) AS " + columns[2] + "\n" +
                "FROM bill\n" +
                "WHERE IS_PAID = " + isPaid + "\n" +
                "GROUP BY ROLLUP (A_ID, BILL_PERIOD);\n";
        try {
            ResultSet resultSet = databaseApi.executeQuery(query);
            String errorMessage = isCashCollected ? "No cash has been collected yet (isPaid is 0 in all tuples)!" :
                    "Accounts receivable was empty (isPaid is 1 in all tuples)!";
            if (ResultSetHelper.isResultSetValid(resultSet, errorMessage)) {
                String resultMessage = isCashCollected ? "Cash collected, grouped by account:" : "Accounts receivable, " +
                        "grouped by account:";
                System.out.println(resultMessage);
                ArrayList<ColumnTypes> columnTypes = new ArrayList<>();

                ArrayList<String> columnNames = new ArrayList<>();

                ResultSetHelper resultSetHelper = new ResultSetHelper(resultSet, columnNames, columnTypes);
                resultSetHelper.printResults(25);
                return columns;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            databaseApi.logout();
        }
    }

    /**
     * Creates a new phone to be added to Jog's phone selection.
     *
     * @param manufacturer The new phone's manufacturer.
     * @param model        The new phone's model.
     */
    public void createNewPhone(String manufacturer, String model) {
        String procedure = "{call CREATE_NEW_PHONE(\'" + manufacturer + "\', \'" + model + "\')}";
        try {
            databaseApi.executeProcedure(procedure);

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("There was an error creating the new phone!");
        } finally {
            databaseApi.logout();
        }
    }

}
