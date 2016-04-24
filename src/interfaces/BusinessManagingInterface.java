package interfaces;

import validation.FormValidation;

/**
 * An interface used to represent the transactions that a business would perform when managing its account.
 */
public class BusinessManagingInterface extends AbstractCustomerInterface {

    private String customerId;

    public BusinessManagingInterface() {
        System.out.println("Welcome customer, we\'re glad to help with all of your corporate needs!");
    }

    @Override
    public boolean performTransaction() {
        if (customerId == null) {
            customerId = getCustomerIdFromList();
            if (customerId == null) {
                return true;
            }
        }
        System.out.println("Please select an option:");
        System.out.printf("%-45s %d\n", "Upgrade a phone and trade in the old one:", 1);
        System.out.printf("%-45s %d\n", "Report a phone as lost, stolen, or found:", 2);
        System.out.printf("%-45s %d\n", "Add a person to your business\'s account:", 3);
        System.out.printf("%-45s %d\n", "Change your account\'s plan:", 4);
        System.out.printf("%-45s %d\n", "View your account\'s billing information", 5);
        System.out.printf("%-45s %d\n", "Pay one of your account\'s bills", 6);
        System.out.printf("%-45s %d\n", "Go back to the interface screen", -1);
        while (true) {
            int choice = FormValidation.getIntegerInput("", 10);
            if (choice == 1) {
                performUpgradePhone(customerId, 1);
                break;
            } else if (choice == 2) {
                performReportPhone(customerId);
                break;
            } else if (choice == 3) {
                addCustomerToAccount(getAccountIdFromCustomerId(customerId), 1);
                break;
            } else if (choice == 4) {
                changeAccountPlan(getAccountIdFromCustomerId(customerId));
                break;
            } else if (choice == 5) {
                performShowBilling(getAccountIdFromCustomerId(customerId));
                break;
            } else if (choice == 6) {
                payBill(getAccountIdFromCustomerId(customerId));
            } else if (choice == -1) {
                return false;
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
