package interfaces;

import validation.FormValidation;

/**
 * An interface used to represent a customer wishing to open an account from online.
 */
public class NewCustomerInterface extends AbstractCustomerInterface {

    public NewCustomerInterface() {
        System.out.println("Greetings, welcome to our online store. We cannot wait to get you signed up with Jog!");
    }

    @SuppressWarnings("Duplicates")
    @Override
    public boolean performTransaction() {
        System.out.println("Would you like to open a new account with us?");
        boolean choice = FormValidation.getTrueOrFalse();
        String customerId = null;
        if (!choice) {
            System.out.println("Returning to the interface selection screen...");
            System.out.println();
            return true;
        } else {
            System.out.println("Would you like to open the new account as an existing customer?");
            choice = FormValidation.getTrueOrFalse();
            if (choice) {
                customerId = getCustomerIdFromList();
            }
            if (customerId == null) {
                System.out.println("Since you didn\'t specify an existing customer, you\'ll create an account as a " +
                        "new customer.");
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
        return true;
    }

}
