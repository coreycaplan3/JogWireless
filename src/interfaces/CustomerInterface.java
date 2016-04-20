package interfaces;

import database.CorporateDatabase;
import database.ResidentialCustomerDatabase;
import validation.FormValidation;

import java.util.ArrayList;

/**
 * An <i>abstract</i> class used for passing on certain methods to the classes that extend it.
 */
abstract class CustomerInterface extends BaseInterface {

    private ResidentialCustomerDatabase residentialCustomerDatabase;
    private CorporateDatabase corporateDatabase;

    CustomerInterface() {
        residentialCustomerDatabase = new ResidentialCustomerDatabase();
        corporateDatabase = new CorporateDatabase();
    }

    /**
     * Allows the user to search for a customer by name and retrieve its corresponding customer ID.
     *
     * @return A string containing the user's customer ID or null if the user wanted to back out of the transaction.
     */
    String getCustomerIdFromList() {
        while (true) {
            String name = FormValidation.getStringInput("Please enter your name:", "name", 250);
            ArrayList<Integer> customerIdList = corporateDatabase.getCustomerIdsForName(name);
            if (customerIdList != null) {
                System.out.println();
                int response = FormValidation.getNumericInput("Please enter your ID from the list, -1 to enter a " +
                        "different name, or -2 to return and create an account as a new customer:");
                if (response == -2) {
                    return null;
                } else if (corporateDatabase.isValidCustomerId(customerIdList, response)) {
                    return response + "";
                } else if (response != -1) {
                    System.out.println("Please enter a valid number from the list.");
                }
                //At this point, we know the user entered -1 so the user is re-prompted for a name.
            } else {
                System.out.println("Please try searching again.");
            }
        }
    }

    /**
     * Allows the user to open an account or return depending on his/her choice of action.
     *
     * @param customerId The ID of the customer wishing to open an account.
     * @return True if the account opened successfully or false if there was an issue during the transaction or the
     * user would like to return.
     */
    boolean performOpenAccount(String customerId) {
        String address;
        String name;
        if (customerId == null) {
            name = FormValidation.getStringInput("Please enter your name, or enter -q to return", "name", 250);
            if (name.equals("-q")) {
                return false;
            }
            address = FormValidation.getStringInput("Please enter your address:", "address", 250);
        } else {
            name = residentialCustomerDatabase.getNameFromCustomerId(Integer.parseInt(customerId));
            address = residentialCustomerDatabase.getAddressFromCustomerId(Integer.parseInt(customerId));
        }
        int desiredPhone = pickNewPhone();
        String desiredPlan = getPhonePlan();
        if (residentialCustomerDatabase.createAccount(name, address, desiredPhone, 1, desiredPlan)) {
            System.out.println("Congrats, your account as been successfully created!");
            System.out.println("Welcome to Jog Wireless!");
        }
        System.out.println();
        return true;
    }

    /**
     * @return An integer representing the model of phone that the user would like.
     */
    @SuppressWarnings("Duplicates")
    int pickNewPhone() {
        Object[][] phonesForSale = residentialCustomerDatabase.getPhoneModelsForSale();
        while (true) {
            int desiredPhone = FormValidation.getNumericInput("Please enter the Phone ID of the phone you would like to buy:");
            if (desiredPhone >= 1 && desiredPhone <= phonesForSale.length) {
                return desiredPhone;
            } else {
                System.out.println("Please enter a valid phone choice.");
            }
        }
    }

    abstract boolean isResidential();

    /**
     * Prompts the user for the different phone plans and forces selection.
     *
     * @return One of the phone plans available to the user (P_TYPE, primary key). Whether its residential or
     * corporate depends on the interface with which the user is interacting.
     */
    String getPhonePlan() {
        String[][] phonePlans;
        if (isResidential()) {
            phonePlans = residentialCustomerDatabase.getResidentPlans();
        } else {
            phonePlans = corporateDatabase.getCorporatePlans();
        }
        int length = (phonePlans.length + "").length();
        length = Math.min(length, 6);
        System.out.printf("%-130s %-" + length + "s", "Plan", "Option\n");
        for (int i = 0; i < phonePlans.length; i++) {
            System.out.printf("%s\n", phonePlans[i][1]);
            System.out.printf("%" + 134 + "s\n", (i + 1));
            System.out.println();
        }
        return getPhonePlansFromList(phonePlans);
    }

    private String getPhonePlansFromList(String[][] phonePlans) {
        while (true) {
            int choice = FormValidation.getNumericInput("Please select a plan for your account:");
            if (choice >= 1 && choice <= phonePlans.length) {
                return phonePlans[choice - 1][0];
            } else {
                System.out.println("Please select a valid plan.");
            }
        }
    }

}
