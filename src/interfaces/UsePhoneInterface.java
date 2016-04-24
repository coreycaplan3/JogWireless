package interfaces;

import database.CustomerUsageDatabase;
import validation.FormValidation;

/**
 * A theoretical interface that allows customers to use their devices. This includes sending text messages,
 * making phone calls, and using data.
 */
public class UsePhoneInterface extends AbstractCustomerInterface {

    private CustomerUsageDatabase customerUsageDatabase;
    private String customerId;
    private long customerPhoneNumber;

    public UsePhoneInterface() {
        System.out.println("Welcome to the theoretical usage interface!");
        System.out.println("This is a test environment in which you may send text messages, make phone calls,");
        System.out.println("and use data. Feel free to stress test Edgar1!");

        customerUsageDatabase = new CustomerUsageDatabase();
    }

    @Override
    public boolean performTransaction() {
        while (true) {
            System.out.println();
            System.out.println("Phone Usage Home Screen:");
            if (customerId == null) {
                customerId = getCustomerIdFromList();
                if (customerId == null) {
                    return true;
                }
                getPhoneFromCustomerId();
            } else {
                System.out.println("Would you like to change the customer that you are impersonating?");
                boolean choice = FormValidation.getTrueOrFalse();
                if (choice) {
                    customerId = getCustomerIdFromList();
                    if(customerId == null) {
                        return true;
                    }
                } else {
                    System.out.println("Would you like to change the phone number that you are using?");
                    choice = FormValidation.getTrueOrFalse();
                    if (choice) {
                        getPhoneFromCustomerId();
                    }
                }
            }
            System.out.println("Phone usage options:");
            System.out.printf("%-20s %d\n", "Send a text message", 1);
            System.out.printf("%-20s %d\n", "Make a phone call", 2);
            System.out.printf("%-20s %d\n", "Use the internet", 3);
            int response;
            while (true) {
                response = FormValidation.getIntegerInput("Please select an option:", 4);
                if (response == 1) {
                    sendTextMessage();
                    break;
                } else if (response == 2) {
                    makePhoneCall();
                    break;
                } else if (response == 3) {
                    useInternet();
                    break;
                } else {
                    System.out.println("Please enter a valid option.");
                }
            }
            if (customerId == null) {
                System.out.println("Returning to the interface selection screen...");
                System.out.println();
                return true;
            }
        }
    }

    private void getPhoneFromCustomerId() {
        Object[][] customerPhones = customerUsageDatabase.getCustomerPhones(customerId);
        while (true) {
            int choice = FormValidation.getIntegerInput("Please select the phone that you would like to use:",
                    customerPhones.length + 1);
            if (customerUsageDatabase.isCustomerPhoneValid(customerPhones, choice)) {
                customerPhoneNumber = (long) customerPhones[choice - 1][3];
                return;
            } else {
                System.out.println("Invalid phone selected!");
            }
        }
    }

    private void sendTextMessage() {
        long destPhoneNumber = FormValidation.getPhoneNumber("Please enter the phone number to which you would like " +
                "to send a text message:");
        String timeSent = FormValidation.getUsageStartDate("Please enter the day, month, and year, that the text was sent:");
        String timeReceived = FormValidation.getUsageEndDate(timeSent, 2);
        int textCount = FormValidation.getIntegerInput("Please enter the amount of texts you would like to send:", 250);
        for (int i = 0; i < textCount; i++) {
            customerUsageDatabase.sendTextMessage(customerPhoneNumber, destPhoneNumber, timeSent, timeReceived);
        }
    }

    private void makePhoneCall() {
        long destPhoneNumber = FormValidation.getPhoneNumber("Please enter the phone number to which you would like " +
                "to send a phone call:");
        String startTime = FormValidation.getUsageStartDate("Please enter the day, month, and year, that the phone " +
                "call was made:");
        int callDuration = FormValidation.getIntegerInput("Please enter the duration of the call in seconds:", 10800);
        String endTime = FormValidation.getUsageEndDate(startTime, callDuration);
        customerUsageDatabase.sendPhoneCall(customerPhoneNumber, destPhoneNumber, startTime, endTime);
    }

    private void useInternet() {
        String usageDate = FormValidation.getUsageStartDate("Please enter the day, month, and year, that the internet " +
                "was used:");
        int megabyteAmount = FormValidation.getIntegerInput("Please enter the amount in megabytes that you would like " +
                "to use:", 10240);
        customerUsageDatabase.useInternet(customerPhoneNumber, usageDate, megabyteAmount);
    }

    @Override
    boolean isResidential() {
        return false;
    }
}
