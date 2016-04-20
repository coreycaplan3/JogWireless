package interfaces;

import database.ResidentCustomerDatabase;
import validation.FormValidation;

/**
 * An interface used to represent the interactions a customer would have inside of a store.
 */
public class CustomerInStoreInterface extends BaseInterface {

    private static int storeNumber = (int) ((Math.random() * 99) + 2);

    private String name;
    private String id;
    private ResidentCustomerDatabase residentCustomerDatabase;

    public CustomerInStoreInterface() {
        System.out.println("Greetings customer, welcome to store number " + storeNumber + "!");
        residentCustomerDatabase = new ResidentCustomerDatabase();
    }

    @Override
    public boolean performTransaction() {
        if (name == null && id == null) {
            if (!getCustomerNameAndId()) {
                return true;
            }
        }
        System.out.println("Welcome " + name + ", you look great today!");
        while (true) {
            System.out.println("Customer Selection Options:");
            System.out.printf("%-45s %d\n", "Open a new account up with Jog:", 1);
            System.out.printf("%-45s %d\n", "Upgrade your phone and trade in the old one:", 2);
            System.out.printf("%-45s %d\n", "Report your phone as lost, stolen, or found:", 3);
            System.out.printf("%-45s %d\n", "Add a person to your account:", 4);
            System.out.printf("%-45s %d\n", "Change your account\'s plan:", 5);
            System.out.printf("%-45s %d\n", "Go back to the interface screen", -1);
            int response = FormValidation.getNumericInput("");
            if (response == -1) {
                return true;
            } else if (response < 1 || response > 5) {
                System.out.println("Please enter a valid choice!");
            } else {
                getChoice(response);
            }

        }
    }

    @SuppressWarnings("Duplicates")
    private boolean getCustomerNameAndId() {
        while (true) {
            name = FormValidation.getStringInput("Please enter your name:", "name", 250);
            if (residentCustomerDatabase.getCustomerIdsForName(name)) {
                System.out.println();
                while (true) {
                    int response = FormValidation.getNumericInput("Please enter your ID from the list, -1 to enter a " +
                            "different name, or -2 to open a new account:");
                    if (response == -2) {
                        performOpenAccount();
                    } else if (residentCustomerDatabase.isValidCustomerId(response)) {
                        id = response + "";
                        return true;
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
                        return false;
                    } else if (response == 1) {
                        performOpenAccount();
                        return true;
                    } else {
                        System.out.println("Please either enter 0 or 1.");
                    }
                }
            }
        }
    }

    private void getChoice(int choice) {
        switch (choice) {
            case 1:
                performOpenAccount();
                break;
            case 2:
                performUpgradePhone();
                break;
            case 3:
                performReportPhone();
                break;
            case 4:
                addCustomerToAccount();
                break;
            case 5:
                changeAccountPlan();
                break;
            default:
                throw new IllegalArgumentException("Invalid choice entered. Found " + choice);
        }
    }

    /**
     * @return True if the account was successfully created or false if it was cancelled.
     */
    private boolean performOpenAccount() {
        System.out.println("Thank you for wanting to sign up with Jog!");
        String name = FormValidation.getStringInput("Please enter your name, or enter -q to return", "name", 250);
        if (name.equals("-q")) {
            return false;
        }
        String address = FormValidation.getStringInput("Please enter your address:", "address", 250);
        int desiredPhone = pickNewPhone();
        String desiredPlan = pickNewPlan();
        if (residentCustomerDatabase.createAccount(name, address, desiredPhone, storeNumber,
                desiredPlan)) {
            System.out.println("Congrats, your account as been successfully created!");
            System.out.println("Welcome to Jog Wireless!");
        }
        System.out.println();
        return true;
    }

    private void performUpgradePhone() {
        System.out.println("Here are the different models from which you may choose:");
        Object[][] phonesForSale = residentCustomerDatabase.getPhoneModelsForSale();
        while (true) {
            int response = FormValidation.getNumericInput("Please enter the Phone ID of the phone you would like " +
                    "to buy, or enter -1 to return:");
            if (response == -1) {
                return;
            } else if (residentCustomerDatabase.isPhoneStocked(response, phonesForSale)) {
                int phoneToBuy = (int) phonesForSale[response - 1][0];
                Object[][] userPhones = residentCustomerDatabase.getCustomerPhones(id);
                while (true) {
                    response = FormValidation.getNumericInput("Please select which of your phones you would like to upgrade:");
                    if (residentCustomerDatabase.doesUserOwnPhone(response, userPhones)) {
                        long oldMeid = (long) userPhones[response - 1][0];
                        long phoneNumber = (long) userPhones[response - 1][3];
                        if (residentCustomerDatabase.replaceNewPhone(phoneToBuy, id, oldMeid, phoneNumber, storeNumber)) {
                            System.out.println("Here is your new phone! Your information has been updated and " +
                                    "transferred over.");
                            System.out.println("Thank you for shopping with Jog!");
                        }
                        System.out.println();
                        return;
                    }
                }
            } else {
                System.out.println("Invalid Phone ID entered! Please try again.");
            }
        }
    }

    private void performReportPhone() {
        System.out.println("Please select one of these options:");
        System.out.printf("%-35s %d\n", "My phone is lost!", 1);
        System.out.printf("%-35s %d\n", "My phone got stolen!", 2);
        System.out.printf("%-35s %d\n", "I found my phone!", 3);
        System.out.printf("%-35s %d\n", "Go back to the selection screen", -1);
        while (true) {
            int response = FormValidation.getNumericInput("");
            if (response == -1) {
                return;
            } else if (response < 1 || response > 3) {
                System.out.println("Please enter a valid choice.");
            } else {
                Object[][] phones = residentCustomerDatabase.getCustomerPhones(id);
                if (phones == null) {
                    System.out.println("You have no phones to report!");
                    System.out.println();
                    return;
                }
                switch (response) {
                    case 1:
                        while (true) {
                            response = FormValidation.getNumericInput("Please select a phone to be reported as lost: ");
                            if (residentCustomerDatabase.doesUserOwnPhone(response, phones)) {
                                if (residentCustomerDatabase.reportLostPhone(phones[response - 1])) {
                                    System.out.println("Your phone as been successfully reported as lost.");
                                    System.out.println("We here at Jog are sorry for the inconvenience, but you will still " +
                                            "have to pay more money for a new phone!");
                                    System.out.println("Moving back to the selection screen...");
                                }
                                System.out.println();
                                return;
                            }
                        }
                    case 2:
                        while (true) {
                            response = FormValidation.getNumericInput("Please select a phone to be reported as stolen: ");
                            if (residentCustomerDatabase.doesUserOwnPhone(response, phones)) {
                                if (residentCustomerDatabase.reportStolenPhone(phones[response - 1])) {
                                    System.out.println("Your phone as been successfully reported as stolen.");
                                    System.out.println("We here at Jog are sorry for the inconvenience, but you will still " +
                                            "have to pay more money for a new phone!");
                                    System.out.println("Moving back to the selection screen...");
                                }
                                System.out.println();
                                return;
                            }
                        }
                    case 3:
                        while (true) {
                            response = FormValidation.getNumericInput("Please select a phone to be reported as found: ");
                            if (residentCustomerDatabase.doesUserOwnPhone(response, phones)) {
                                if (residentCustomerDatabase.reportFoundPhone(phones[response - 1])) {
                                    System.out.println("Your phone as been successfully reported as found!");
                                    System.out.println("We here at Jog are glad that you were able to find your phone!");
                                    System.out.println("Moving back to the selection screen...");
                                }
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

    private void addCustomerToAccount() {
        Object[][] customerAccounts = residentCustomerDatabase.getCustomerAccounts(id);
        if (customerAccounts == null) {
            System.out.println();
            return;
        }
        while (true) {
            int accountId = FormValidation.getNumericInput("Enter the account ID of the account you would like to add " +
                    "a customer, or -1 to return:");
            if (accountId == -1) {
                return;
            } else if (isAccountIdValid(customerAccounts, accountId)) {
                System.out.println();
                String name = FormValidation.getStringInput("Enter the name of the new person you would like to add " +
                        "to your account, or enter -s to search from our existing customer base: ", "name", 250);
                if (name.equals("-s")) {
                    while (true) {
                        name = FormValidation.getStringInput("Please enter the name of the customer you would like " +
                                "to find:", "name", 250);
                        if (residentCustomerDatabase.getCustomerIdsForName(name)) {
                            int id = FormValidation.getNumericInput("Please enter the customer\'s ID from the list " +
                                    "or enter -1 to research:");
                            if (residentCustomerDatabase.isValidCustomerId(id)) {
                                name = residentCustomerDatabase.getNameFromCustomerId(id);
                                String address = residentCustomerDatabase.getAddressFromCustomerId(id);
                                int desiredPhone = pickNewPhone();
                                if (residentCustomerDatabase.addCustomerToAccount(id, name, address, desiredPhone,
                                        accountId, storeNumber)) {
                                    System.out.println("Successfully added the " + name + " to your account!");
                                } else {
                                    System.out.println("Returning to the home screen...");
                                }
                                System.out.println();
                                return;
                            } else if (id != -1) {
                                System.out.println("Please enter a valid customer ID from the list.");
                            }
                        }
                    }
                } else {
                    String address = FormValidation.getStringInput("Please enter the person\'s address:", "address",
                            250);
                    int desiredPhone = pickNewPhone();
                    if (residentCustomerDatabase.addCustomerToAccount(-1, name, address, desiredPhone,
                            accountId, storeNumber)) {
                        System.out.println("Successfully added " + name + " to your account!");
                    }
                    System.out.println();
                    return;
                }
            } else {
                System.out.println("Please enter a valid account number.");
            }
        }
    }

    private boolean isAccountIdValid(Object[][] accountIds, int chosenId) {
        for (Object[] accountId : accountIds) {
            if ((Integer) accountId[0] == chosenId) {
                return true;
            }
        }
        return false;
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

    private void changeAccountPlan() {
        System.out.println("Here are the accounts of which you are listed as an owner:");
        String[][] accounts = residentCustomerDatabase.getAccountsWhereCustomerIsOwner(Integer.parseInt(id));
        if (accounts == null) {
            System.out.println("Returning to the home screen...");
            System.out.println();
            return;
        }
        int accountId = getAccountId(accounts);
        String plan = pickNewPlan();
        if (residentCustomerDatabase.changePlan(plan, accountId)) {
            System.out.println("Your desired plan has been changed and the effects will take place during the next " +
                    "billing cycle.");
        }
        System.out.println("Returning to the home screen....");
        System.out.println();
    }

    private int getAccountId(String[][] accounts) {
        while (true) {
            int accountId = FormValidation.getNumericInput("Please enter an account ID from the list");
            for (String[] account : accounts) {
                if (Integer.parseInt(account[0]) == accountId) {
                    return accountId;
                }
            }
        }
    }

    private String pickNewPlan() {
        String[][] residentPlans = residentCustomerDatabase.getResidentPlans();
        int length = (residentPlans.length + "").length();
        length = Math.min(length, 6);
        System.out.printf("%-130s %-" + length + "s", "Plan", "Option\n");
        for (int i = 0; i < residentPlans.length; i++) {
            System.out.printf("%s\n", residentPlans[i][1]);
            System.out.printf("%" + 134 + "s\n", (i + 1));
            System.out.println();
        }
        while (true) {
            int choice = FormValidation.getNumericInput("Please select a plan for your account:");
            if (choice >= 1 && choice <= residentPlans.length) {
                return residentPlans[choice - 1][0];
            } else {
                System.out.println("Please select a valid plan.");
            }
        }
    }

}
