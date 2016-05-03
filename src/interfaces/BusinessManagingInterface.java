package interfaces;

import database.CustomerDatabase;
import validation.FormValidation;

import java.util.TreeMap;

/**
 * An interface used to represent the transactions that a business would perform when managing its account.
 */
public class BusinessManagingInterface extends AbstractCustomerInterface {

    private TreeMap<Integer, Integer> customersOnBusinessAccounts;

    public BusinessManagingInterface() {
        System.out.println("Welcome customer, we\'re glad to help with all of your corporate needs!");
        CustomerDatabase customerDatabase = new CustomerDatabase();
        customersOnBusinessAccounts = customerDatabase.getCustomersOnBusinessAccounts();
    }

    @Override
    public boolean performTransaction() {
        if (Integer.parseInt(getCustomerId()) == -1) {
            setCustomerId(getCustomerIdFromList());
            if (getCustomerId() == null) {
                System.out.println("Well, we can\'t do anything without your information.");
                System.out.println("Returning to the interface selection screen...");
                System.out.println();
                return true;
            } else if (!customersOnBusinessAccounts.containsKey(Integer.parseInt(getCustomerId()))) {
                System.out.println("Well we can\'t perform any transactions without you being on an account.");
                System.out.println("Returning to the interface selection screen...");
                System.out.println();
                return true;
            }
        }
        System.out.println("*********************** Corporate Account Menu ***********************");
        System.out.printf("%-45s %d\n", "View basic information about your account", 1);
        System.out.printf("%-45s %d\n", "Change basic information", 2);
        System.out.printf("%-45s %d\n", "Upgrade a phone and trade in the old one", 3);
        System.out.printf("%-45s %d\n", "Report a phone as lost, stolen, or found", 4);
        System.out.printf("%-45s %d\n", "Add a person to your business\'s account", 5);
        System.out.printf("%-45s %d\n", "Change your account\'s plan", 6);
        System.out.printf("%-45s %d\n", "View your account\'s billing information", 7);
        System.out.printf("%-45s %d\n", "Pay one of your account\'s bills", 8);
        System.out.printf("%-45s %d\n", "Go back to the interface screen", -1);
        System.out.println("**********************************************************************");
        while (true) {
            int choice = FormValidation.getIntegerInput("Select an option:", 10);
            String accountId;
            if (choice == 1) {
                accountId = getAccountIdFromCustomerId(getCustomerId());
                if (accountId == null) {
                    System.out.println("You must be the owner of a business account to view account information!");
                    System.out.println("Returning to the selection screen...");
                    System.out.println();
                    break;
                }
                viewAccountInformation(accountId);
                break;
            } else if (choice == 2) {
                changeBasicInformation();
                break;
            } else if (choice == 3) {
                performUpgradePhone(getCustomerId(), 1);
                break;
            } else if (choice == 4) {
                performReportPhone(getCustomerId());
                break;
            } else if (choice == 5) {
                accountId = getAccountIdFromCustomerId(getCustomerId());
                if (accountId == null) {
                    System.out.println("You must be the owner of a business account to add a customer!");
                    System.out.println("Returning to the selection screen...");
                    System.out.println();
                    break;
                }
                addCustomerToAccount(accountId, 1);
                break;
            } else if (choice == 6) {
                accountId = getAccountIdFromCustomerId(getCustomerId());
                if (accountId == null) {
                    System.out.println("You must be the owner of a business account to change the billing plan!");
                    System.out.println("Returning to the selection screen...");
                    System.out.println();
                    break;
                }
                changeAccountPlan(accountId);
                break;
            } else if (choice == 7) {
                accountId = getAccountIdFromCustomerId(getCustomerId());
                if (accountId == null) {
                    System.out.println("You must be the owner of a business account to view billing information!");
                    System.out.println("Returning to the selection screen...");
                    System.out.println();
                    break;
                }
                performShowBilling(accountId);
                break;
            } else if (choice == 8) {
                accountId = getAccountIdFromCustomerId(getCustomerId());
                if (accountId == null) {
                    System.out.println("You must be the owner of a business account to pay a bill!");
                    System.out.println("Returning to the selection screen...");
                    System.out.println();
                    break;
                }
                payBill(accountId);
                break;
            } else if (choice == -1) {
                return true;
            } else {
                System.out.println("Please enter a valid option.");
            }
        }
        return false;
    }

    @Override
    public boolean isResidential() {
        return false;
    }

}
