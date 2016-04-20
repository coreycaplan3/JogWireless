package interfaces;

import database.ResidentCustomerDatabase;
import validation.FormValidation;

/**
 * An interface used to represent a customer wishing to open an account from online.
 */
public class NewCustomerInterface extends BaseInterface {

    private String customerId;

    private ResidentCustomerDatabase residentCustomerDatabase;

    public NewCustomerInterface() {
        System.out.println("Greetings customer, welcome to our online store. We cannot wait to get you signed up with " +
                "Jog!");
        residentCustomerDatabase = new ResidentCustomerDatabase();
    }

    @Override
    public boolean performTransaction() {
        System.out.println("Would you like to open a new account?");
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
                performOpenAccount();
                System.out.println("Returning to the interface screen...");
                System.out.println();
                return true;
            } else {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    @SuppressWarnings("Duplicates")
    private void getCustomerIdFromList() {
        while (true) {
            String name = FormValidation.getStringInput("Please enter your name:", "name", 250);
            if (residentCustomerDatabase.getCustomerIdsForName(name)) {
                System.out.println();
                int response = FormValidation.getNumericInput("Please enter your ID from the list, -1 to enter a " +
                        "different name, or -2 to return and create an account as a new customer:");
                if (response == -2) {
                    return;
                } else if (residentCustomerDatabase.isValidCustomerId(response)) {
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

    private boolean performOpenAccount() {
        String address;
        String name;
        if (customerId == null) {
            name = FormValidation.getStringInput("Please enter your name, or enter -q to return", "name", 250);
            if (name.equals("-q")) {
                return false;
            }
            address = FormValidation.getStringInput("Please enter your address:", "address", 250);
        } else {
            name = residentCustomerDatabase.getNameFromCustomerId(Integer.parseInt(customerId));
            address = residentCustomerDatabase.getAddressFromCustomerId(Integer.parseInt(customerId));
        }
        int desiredPhone = pickNewPhone();
        String desiredPlan = getResidentPlans();
        if (residentCustomerDatabase.createAccount(name, address, desiredPhone, 1, desiredPlan)) {
            System.out.println("Congrats, your account as been successfully created!");
            System.out.println("Welcome to Jog Wireless!");
        }
        System.out.println();
        return true;
    }

    @SuppressWarnings("Duplicates")
    private int pickNewPhone() {
        Object[][] phonesForSale = residentCustomerDatabase.getPhoneModelsForSale();
        while (true) {
            int desiredPhone = FormValidation.getNumericInput("Please enter the Phone ID of the phone you would like to buy:");
            if (desiredPhone >= 1 && desiredPhone <= phonesForSale.length) {
                return desiredPhone;
            } else {
                System.out.println("Please enter a valid phone choice.");
            }
        }
    }

    @SuppressWarnings("Duplicates")
    private String getResidentPlans() {
        System.out.println("Please select a plan for your account:");

        while (true) {
            int response = FormValidation.getNumericInput("Please select a plan");
            if (response > 2 || response < 1) {
                System.out.println("Please enter a valid response.");
            } else {
                if (response == 1) {
                    return "RESIDENT_LIMIT";
                } else {
                    return "RESIDENT_AS_USED";
                }
            }
        }
    }

}
