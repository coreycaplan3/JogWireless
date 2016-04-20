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

    String[] getCustomerNameAndId() {
        String name, id;
        while (true) {
            name = FormValidation.getStringInput("Please enter your name:", "name", 250);
            ArrayList<Integer> customerIdList = residentialCustomerDatabase.getCustomerIdsForName(name);
            if (customerIdList != null) {
                System.out.println();
                while (true) {
                    int response = FormValidation.getNumericInput("Please enter your ID from the list, -1 to enter a " +
                            "different name, or -2 to open a new account:");
                    if (response == -2) {
                        performOpenAccount(null);
                    } else if (residentialCustomerDatabase.isValidCustomerId(customerIdList, response)) {
                        id = response + "";
                        return new String[]{name, id};
                    } else if (response == -1) {
                        break;
                    }
                }
            } else {
                System.out.println("It appears you aren't in our system. Would you like to open an account?");
                while (true) {
                    int response = FormValidation.getNumericInput("Please enter 0 for no, or 1 for yes:");
                    if (response == 0) {
                        System.out.println("Returning to the interface screen...");
                        System.out.println();
                        return null;
                    } else if (response == 1) {
                        performOpenAccount(null);
                        return null;
                    } else {
                        System.out.println("Please either enter 0 or 1.");
                    }
                }
            }
        }
    }

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
