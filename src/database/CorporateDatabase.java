package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * A class used for making database transactions that are centered upon corporate accounts.
 */
public class CorporateDatabase extends CustomerDatabase {

    private DatabaseApi databaseApi;

    public CorporateDatabase() {
        databaseApi = DatabaseApi.getInstance();
    }

    /**
     * @return A 2d array of strings containing the plan name in the 0 column and a description in the 1st. Can be
     * <b>null</b> if there was an error performing the transaction.
     */
    public String[][] getCorporatePlans() {
        try {
            String query = "SELECT *\n" +
                    "FROM PLANS\n" +
                    "WHERE IS_RESIDENTIAL = 0\n";
            ResultSet resultSet = databaseApi.executeQuery(query);
            ArrayList<String> planDescriptions = new ArrayList<>();
            ArrayList<String> planNames = new ArrayList<>();

            while (resultSet.next()) {
                String planName = resultSet.getString(TableConstants.Plans.P_TYPE);
                int hardLimit = resultSet.getInt(TableConstants.Plans.HARD_LIMIT);
                int limitTexts = resultSet.getInt(TableConstants.Plans.LIMIT_TEXTS);
                int limitCalls = resultSet.getInt(TableConstants.Plans.LIMIT_CALLS_SECONDS);
                int limitInternet = resultSet.getInt(TableConstants.Plans.LIMIT_INTERNET_MEGABYTES);
                double rateTexts = resultSet.getDouble(TableConstants.Plans.RATE_TEXTS);
                double rateCalls = resultSet.getDouble(TableConstants.Plans.RATE_CALLS_SECONDS);
                double rateInternet = resultSet.getDouble(TableConstants.Plans.RATE_INTERNET_MEGABYTES);
                double baseRate = resultSet.getDouble(TableConstants.Plans.BASE_RATE);

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
