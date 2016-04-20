package interfaces;

import database.CorporateDatabase;
import validation.FormValidation;

import java.util.ArrayList;

/**
 * An interface used to represent a new business opening an account with Jog.
 */
public class NewBusinessInterface extends CustomerInterface {

    private String customerId;

    private CorporateDatabase corporateDatabase;

    public NewBusinessInterface() {
        System.out.println("Welcome to Jog for business! We cannot wait to get you signed up with our reliable service!");
        corporateDatabase = new CorporateDatabase();
    }

    @Override
    public boolean performTransaction() {
        System.out.println("Would you like to open a new account with us?");
        while (true) {
            int choice = FormValidation.getNumericInput("Please enter 0 for no or 1 for yes.");
            if (choice == 0) {
                System.out.println("Returning to the interface selection screen...");
                System.out.println();
                return true;
            } else if (choice == 1) {
                if (customerId == null) {
                    System.out.println("Would you like to open a new account as an existing customer?");
                    while (true) {
                        choice = FormValidation.getNumericInput("Please enter 0 for no or 1 for yes.");
                        if (choice == 0) {
                            break;
                        } else if (choice == 1) {
                            getCustomerIdFromList();
                            break;
                        } else {
                            System.out.println("Please enter a valid number.");
                        }
                    }
                }
                performOpenAccount(customerId);
                System.out.println("Returning to the interface screen...");
                System.out.println();
                return true;
            } else {
                System.out.println("Please enter a valid number.");
            }
        }
    }


    private void getCustomerIdFromList() {
        while (true) {
            String name = FormValidation.getStringInput("Please enter your name:", "name", 250);
            ArrayList<Integer> customerIdList = corporateDatabase.getCustomerIdsForName(name);
            if (customerIdList != null) {
                System.out.println();
                int response = FormValidation.getNumericInput("Please enter your ID from the list, -1 to enter a " +
                        "different name, or -2 to return and create an account as a new customer:");
                if (response == -2) {
                    return;
                } else if (corporateDatabase.isValidCustomerId(customerIdList, response)) {
                    customerId = response + "";
                    return;
                } else if (response != -1) {
                    System.out.println("Please enter a valid number from the list.");
                }
            } else {
                System.out.println("Please try searching again.");
            }
        }
    }

    @Override
    boolean isResidential() {
        return false;
    }
}
