package interfaces;

import database.CustomerDatabase;
import validation.FormValidation;

import java.util.TreeMap;

/**
 * An <i>abstract</i> class used for passing on certain methods to the classes that extend it.
 */
abstract class AbstractCustomerInterface extends BaseInterface {

    private CustomerDatabase customerDatabase;
    private String customerName;
    private String customerId;
    private TreeMap<Integer, Integer> customersWithAccounts;

    AbstractCustomerInterface() {
        customerDatabase = new CustomerDatabase();
        customerId = "-1";
        if (!isResidential()) {
            customersWithAccounts = customerDatabase.getCustomersOnBusinessAccounts();
        }
    }

    String getCustomerId() {
        return customerId;
    }

    String getCustomerName() {
        return customerName;
    }

    void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    void setCustomerName(String customerName) {
        this.customerName = customerName;
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
                prompt = "Please enter your name (which is case sensitive) or -q to return:";
            } else {
                prompt = "Please enter the name of a person on the business\'s account (which is case sensitive) " +
                        "or -q to return:";
            }
            String name = FormValidation.getStringInput(prompt, "name", 50);
            if (name.equals("-q")) {
                return null;
            }
            Object[][] customerIdList = customerDatabase.getCustomerIdsForName(name);
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
                    if (!isResidential() && customersWithAccounts.containsKey(response)) {
                        return response + "";
                    } else if (!isResidential()) {
                        System.out.println("It appears you aren\'t tied to an account.");
                        System.out.println("You\'re going to have to select a different customer.");
                        System.out.println();
                    } else if (isResidential()) {
                        return response + "";
                    }
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
    void performOpenAccount(String customerId, int storeNumber) {
        String address;
        String prompt;
        if (customerId == null) {
            return;
        }
        if (Integer.parseInt(customerId) == -1) {
            if (isResidential()) {
                prompt = "Please enter your name:";
            } else {
                prompt = "Please enter the name of the owner of the business\'s account:";
            }
            customerName = FormValidation.getStringInput(prompt, "name", 50);
            if (isResidential()) {
                prompt = "Please enter your address:";
            } else {
                prompt = "Please enter the address of your business:";
            }
            address = FormValidation.getStringInput(prompt, "address", 50);
        } else {
            this.customerId = customerId;
            customerName = customerDatabase.getNameFromCustomerId(Integer.parseInt(customerId));
            address = customerDatabase.getAddressFromCustomerId(Integer.parseInt(customerId));
        }
        int desiredPhone = getNewPhone(storeNumber);
        int desiredPlan = getPhonePlanForAccount("Here are the plans offered with your account:");
        System.out.println("Are you sure you would like to create the account?");
        boolean choice = FormValidation.getTrueOrFalse();
        if (!choice) {
            return;
        }
        this.customerId = "-2";
        customerDatabase.createAccount(customerName, address, desiredPhone, storeNumber, desiredPlan,
                Integer.parseInt(customerId));
        System.out.println();
    }

    void performUpgradePhone(String customerId, int storeNumber) {
        Object[][] phonesForSale = customerDatabase.getPhoneModelsForSale(storeNumber);
        while (true) {
            int response = FormValidation.getIntegerInput("Please enter the Phone ID of the phone you would like " +
                    "to buy:", phonesForSale.length + 1);
            if (customerDatabase.isPhoneStocked(response, phonesForSale)) {
                int phoneToBuy = (int) phonesForSale[response - 1][0];
                Object[][] userPhones = customerDatabase.getCustomerPhones(customerId, isResidential());
                while (true) {
                    response = FormValidation.getIntegerInput("Please select which of your phones you would like to " +
                            "upgrade:", 100000);
                    if (customerDatabase.doesUserOwnPhone(response, userPhones)) {
                        System.out.println("Are you sure you would like to upgrade your phone?");
                        boolean shouldContinue = FormValidation.getTrueOrFalse();
                        if (!shouldContinue) {
                            System.out.println("Returning to the menu...");
                            System.out.println();
                            return;
                        }
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

    void changeBasicInformation() {
        System.out.println("********************** Change Basic Information **********************");
        System.out.printf("%-45s %d\n", "Change your name on your accounts", 1);
        System.out.printf("%-45s %d\n", "Change your address on your accounts", 2);
        System.out.printf("%-45s %d\n", "Return to the main menu", -1);
        System.out.println("**********************************************************************");
        while (true) {
            int choice = FormValidation.getIntegerInput("Please select an option:", 5);
            if (choice == -1) {
                return;
            } else if (choice == 2) {
                changeName();
                return;
            } else if (choice == 3) {
                changeAddress();
                return;
            } else {
                System.out.println("Please enter a valid option.");
            }
        }
    }

    private void changeName() {
        String newName = FormValidation.getStringInput("Please enter your new name:", "name", 50);
        System.out.println("Are you sure you would like to change your name?");
        boolean shouldContinue = FormValidation.getTrueOrFalse();
        if (!shouldContinue) {
            System.out.println("Name change discarded.");
        } else {
            customerDatabase.changeCustomerName(customerId, newName);
        }
        System.out.println("Returning to the main menu...");
        System.out.println();
    }

    private void changeAddress() {
        String newAddress = FormValidation.getStringInput("Please enter your new address:", "address", 50);
        System.out.println("Are you sure you would like to change your address?");
        boolean shouldContinue = FormValidation.getTrueOrFalse();
        if (!shouldContinue) {
            System.out.println("Address change discarded.");
        } else {
            customerDatabase.changeCustomerAddress(customerId, newAddress);
        }
        System.out.println("Returning to the main menu...");
        System.out.println();
    }

    void viewCustomerPhones() {
        customerDatabase.viewCustomersPhones(customerId, isResidential());
        System.out.println("Returning to the main menu...");
        System.out.println();
    }

    void viewAccountInformation(String accountId) {
        while (true) {
            System.out.println("********************** View Account Information **********************");
            System.out.printf("%-45s %d\n", "View customers on your account", 1);
            System.out.printf("%-45s %d\n", "View all of the phones on your account", 2);
            System.out.printf("%-45s %d\n", "View your current billing plan", 3);
            System.out.printf("%-45s %d\n", "View your usage for a given billing period", 4);
            System.out.printf("%-45s %d\n", "View customers on your account", -1);
            System.out.println("**********************************************************************");
            while (true) {
                int choice = FormValidation.getIntegerInput("Please select an option:", 5);
                if (choice == 1) {
                    customerDatabase.viewCustomersOnAccount(accountId);
                    System.out.println();
                    break;
                } else if (choice == 2) {
                    customerDatabase.viewPhonesOnAccount(accountId);
                    System.out.println();
                    break;
                } else if (choice == 3) {
                    String billingPeriod = FormValidation.getBillingPeriod("Please enter the billing period for which " +
                            "you would like to view your account\'s usage:");
                    customerDatabase.getUsageInformation(accountId, billingPeriod);
                    System.out.println();
                    break;
                } else if (choice == 4) {
                    customerDatabase.viewCurrentPlan(accountId);
                    System.out.println();
                    break;
                } else if (choice == -1) {
                    return;
                } else {
                    System.out.println("Please enter a valid option.");
                }
            }
        }
    }

    void performReportPhone(String customerId) {
        System.out.println("*************************** Report A Phone ***************************");
        System.out.printf("%-35s %d\n", "My phone is lost!", 1);
        System.out.printf("%-35s %d\n", "My phone got stolen!", 2);
        System.out.printf("%-35s %d\n", "I found my phone!", 3);
        System.out.printf("%-35s %d\n", "Go back to the selection screen", -1);
        System.out.println("**********************************************************************");
        while (true) {
            int response = FormValidation.getIntegerInput("Please select an option:", 5);
            if (response == -1) {
                return;
            } else if (response < 1 || response > 3) {
                System.out.println("Please enter a valid choice.");
            } else {
                Object[][] phones = customerDatabase.getCustomerPhones(customerId, isResidential());
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
     * @return The ID of the customer's account where he/she is the owner or <b>null</b> if the customer isn't tied to
     * any accounts.
     */
    String getAccountIdFromCustomerId(String customerId) {
        Object[][] customerAccounts = customerDatabase.getAccountsWhereCustomerIsOwner(customerId, isResidential());
        if (customerAccounts == null) {
            return null;
        }
        while (true) {
            int choice = FormValidation.getIntegerInput("Please enter the ID of the account you would like to manage:",
                    100000);
            for (Object[] customerAccount : customerAccounts) {
                if (Integer.parseInt((String) customerAccount[0]) == choice) {
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
                "to your account, or enter -s to search from our existing customer base: ", "name", 50);
        if (name.equals("-s")) {
            while (true) {
                name = FormValidation.getStringInput("Please enter the name of the customer you would like " +
                        "to find:", "name", 50);
                Object[][] customerIdList = customerDatabase.getCustomerIdsForName(name);
                if (customerIdList != null) {
                    while (true) {
                        int customerId = FormValidation.getIntegerInput("Please enter the customer\'s ID from the list " +
                                "or enter -1 to research:", 1000000);
                        if (customerDatabase.isValidCustomerId(customerIdList, customerId)) {
                            name = customerDatabase.getNameFromCustomerId(customerId);
                            String address = customerDatabase.getAddressFromCustomerId(customerId);
                            int desiredPhone = getNewPhone(storeNumber);
                            customerDatabase.addCustomerToAccount("" + customerId, name, address, desiredPhone,
                                    accountId, storeNumber);
                            System.out.println("Returning to the home screen...");
                            System.out.println();
                            return;
                        } else if (customerId == -1) {
                            break;
                        } else {
                            System.out.println("Please enter a valid customer ID from the list.");
                        }
                    }
                }
            }
        } else {
            String address = FormValidation.getStringInput("Please enter the person\'s address:", "address",
                    50);
            int desiredPhone = getNewPhone(storeNumber);
            System.out.println("Would you like to add this person to your account?");
            boolean shouldContinue = FormValidation.getTrueOrFalse();
            if (!shouldContinue) {
                System.out.println("Returning to the menu...");
                System.out.println();
                return;
            }
            customerDatabase.addCustomerToAccount("-1", name, address, desiredPhone, accountId, storeNumber);
            System.out.println("Returning to the home screen...");
            System.out.println();
        }
        System.out.println();
    }

    /**
     * @param storeNumber The store from which the phone is being bought.
     * @return An integer representing the model of phone that the user would like.
     */
    private int getNewPhone(int storeNumber) {
        Object[][] phonesForSale = customerDatabase.getPhoneModelsForSale(storeNumber);
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
        int desiredPlan = getPhonePlanForAccount("Here are the plans to which you may switch:");
        System.out.println("Are you sure you want to switch your plan?");
        boolean shouldContinue = FormValidation.getTrueOrFalse();
        if (!shouldContinue) {
            System.out.println("Returning to the menu...");
            System.out.println();
            return;
        }
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
     * @return One of the phone plans available to the user (P_NAME, primary key). Whether its residential or
     * corporate depends on the interface with which the user is interacting.
     */
    private int getPhonePlanForAccount(String prompt) {
        System.out.println(prompt);
        System.out.println();
        String[][] phonePlans = customerDatabase.getAvailablePlans(isResidential());
        for (int i = 0; i < phonePlans.length; i++) {
            System.out.println((i + 1) + ":");
            System.out.println(phonePlans[i][1]);
            System.out.println();
        }
        return getValidPhonePlan(phonePlans);
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

    private int getValidPhonePlan(String[][] phonePlans) {
        while (true) {
            int choice = FormValidation.getIntegerInput("Please select a plan for your account:", 100000);
            if (choice >= 1 && choice <= phonePlans.length) {
                return Integer.parseInt(phonePlans[choice - 1][0]);
            } else {
                System.out.println("Please select a valid plan.");
            }
        }
    }

    void payBill(String accountId) {
        Object[][] unpaidBills = customerDatabase.getUnpaidBills(accountId);
        if (unpaidBills == null) {
            System.out.println("Returning to the selection screen...");
            System.out.println();
            return;
        }
        System.out.println();
        System.out.println("Here are your unpaid bills:");
        while (true) {
            int billToPay = FormValidation.getIntegerInput("Please enter the bill ID of the bill you would like to " +
                    "pay or -1 to return.", 1000000);
            if (billToPay == -1) {
                System.out.println("Returning to the menu...");
                System.out.println();
                return;
            }
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
