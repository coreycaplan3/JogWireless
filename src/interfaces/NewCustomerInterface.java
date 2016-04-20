package interfaces;

import database.ResidentialCustomerDatabase;
import validation.FormValidation;

/**
 * An interface used to represent a customer wishing to open an account from online.
 */
public class NewCustomerInterface extends CustomerInterface {

    private String customerId;
    private String name;

    public NewCustomerInterface() {
        System.out.println("Greetings customer, welcome to our online store. We cannot wait to get you signed up with " +
                "Jog!");
    }

    @Override
    public boolean performTransaction() {
        System.out.println("Would you like to sign up as an existing customer?");
        while (true) {
            int choice = FormValidation.getNumericInput("Enter 0 for no or 1 for yes:");
            if (choice == 0) {
                performOpenAccount(null);
                return true;
            } else if (choice == 1) {
                String[] information = getCustomerNameAndId();
                if (information != null) {
                    performOpenAccount(information[1]);
                }
                return true;
            } else {
                System.out.println("Invalid number entered.");
            }
        }
    }

    @Override
    boolean isResidential() {
        return true;
    }

}
