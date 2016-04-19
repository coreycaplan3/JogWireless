package interfaces;

import database.CustomerInStoreDatabase;
import database.NewCustomerDatabase;
import validation.FormValidation;

/**
 *
 */
public class NewCustomerInterface extends BaseInterface {

    private String customerId;
    private String customerName;

    private NewCustomerDatabase newCustomerDatabase;
    private CustomerInStoreDatabase customerInStoreDatabase;

    public NewCustomerInterface() {
        System.out.println("Greetings customer, we cannot wait to get you signed up at Jog!");
        newCustomerDatabase = new NewCustomerDatabase();
        customerInStoreDatabase = new CustomerInStoreDatabase();
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
                    System.out.println("Would you like to open a new account using an existing customer\'s information?");
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
            } else {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    @SuppressWarnings("Duplicates")
    private void getCustomerIdFromList() {
        while (true) {
            String name = FormValidation.getStringInput("Please enter your name:", "name");
            if (newCustomerDatabase.getCustomerIdsForName(name)) {
                System.out.println();
                int response = FormValidation.getNumericInput("Please enter your ID from the list, -1 to enter a " +
                        "different name, or -2 to return and create an account as a new customer:");
                if (response == -2) {
                    return;
                } else if (newCustomerDatabase.isValidCustomerId(response)) {
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
            name = FormValidation.getStringInput("Please enter your name, or enter -q to return", "name");
            if (name.equals("-q")) {
                return false;
            }
            address = FormValidation.getStringInput("Please enter your address:", "address");
        } else {
            name = newCustomerDatabase.getNameFromCustomerId(customerId);
            address = newCustomerDatabase.getAddressFromCustomerId(customerId);
        }
        int desiredPhone = pickNewPhone();
        String desiredPlan = getResidentPlans();
        if (customerInStoreDatabase.createAccount(name, address, desiredPhone, 1, desiredPlan)) {
            System.out.println("Congrats, your account as been successfully created!");
            System.out.println("Welcome to Jog Wireless!");
        }
        System.out.println();
        return true;
    }

    @SuppressWarnings("Duplicates")
    private int pickNewPhone() {
        Object[][] phonesForSale = customerInStoreDatabase.getPhoneModelsForSale();
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
        System.out.printf("%-120s %d\n", "Resident - Base rate of $80/month. Includes 300 " +
                "outgoing minutes, 1,000 outgoing texts, 10GB data. There are major overdraft fees.", 1);
        System.out.printf("%-120s %d\n", "Resident - As used. $0.09 per outgoing minute, $0.01 per " +
                "outgoing text, and $5.12 per GB of data", 2);
        System.out.println("With Jog, all incoming calls and text messages are free!");
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
