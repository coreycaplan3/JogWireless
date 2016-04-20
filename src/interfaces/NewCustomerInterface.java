package interfaces;

import validation.FormValidation;

/**
 * An interface used to represent a customer wishing to open an account from online.
 */
public class NewCustomerInterface extends CustomerInterface {

    public NewCustomerInterface() {
        System.out.println("Greetings, welcome to our online store. We cannot wait to get you signed up with Jog!");
    }

    @Override
    public boolean performTransaction() {
        System.out.println("Would you like to sign up as an existing customer?");
        boolean choice = FormValidation.getTrueOrFalse();
        String information = null;
        if (choice) {
            information = getCustomerIdFromList();
        }
        performOpenAccount(information);
        System.out.println("Returning to the interface selection screen...");
        System.out.println();
        return true;
    }

    @Override
    boolean isResidential() {
        return true;
    }

}
