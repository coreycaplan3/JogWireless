package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by coreycaplan on 4/20/16.
 * <p></p>
 * This class represents the database transactions that occur with a customer using his/her device.
 */
public class CustomerUsageDatabase extends CustomerDatabase {

    private DatabaseApi databaseApi;

    public CustomerUsageDatabase() {
        databaseApi = DatabaseApi.getInstance();
    }

    /**
     * Sends a text message from the given source phone to th
     *
     * @param sourcePhone  The phone that is sending the text message.
     * @param destPhone    The phone that is receiving the text message.
     * @param timeSent     The time that the source phone sent the text message.
     * @param timeReceived The time that the receiving phone actually got the text message.
     * @return True if the text message sent successfully or false if it did not.
     */
    public boolean sendTextMessage(long sourcePhone, long destPhone, String timeSent, String timeReceived) {
        return sendTextMessage(sourcePhone, destPhone, timeSent, timeReceived, 1024);
    }

    public boolean sendTextMessage(long sourcePhone, long destPhone, String timeSent, String timeReceived, int bytes) {
        try {
            String procedure = "{call SEND_TEXT_MESSAGE(" +
                    sourcePhone + ", " +
                    destPhone + ", " +
                    "to_date(\'" + timeSent + "\', \'yyyy-MM-dd HH24:mi:ss\'), " +
                    "to_date(\'" + timeReceived + "\', \'yyyy-MM-dd HH24:mi:ss\'), "
                    + bytes + ")}";
            databaseApi.executeProcedure(procedure);
            System.out.println("Text sent successfully!");
            return true;
        } catch (SQLException e) {
            if (e.getErrorCode() == 20000) {
                System.out.println("You cannot send anymore text messages for the month. You reached your monthly " +
                        "limit!");
            } else {
                e.printStackTrace();
                System.out.println("Error sending text message...");
            }
            return false;
        } finally {
            databaseApi.logout();
        }
    }

    /**
     * Sends a text message from the given source phone to th
     *
     * @param sourcePhone The phone that is sending the text message.
     * @param destPhone   The phone that is receiving the text message.
     * @param startTime   The time that the source phone started the text phone call.
     * @param endTime     The time that the phone call was terminated between the two phones.
     * @return True if the text message sent successfully or false if it did not.
     */
    public boolean sendPhoneCall(long sourcePhone, long destPhone, String startTime, String endTime) {
        try {
            String procedure = "{call SEND_PHONE_CALL(" +
                    sourcePhone + ", " +
                    destPhone + ", " +
                    "to_date(\'" + startTime + "\', \'yyyy-MM-dd HH24:mi:ss\'), " +
                    "to_date(\'" + endTime + "\', \'yyyy-MM-dd HH24:mi:ss\')" +
                    ")}";
            databaseApi.executeProcedure(procedure);
            System.out.println("Phone call was successful!");
            return true;
        } catch (SQLException e) {
            if (e.getErrorCode() == 20000) {
                System.out.println("You cannot make anymore phone calls for the month. You reached your monthly " +
                        "limit!");
            } else {
                System.out.println("Error sending phone call...");
            }
            return false;
        } finally {
            databaseApi.logout();
        }
    }

    /**
     * Sends a text message from the given source phone to th
     *
     * @param sourcePhone    The phone that is sending the text message.
     * @param usageDate      The date that the data was accessed.
     * @param megabyteAmount The amount of data that should be used, in megabytes.
     * @return True if the text message sent successfully or false if it did not.
     */
    public boolean useInternet(long sourcePhone, String usageDate, int megabyteAmount) {
        try {
            String procedure = "{call USE_INTERNET(" +
                    sourcePhone + ", " +
                    "to_date(\'" + usageDate + "\', \'yyyy-MM-dd HH24:mi:ss\'), " +
                    megabyteAmount + ")}";
            databaseApi.executeProcedure(procedure);
            System.out.println("Internet usage successful!");
            return true;
        } catch (SQLException e) {
            if (e.getErrorCode() == 20000) {
                System.out.println("You cannot use anymore data for the month. You reached your monthly limit!");
            } else {
                e.printStackTrace();
                System.out.println("Error using internet...");
            }
            return false;
        } finally {
            databaseApi.logout();
        }
    }

    /**
     * Gets all of the phone numbers that are in-service and on an account.
     *
     * @return A {@link TreeMap} that contains all of the phone numbers tied to an account.
     */
    public TreeMap<Long, Integer> getAllPhoneNumbersWithAccounts() {
        ArrayList<String> columnNames = ResultSetHelper.makeColumnNames("PHONE_NUMBER", "A_ID");
        String query = "SELECT\n" +
                "  A_ID,\n" +
                "  PHONE_NUMBER\n" +
                "FROM SUBSCRIBES";
        TreeMap<Long, Integer> treeMap = new TreeMap<>();
        try {
            ResultSet resultSet = databaseApi.executeQuery(query);
            while (resultSet.next()) {
                treeMap.put(resultSet.getLong(columnNames.get((0))), resultSet.getInt(columnNames.get(1)));
            }
        } catch (SQLException ignored) {
        }
        return treeMap;
    }

}
