package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static database.TableConstants.*;

/**
 * Makes calls to the database that represent either a customer in one of the stores.
 */
public class ResidentialCustomerDatabase extends AbstractCustomerDatabase {

    private DatabaseApi databaseApi;

    public ResidentialCustomerDatabase() {
        databaseApi = DatabaseApi.getInstance();
    }

    /**
     * @return A 2d array of strings containing the plan name in the 0 column and a description in the 1st. Can be
     * <b>null</b> if there was an error performing the transaction.
     */
    public String[][] getResidentPlans() {
        try {
            String query = "SELECT *\n" +
                    "FROM PLANS\n" +
                    "WHERE IS_RESIDENTIAL = 1\n";
            ResultSet resultSet = databaseApi.executeQuery(query);
            ArrayList<String> planDescriptions = new ArrayList<>();
            ArrayList<String> planNames = new ArrayList<>();

            while (resultSet.next()) {
                String planName = resultSet.getString(Plans.P_TYPE);
                int hardLimit = resultSet.getInt(Plans.HARD_LIMIT);
                int limitTexts = resultSet.getInt(Plans.LIMIT_TEXTS);
                int limitCalls = resultSet.getInt(Plans.LIMIT_CALLS_SECONDS);
                int limitInternet = resultSet.getInt(Plans.LIMIT_INTERNET_MEGABYTES);
                double rateTexts = resultSet.getDouble(Plans.RATE_TEXTS);
                double rateCalls = resultSet.getDouble(Plans.RATE_CALLS_SECONDS);
                double rateInternet = resultSet.getDouble(Plans.RATE_INTERNET_MEGABYTES);
                double baseRate = resultSet.getDouble(Plans.BASE_RATE);

                PlanParser planParser = new PlanParser(planName, hardLimit, limitTexts, limitCalls, limitInternet,
                        rateTexts, rateCalls, rateInternet, baseRate);
                planDescriptions.add(planParser.parse());
                planNames.add(planName);
            }

            String[][] planInformation = new String[planDescriptions.size()][2];
            for (int i = 0; i < planInformation.length; i++) {
                planInformation[i][0] = planNames.get(i);
                planInformation[i][1] = planDescriptions.get(i);
            }
            return planInformation;
        } catch (SQLException e) {
            System.out.println("Error processing transaction...");
            return null;
        }
    }

}
