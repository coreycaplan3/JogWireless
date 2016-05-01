package interfaces;

import database.CustomerDatabase;
import database.CustomerUsageDatabase;
import validation.FormValidation;

import java.util.TreeMap;

/**
 * A theoretical interface that allows customers to use their devices. This includes sending text messages,
 * making phone calls, and using data.
 */
public class UsePhoneInterface extends AbstractCustomerInterface {

    private CustomerUsageDatabase customerUsageDatabase;
    private CustomerDatabase customerDatabase;
    private String customerId;
    private long customerPhoneNumber;
    private TreeMap<Integer, Integer> customersWithAccounts;

    public UsePhoneInterface() {
        System.out.println("Welcome to the theoretical usage interface!");
        System.out.println("This is a test environment in which you may send text messages, make phone calls,");
        System.out.println("and use data. Feel free to stress test Edgar1!");

        customerUsageDatabase = new CustomerUsageDatabase();
        customerDatabase = new CustomerDatabase();
        customersWithAccounts = customerDatabase.getCustomerAccounts();
    }

    @Override
    public boolean performTransaction() {
        while (true) {
            System.out.println();
            if (getCustomerInformation()) {
                return true;
            }
            System.out.println("***************************** Usage Menu *****************************");
            System.out.printf("%-20s %d\n", "Send a text message", 1);
            System.out.printf("%-20s %d\n", "Receive a text message", 2);
            System.out.printf("%-20s %d\n", "Make a phone call", 3);
            System.out.printf("%-20s %d\n", "Receive a phone call", 4);
            System.out.printf("%-20s %d\n", "Use the internet", 5);
            System.out.printf("%-20s %d\n", "Go back to the main menu", -1);
            System.out.println("**********************************************************************");
            int response;
            while (true) {
                response = FormValidation.getIntegerInput("Please select an option:", 4);
                if (response == 1) {
                    sendTextMessage();
                    break;
                } else if (response == 2) {
                    receiveTextMessage();
                    break;
                } else if (response == 3) {
                    sendPhoneCall();
                    break;
                } else if (response == 4) {
                    receivePhoneCall();
                    break;
                } else if (response == 5) {
                    useInternet();
                    break;
                } else if (response == -1) {
                    return true;
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

    private boolean getCustomerInformation() {
        if (customerId == null) {
            customerId = retrieveCustomerId();
            if (customerId == null) {
                return true;
            }
            getPhoneFromCustomerId();
        } else {
            System.out.println("Would you like to change the customer that you are impersonating?");
            boolean choice = FormValidation.getTrueOrFalse();
            if (choice) {
                customerId = retrieveCustomerId();
                if (customerId == null) {
                    return true;
                } else {
                    getPhoneFromCustomerId();
                }
            } else {
                System.out.println("Would you like to change the phone number that you are using?");
                choice = FormValidation.getTrueOrFalse();
                if (choice) {
                    getPhoneFromCustomerId();
                }
            }
        }
        return false;
    }

    private String retrieveCustomerId() {
        while (true) {
            String name = FormValidation.getStringInput("Please enter the name of the person you would like to " +
                    "impersonate or -q to return:", "name", 250);
            if (name.equals("-q")) {
                return null;
            }
            Object[][] customerIdList = customerDatabase.getCustomerIdsForName(name);
            if (customerIdList != null) {
                while (true) {
                    int customerId = FormValidation.getIntegerInput("Please enter the ID from the list or -1 to " +
                            "search for a different name:", 10000000);
                    if (customerId == -1) {
                        break;
                    } else if (customerDatabase.isValidCustomerId(customerIdList, customerId)) {
                        if (customersWithAccounts.containsKey(customerId)) {
                            return customerId + "";
                        } else {
                            System.out.println("Sorry that customer isn\'t linked to an account.");
                        }
                    } else {
                        System.out.println("Please enter a valid ID.");
                    }
                }
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
        long destinationPhoneNumber = FormValidation.getPhoneNumber("Please enter the phone number to which you " +
                "would like to send a text message:");
        String timeSent = FormValidation.getUsageStartDate("Please enter the day, month, and year, that the text " +
                "was sent:");
        String timeReceived = FormValidation.getUsageEndDate(timeSent, 2);
        int textCount = FormValidation.getIntegerInput("Please enter the amount of texts you would like to send:", 250);
        System.out.println("Preparing to send text messages...");
        for (int i = 0; i < textCount; i++) {
            customerUsageDatabase.sendTextMessage(customerPhoneNumber, destinationPhoneNumber, timeSent, timeReceived);
        }
    }

    private void receiveTextMessage() {
        long sourcePhone = FormValidation.getPhoneNumber("Please enter the phone number from which you " +
                "would like to receive a text message:");
        String timeSent = FormValidation.getUsageStartDate("Please enter the day, month, and year, that the text " +
                "was sent to you:");
        String timeReceived = FormValidation.getUsageEndDate(timeSent, 2);
        int textCount = FormValidation.getIntegerInput("Please enter the amount of texts you would like to receive:",
                250);
        System.out.println("Preparing to receive text messages...");
        for (int i = 0; i < textCount; i++) {
            customerUsageDatabase.receiveTextMessage(sourcePhone, customerPhoneNumber, timeSent, timeReceived);
        }
    }

    private void sendPhoneCall() {
        long destinationPhoneNumber = FormValidation.getPhoneNumber("Please enter the phone number to which you " +
                "would like to send a phone call:");
        String startTime = FormValidation.getUsageStartDate("Please enter the day, month, and year, that the phone " +
                "call was made:");
        int callDuration = FormValidation.getIntegerInput("Please enter the duration of the call in seconds:", 10800);
        String endTime = FormValidation.getUsageEndDate(startTime, callDuration);
        System.out.println("Preparing to make the phone call...");
        customerUsageDatabase.sendPhoneCall(customerPhoneNumber, destinationPhoneNumber, startTime, endTime);
    }

    private void receivePhoneCall() {
        long destinationPhoneNumber = FormValidation.getPhoneNumber("Please enter the phone number from which you " +
                "would like to receive a phone call:");
        String startTime = FormValidation.getUsageStartDate("Please enter the day, month, and year, that the phone " +
                "call was received:");
        int callDuration = FormValidation.getIntegerInput("Please enter the duration of the call in seconds:", 10800);
        String endTime = FormValidation.getUsageEndDate(startTime, callDuration);
        System.out.println("Preparing to make the phone call...");
        customerUsageDatabase.sendPhoneCall(destinationPhoneNumber, customerPhoneNumber, startTime, endTime);
    }

    private void useInternet() {
        String usageDate = FormValidation.getUsageStartDate("Please enter the day, month, and year, that the internet " +
                "was used:");
        int megabyteAmount = FormValidation.getIntegerInput("Please enter the amount in megabytes that you would like " +
                "to use:", 10240);
        System.out.println("Preparing to use the internet...");
        customerUsageDatabase.useInternet(customerPhoneNumber, usageDate, megabyteAmount);
    }

    @Override
    boolean isResidential() {
        return false;
    }
}
