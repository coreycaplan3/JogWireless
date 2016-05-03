package interfaces;

import database.CustomerDatabase;
import validation.FormValidation;

import java.util.TreeMap;

/**
 * An interface used to represent the interactions a customer would have inside of a store.
 */
public class CustomerInStoreInterface extends AbstractCustomerInterface {

    private int storeNumber = ((int) (Math.random() * 9)) + 2;

    private CustomerDatabase customerDatabase;
    private TreeMap<Integer, Integer> customerIdsWithAccounts;

    public CustomerInStoreInterface() {
        System.out.println("Greetings customer, welcome to store number " + storeNumber + "!");
        customerDatabase = new CustomerDatabase();
        customerIdsWithAccounts = customerDatabase.getCustomerAccounts();
    }

    @Override
    public boolean performTransaction() {
        while (!customerIdsWithAccounts.containsKey(Integer.parseInt(getCustomerId()))) {
            getCustomerNameAndId();
            if (Integer.parseInt(getCustomerId()) == -1) {
                System.out.println("Well, we can\'t perform any transactions without an account and your information.");
                System.out.println("Returning to the interface screen...");
                System.out.println();
                return true;
            } else if (Integer.parseInt(getCustomerId()) == -2) {
                //Indicates the user created an account successfully.
                System.out.println("Now that you created an account, you may search for yourself in our system.");
                getCustomerNameAndId();
                if (Integer.parseInt(getCustomerId()) == -1) {
                    System.out.println("Well, we can\'t perform any transactions without an account and your information.");
                    System.out.println("Returning to the interface screen...");
                    System.out.println();
                    return true;
                }
            }
        }
        System.out.println("Welcome " + getCustomerName() + ", you look great today!");
        System.out.println();
        while (true) {
            System.out.println("*************• JOG WIRELESS: STORE " + storeNumber + " **********************************");
            System.out.printf("%-45s %d\n", "Open a new account up with Jog", 1);
            System.out.printf("%-45s %d\n", "View all of the phones you own", 2);
            System.out.printf("%-45s %d\n", "Upgrade your phone and trade in the old one", 3);
            System.out.printf("%-45s %d\n", "Report your phone as lost, stolen, or found", 4);
            System.out.printf("%-45s %d\n", "View basic information about your account", 5);
            System.out.printf("%-45s %d\n", "Add a person to your account", 6);
            System.out.printf("%-45s %d\n", "Change your account\'s plan", 7);
            System.out.printf("%-45s %d\n", "View your account\'s billing information", 8);
            System.out.printf("%-45s %d\n", "Pay one of your account\'s bills", 9);
            System.out.printf("%-45s %d\n", "Change your basic information", 10);
            System.out.printf("%-45s %d\n", "Go back to the interface screen", -1);
            System.out.println("**********************************************************************");
            int response = FormValidation.getIntegerInput("Select an option:", 11);
            if (response == -1) {
                return true;
            } else if (response < 1 || response > 10) {
                System.out.println("Please enter a valid choice!");
            } else {
                getChoice(response);
            }
        }
    }

    /**
     * Allows the user to find his/her information (name and id) from the DB. If the name isn't found, the user is
     * given the option to open an account.
     */
    private void getCustomerNameAndId() {
        String name;
        setCustomerId("-1");
        System.out.println("To get started, we\'re going to need your information.");
        while (true) {
            name = FormValidation.getStringInput("Please enter your name (which is case sensitive), -a to open an " +
                    "account, or -q to quit:", "name", 50);
            if (name.equals("-q")) {
                return;
            } else if (name.equals("-a")) {
                performOpenAccount(getCustomerId(), storeNumber);
                customerIdsWithAccounts = customerDatabase.getCustomerAccounts();
                return;
            }
            Object[][] customerIds = customerDatabase.getCustomerIdsForName(name);
            if (customerIds != null) {
                System.out.println();
                while (true) {
                    int customerId = FormValidation.getIntegerInput("Please enter your ID from the list, -1 to enter " +
                            "a different name:", 1000000);
                    String newName = isValidCustomerId(customerIds, customerId);
                    if (newName != null) {
                        setCustomerId(customerId + "");
                        setCustomerName(newName);
                        break;
                    } else if (customerId == -1) {
                        setCustomerId(customerId + "");
                        break;
                    } else {
                        System.out.println("Invalid ID entered.");
                    }
                }
                if (Integer.parseInt(getCustomerId()) != -1 &&
                        !customerIdsWithAccounts.containsKey(Integer.parseInt(getCustomerId()))) {
                    System.out.println("It appears that you aren\'t linked to an account right now.");
                    System.out.println("Would you like to open one?");
                    boolean shouldOpenAccount = FormValidation.getTrueOrFalse();
                    if (shouldOpenAccount) {
                        performOpenAccount(getCustomerId(), storeNumber);
                        customerIdsWithAccounts = customerDatabase.getCustomerAccounts();
                        return;
                    } else {
                        System.out.println("Returning to select a different customer.");
                        return;
                    }
                } else if (Integer.parseInt(getCustomerId()) != -1) {
                    //We got a valid customer with a valid account.
                    return;
                }
            } else {
                System.out.println("It appears you aren't in our system. Would you like to open an account?");
                boolean isGoingToOpenAccount = FormValidation.getTrueOrFalse();
                if (isGoingToOpenAccount) {
                    performOpenAccount(getCustomerId(), storeNumber);
                    customerIdsWithAccounts = customerDatabase.getCustomerAccounts();
                    return;
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
        String accountId;
        switch (choice) {
            case 1:
                performOpenAccount(getCustomerId(), storeNumber);
                System.out.println("Returning to the menu...");
                System.out.println();
                break;
            case 2:
                viewCustomerPhones();
                break;
            case 3:
                performUpgradePhone(getCustomerId(), storeNumber);
                break;
            case 4:
                performReportPhone(getCustomerId());
                break;
            case 5:
                accountId = getAccountIdFromCustomerId(getCustomerId());
                if (accountId == null) {
                    System.out.println("You must be the owner of a residential account to view account information!");
                    System.out.println("Returning to the selection screen...");
                    System.out.println();
                    return;
                }
                viewAccountInformation(accountId);
                break;
            case 6:
                accountId = getAccountIdFromCustomerId(getCustomerId());
                if (accountId == null) {
                    System.out.println("You must be the owner of an account to add a customer to an account!");
                    System.out.println("Returning to the selection screen...");
                    System.out.println();
                    return;
                }
                addCustomerToAccount(accountId, storeNumber);
                break;
            case 7:
                accountId = getAccountIdFromCustomerId(getCustomerId());
                if (accountId == null) {
                    System.out.println("You must be the owner of a residential account to change an account\'s plan!");
                    System.out.println("Returning to the selection screen...");
                    System.out.println();
                    return;
                }
                changeAccountPlan(accountId);
                break;
            case 8:
                accountId = getAccountIdFromCustomerId(getCustomerId());
                if (accountId == null) {
                    System.out.println("You must be the owner of a residential account to see billing information!");
                    System.out.println("Returning to the selection screen...");
                    System.out.println();
                    return;
                }
                performShowBilling(accountId);
                break;
            case 9:
                accountId = getAccountIdFromCustomerId(getCustomerId());
                if (accountId == null) {
                    System.out.println("You must be the owner of a residential account to pay a bill!");
                    System.out.println("Returning to the selection screen...");
                    System.out.println();
                    return;
                }
                payBill(accountId);
                break;
            case 10:
                changeBasicInformation();
                break;
            default:
                throw new IllegalArgumentException("Invalid choice entered. Found " + choice);
        }
    }

}
