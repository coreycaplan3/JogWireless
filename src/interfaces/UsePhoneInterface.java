package interfaces;

import database.CustomerUsageDatabase;
import validation.FormValidation;

import java.util.jar.Pack200;

/**
 * A theoretical interface that allows customers to use their devices. This includes sending text messages,
 * making phone calls, and using data.
 */
public class UsePhoneInterface extends CustomerInterface {

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
                getPhoneFromCustomerId();
            } else {
                System.out.println("Would you like to change the customer that you are impersonating?");
                boolean choice = FormValidation.getTrueOrFalse();
                if (choice) {
                    customerId = getCustomerIdFromList();
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
                response = FormValidation.getNumericInput("Please select an option:");
                switch (response) {
                    case 1:
                        sendTextMessage();
                        break;
                    case 2:
                        makePhoneCall();
                        break;
                    case 3:
                        useInternet();
                        break;
                    default:
                        System.out.println("Please enter a valid option.");
                        break;
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
            int choice = FormValidation.getNumericInput("Please select the phone that you would like to use:");
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
                "to send a text message.");
        String timeSent = FormValidation.getBillingPeriod("Please enter the day, month, and year, that the text was " +
                "sent.");
        customerUsageDatabase.sendTextMessage(customerPhoneNumber, destPhoneNumber, "", "" + 1);
    }

    @Override
    boolean isResidential() {
        return false;
    }
}
