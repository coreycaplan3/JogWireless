package interfaces;

import database.CustomerDatabase;
import validation.FormValidation;

import java.util.ArrayList;

/**
 * An interface used to represent the interactions a customer would have inside of a store.
 */
public class CustomerInStoreInterface extends AbstractCustomerInterface {

    private int storeNumber = ((int) (Math.random() * 9)) + 2;

    private String name;
    private String customerId;
    private CustomerDatabase customerDatabase;

    public CustomerInStoreInterface() {
        System.out.println("Greetings customer, welcome to store number " + storeNumber + "!");
        customerDatabase = new CustomerDatabase();
    }

    @Override
    public boolean performTransaction() {
        if (name == null && customerId == null) {
            String[] information = getCustomerNameAndId();
            if (information == null) {
                System.out.println("Well, we can\'t perform any transactions without your information.");
                System.out.println("Returning to the interface screen...");
                System.out.println();
                return true;
            }
            name = information[0];
            customerId = information[1];
        }
        System.out.println("Welcome " + name + ", you look great today!");
        while (true) {
            System.out.println("Customer Selection Options:");
            System.out.printf("%-45s %d\n", "Open a new account up with Jog:", 1);
            System.out.printf("%-45s %d\n", "Upgrade your phone and trade in the old one:", 2);
            System.out.printf("%-45s %d\n", "Report your phone as lost, stolen, or found:", 3);
            System.out.printf("%-45s %d\n", "Add a person to your account:", 4);
            System.out.printf("%-45s %d\n", "Change your account\'s plan:", 5);
            System.out.printf("%-45s %d\n", "View your account\'s billing information", 6);
            System.out.printf("%-45s %d\n", "Pay one of your account\'s bills", 7);
            System.out.printf("%-45s %d\n", "Go back to the interface screen", -1);
            int response = FormValidation.getIntegerInput("", 10);
            if (response == -1) {
                return true;
            } else if (response < 1 || response > 6) {
                System.out.println("Please enter a valid choice!");
            } else {
                getChoice(response);
            }
        }
    }

    /**
     * Allows the user to find his/her information (name and id) from the DB. If the name isn't found, the user is
     * given the option to open an account.
     *
     * @return A string array containing the customer's name and ID in the 0 and 1st index respectively.
     */
    private String[] getCustomerNameAndId() {
        String name, id;
        while (true) {
            name = FormValidation.getStringInput("Please enter your name:", "name", 250);
            Object[][] customerIds = customerDatabase.getCustomerIdsForName(name);
            if (customerIds != null) {
                System.out.println();
                while (true) {
                    int response = FormValidation.getIntegerInput("Please enter your ID from the list, -1 to enter a " +
                            "different name, or -2 to open a new account:", 1000000);
                    String newName = isValidCustomerId(customerIds, response);
                    if (response == -2) {
                        performOpenAccount(null);
                    } else if (newName != null) {
                        id = response + "";
                        return new String[]{newName, id};
                    } else if (response == -1) {
                        break;
                    } else {
                        System.out.println("Invalid ID entered.");
                    }
                }
            } else {
                System.out.println("It appears you aren't in our system. Would you like to open an account?");
                boolean isGoingToOpenAccount = FormValidation.getTrueOrFalse();
                if (isGoingToOpenAccount) {
                    performOpenAccount(null);
                    return null;
                }
            }
        }
    }

    private String isValidCustomerId(Object[][] customerIds, int response) {
        for (Object[] customerId : customerIds) {
            if (((Integer) customerId[0]) == response) {
                return (String) customerId[1];
            }
        }
        return null;
    }

    @Override
    boolean isResidential() {
        return true;
    }

    private void getChoice(int choice) {
        switch (choice) {
            case 1:
                performOpenAccount(customerId);
                break;
            case 2:
                performUpgradePhone(customerId, storeNumber);
                break;
            case 3:
                performReportPhone(customerId);
                break;
            case 4:
                addCustomerToAccount(getAccountIdFromCustomerId(customerId), storeNumber);
                break;
            case 5:
                changeAccountPlan(getAccountIdFromCustomerId(customerId));
                break;
            case 6:
                performShowBilling(getAccountIdFromCustomerId(customerId));
                break;
            case 7:
                payBill(getAccountIdFromCustomerId(customerId));
                break;
            default:
                throw new IllegalArgumentException("Invalid choice entered. Found " + choice);
        }
    }

}
