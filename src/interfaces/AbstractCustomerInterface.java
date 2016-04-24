package interfaces;

import database.CustomerDatabase;
import validation.FormValidation;

import java.util.ArrayList;

/**
 * An <i>abstract</i> class used for passing on certain methods to the classes that extend it.
 */
abstract class AbstractCustomerInterface extends BaseInterface {

    private CustomerDatabase customerDatabase;

    AbstractCustomerInterface() {
        customerDatabase = new CustomerDatabase();
    }

    /**
     * Allows the user to search for a customer by name and retrieve its corresponding customer ID.
     *
     * @return A string containing the user's customer ID or null if the user wanted to back out of the transaction
     * and create a new account.
     */
    String getCustomerIdFromList() {
        while (true) {
            String prompt;
            if (isResidential()) {
                prompt = "Please enter your name:";
            } else {
                prompt = "Please enter the name of the owner of the business:";
            }
            String name = FormValidation.getStringInput(prompt, "name", 250);
            ArrayList<Integer> customerIdList = customerDatabase.getCustomerIdsForName(name);
            if (customerIdList != null) {
                System.out.println();
                if (isResidential()) {
                    prompt = "Please enter your ID from the list, -1 to enter a different name, or -2 to return:";
                } else {
                    prompt = "Please enter the owner\'s ID from the list, -1 to enter a different owner\'s name, or " +
                            "-2 to return:";
                }
                int response = FormValidation.getIntegerInput(prompt, 1000000);
                if (response == -2) {
                    return null;
                } else if (customerDatabase.isValidCustomerId(customerIdList, response)) {
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
     */
    void performOpenAccount(String customerId) {
        String address;
        String name;
        String prompt;
        if (customerId == null) {
            if (isResidential()) {
                prompt = "Please enter your name, or enter -q to return";
            } else {
                prompt = "Please enter the name of the owner of the account or -q to return";
            }
            name = FormValidation.getStringInput(prompt, "name", 100000);
            if (name.equals("-q")) {
                return;
            }
            if (isResidential()) {
                prompt = "Please enter your address:";
            } else {
                prompt = "Please enter the address of your business:";
            }
            address = FormValidation.getStringInput(prompt, "address", 250);
        } else {
            name = customerDatabase.getNameFromCustomerId(Integer.parseInt(customerId));
            address = customerDatabase.getAddressFromCustomerId(Integer.parseInt(customerId));
        }
        int desiredPhone = getNewPhone();
        String desiredPlan = getNewPhonePlanForAccount();
        customerDatabase.createAccount(name, address, desiredPhone, 1, desiredPlan);
        System.out.println();
    }

    void performUpgradePhone(String customerId, int storeNumber) {
        System.out.println("Here are the different models from which you may choose:");
        Object[][] phonesForSale = customerDatabase.getPhoneModelsForSale();
        while (true) {
            int response = FormValidation.getIntegerInput("Please enter the Phone ID of the phone you would like " +
                    "to buy, or enter -1 to return:", phonesForSale.length + 1);
            if (response == -1) {
                return;
            } else if (customerDatabase.isPhoneStocked(response, phonesForSale)) {
                int phoneToBuy = (int) phonesForSale[response - 1][0];
                Object[][] userPhones = customerDatabase.getCustomerPhones(customerId);
                while (true) {
                    response = FormValidation.getIntegerInput("Please select which of your phones you would like to " +
                            "upgrade:", 100000);
                    if (customerDatabase.doesUserOwnPhone(response, userPhones)) {
                        long oldMeid = (long) userPhones[response - 1][0];
                        long phoneNumber = (long) userPhones[response - 1][3];
                        customerDatabase.replaceNewPhone(phoneToBuy, customerId, oldMeid, phoneNumber,
                                storeNumber);
                        System.out.println();
                        return;
                    }
                }
            } else {
                System.out.println("Invalid Phone ID entered! Please try again.");
            }
        }
    }

    void performReportPhone(String customerId) {
        System.out.println("Please select one of these options:");
        System.out.printf("%-35s %d\n", "My phone is lost!", 1);
        System.out.printf("%-35s %d\n", "My phone got stolen!", 2);
        System.out.printf("%-35s %d\n", "I found my phone!", 3);
        System.out.printf("%-35s %d\n", "Go back to the selection screen", -1);
        while (true) {
            int response = FormValidation.getIntegerInput("", 5);
            if (response == -1) {
                return;
            } else if (response < 1 || response > 3) {
                System.out.println("Please enter a valid choice.");
            } else {
                Object[][] phones = customerDatabase.getCustomerPhones(customerId);
                if (phones == null) {
                    System.out.println("You have no phones to report!");
                    System.out.println();
                    return;
                }
                switch (response) {
                    case 1:
                        while (true) {
                            response = FormValidation.getIntegerInput("Please select a phone to be reported as lost: ",
                                    10000);
                            if (customerDatabase.doesUserOwnPhone(response, phones)) {
                                customerDatabase.reportLostPhone(phones[response - 1]);
                                System.out.println("Moving back to the selection screen...");
                                System.out.println();
                                return;
                            }
                        }
                    case 2:
                        while (true) {
                            response = FormValidation.getIntegerInput("Please select a phone to be reported as stolen:",
                                    10000);
                            if (customerDatabase.doesUserOwnPhone(response, phones)) {
                                customerDatabase.reportStolenPhone(phones[response - 1]);
                                System.out.println("Moving back to the selection screen...");
                                System.out.println();
                                return;
                            }
                        }
                    case 3:
                        while (true) {
                            response = FormValidation.getIntegerInput("Please select a phone to be reported as found:",
                                    10000);
                            if (customerDatabase.doesUserOwnPhone(response, phones)) {
                                customerDatabase.reportFoundPhone(phones[response - 1]);
                                System.out.println("Moving back to the selection screen...");
                                System.out.println();
                                return;
                            }
                        }
                    default:
                        throw new IllegalArgumentException("Invalid argument! Found " + response);
                }
            }
        }
    }

    /**
     * Gets the customer's account ID from a given customer ID.
     *
     * @param customerId The ID of the customer whose account should be tied to it.
     * @return The ID of the customer\'s account or -1 if the customer isn't tied to any accounts.
     */
    String getAccountIdFromCustomerId(String customerId) {
        Object[][] customerAccounts = customerDatabase.getAccountsWhereCustomerIsOwner(customerId);
        if (customerAccounts == null) {
            System.out.println();
            return "-1";
        }
        while (true) {
            int choice = FormValidation.getIntegerInput("Please enter the ID of the account you would like to manage",
                    100000);
            for (Object[] customerAccount : customerAccounts) {
                if (((Integer) customerAccount[0]) == choice) {
                    return "" + customerAccount[0];
                }
            }
            System.out.println("Please enter a valid ID from the list.");
        }
    }

    /**
     * Adds a customer to users account
     *
     * @param accountId   The ID of the account that should add a customer to it.
     * @param storeNumber The store that is adding the customer to his/her account. Used for keeping track of
     *                    inventory when a phone is sold.
     */
    void addCustomerToAccount(String accountId, int storeNumber) {
        System.out.println();
        String name = FormValidation.getStringInput("Enter the name of the new person you would like to add " +
                "to your account, or enter -s to search from our existing customer base: ", "name", 250);
        if (name.equals("-s")) {
            while (true) {
                name = FormValidation.getStringInput("Please enter the name of the customer you would like " +
                        "to find:", "name", 250);
                ArrayList<Integer> customerIdList = customerDatabase.getCustomerIdsForName(name);
                if (customerIdList != null) {
                    int customerId = FormValidation.getIntegerInput("Please enter the customer\'s ID from the list " +
                            "or enter -1 to research:", 1000000);
                    if (customerDatabase.isValidCustomerId(customerIdList, customerId)) {
                        name = customerDatabase.getNameFromCustomerId(customerId);
                        String address = customerDatabase.getAddressFromCustomerId(customerId);
                        int desiredPhone = getNewPhone();
                        customerDatabase.addCustomerToAccount("" + customerId, name, address, desiredPhone,
                                accountId, storeNumber);
                        System.out.println("Returning to the home screen...");
                        System.out.println();
                        return;
                    } else if (customerId != -1) {
                        System.out.println("Please enter a valid customer ID from the list.");
                    }
                }
            }
        } else {
            String address = FormValidation.getStringInput("Please enter the person\'s address:", "address",
                    250);
            int desiredPhone = getNewPhone();
            customerDatabase.addCustomerToAccount("-1", name, address, desiredPhone, accountId, storeNumber);
            System.out.println("Returning to the home screen...");
            System.out.println();
        }
        System.out.println();
    }

    /**
     * @return An integer representing the model of phone that the user would like.
     */
    int getNewPhone() {
        Object[][] phonesForSale = customerDatabase.getPhoneModelsForSale();
        while (true) {
            int desiredPhone = FormValidation.getIntegerInput("Please enter the Phone ID of the phone you would " +
                    "like to buy:", 100000);
            if (desiredPhone >= 1 && desiredPhone <= phonesForSale.length) {
                return desiredPhone;
            } else {
                System.out.println("Please enter a valid phone choice.");
            }
        }
    }

    /**
     * Changes the given account's billing plan.
     *
     * @param accountId The ID of the account that would like to change his/her plan.
     */
    void changeAccountPlan(String accountId) {
        String desiredPlan = getNewPhonePlanForAccount();
        if (isResidential()) {
            customerDatabase.changePlan(desiredPlan, accountId);
        } else {
            customerDatabase.changePlan(desiredPlan, accountId);
        }
        System.out.println("Returning to the home screen....");
        System.out.println();
    }

    /**
     * @return True if the given transaction is happening for a residential account or false if it's occurring for a
     * corporate account.
     */
    abstract boolean isResidential();

    /**
     * Prompts the user for the different phone plans and forces selection.
     *
     * @return One of the phone plans available to the user (P_TYPE, primary key). Whether its residential or
     * corporate depends on the interface with which the user is interacting.
     */
    String getNewPhonePlanForAccount() {
        System.out.println("Here are the plans to which you may switch:");
        String[][] phonePlans;
        phonePlans = customerDatabase.getAvailablePlans(isResidential());
        int length = (phonePlans.length + "").length();
        length = Math.min(length, 6);
        System.out.printf("%-130s %-" + length + "s", "Plan", "Option\n");
        for (int i = 0; i < phonePlans.length; i++) {
            System.out.printf("%s\n", phonePlans[i][1]);
            System.out.printf("%" + 134 + "s\n", (i + 1));
            System.out.println();
        }
        return isValidPhonePlan(phonePlans);
    }

    /**
     * Shows the charges for a given customer's account and billing period.
     *
     * @param accountId The ID of the account whose billing information should be shown.
     */
    void performShowBilling(String accountId) {
        String billingPeriod = FormValidation.getBillingPeriod("Please enter the billing period from which you would " +
                "like to see your bill.");
        customerDatabase.showBillingCharges(accountId, billingPeriod);
        System.out.println("Returning to the home screen...");
        System.out.println();
    }

    private String isValidPhonePlan(String[][] phonePlans) {
        while (true) {
            int choice = FormValidation.getIntegerInput("Please select a plan for your account:", 100000);
            if (choice >= 1 && choice <= phonePlans.length) {
                return phonePlans[choice - 1][0];
            } else {
                System.out.println("Please select a valid plan.");
            }
        }
    }

    void payBill(String accountId) {
        Object[][] unpaidBills = customerDatabase.getUnpaidBills(accountId);
        if(unpaidBills == null) {
            System.out.println("Returning to the selection screen...");
            System.out.println();
            return;
        }
        while (true) {
            int billToPay = FormValidation.getIntegerInput("Please enter the bill ID of the bill you would like to " +
                    "pay.", 1000000);
            for (Object[] unpaidBill : unpaidBills) {
                if ((Integer) unpaidBill[0] == billToPay) {
                    customerDatabase.payBill((int) unpaidBill[0]);
                    System.out.println("Returning to the selection screen...");
                    System.out.println();
                    return;
                }
            }
            System.out.println("Please enter a valid bill ID from the list.");
        }
    }

}
