package interfaces;

import validation.FormValidation;

/**
 * An interface used to represent a new business opening an account with Jog.
 */
public class NewBusinessInterface extends AbstractCustomerInterface {

    private String customerId;

    public NewBusinessInterface() {
        System.out.println("Welcome to Jog for business! We cannot wait to get you signed up with our reliable service!");
    }

    @SuppressWarnings("Duplicates")
    @Override
    public boolean performTransaction() {
        System.out.println("Would you like to open a new account with us?");
        boolean choice = FormValidation.getTrueOrFalse();
        if (!choice) {
            System.out.println("Returning to the interface selection screen...");
            System.out.println();
            return true;
        } else {
            System.out.println("Would you like to open a new account as an existing customer?");
            choice = FormValidation.getTrueOrFalse();
            if (choice) {
                customerId = getCustomerIdFromList();
            }
            if (customerId == null) {
                System.out.println("Since you didn\'t specify an existing customer, you\'ll create the " +
                        "business\'s account as a new customer.");
                customerId = "-1";
            }
            performOpenAccount(customerId, 1);
            System.out.println("Returning to the interface screen...");
            System.out.println();
            return true;
        }
    }


    @Override
    boolean isResidential() {
        return false;
    }
}
