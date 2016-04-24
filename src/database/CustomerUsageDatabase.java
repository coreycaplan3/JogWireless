package database;

import java.sql.SQLException;

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
        try {
            String procedure = "{call SEND_TEXT_MESSAGE(" +
                    sourcePhone + ", " +
                    destPhone + ", " +
                    "\'" + timeSent + "\', " +
                    "\'" + timeReceived + "\')}";
            databaseApi.executeProcedure(procedure);
            return true;
        } catch (SQLException e) {
            System.out.println("Error sending text message...");
            return false;
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
                    "\'" + startTime + "\', " +
                    "\'" + endTime + "\')}";
            databaseApi.executeProcedure(procedure);
            return true;
        } catch (SQLException e) {
            System.out.println("Error sending phone call...");
            return false;
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
                    "\'" + usageDate + "\', " +
                    megabyteAmount + ")}";
            databaseApi.executeProcedure(procedure);
            return true;
        } catch (SQLException e) {
            System.out.println("Error sending text message...");
            return false;
        }
    }

}
