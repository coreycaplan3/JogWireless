package interfaces;

import database.ResidentialCustomerDatabase;
import validation.FormValidation;

import java.util.ArrayList;

/**
 * An interface used to represent the interactions a customer would have inside of a store.
 */
public class CustomerInStoreInterface extends CustomerInterface {

    private static int storeNumber = (int) ((Math.random() * 99) + 2);

    private String name;
    private String customerId;
    private ResidentialCustomerDatabase residentialCustomerDatabase;

    public CustomerInStoreInterface() {
        System.out.println("Greetings customer, welcome to store number " + storeNumber + "!");
        residentialCustomerDatabase = new ResidentialCustomerDatabase();
    }

    @Override
    public boolean performTransaction() {
        if (name == null && customerId == null) {
            String[] information = getCustomerNameAndId();
            if (information == null) {
                System.out.println("Returning to the interface screen...");
                System.out.println();
                return true;
            }
            name = information[0];
            customerId = information[1];
        }
        System.out.println("Welcome " + name + ", you look great today!");
        while (true) {
            System.out.println("Customer Selection Options:");
            System.out.printf("%-45s %d\n", "Open a new account up with Jog:", 1);
            System.out.printf("%-45s %d\n", "Upgrade your phone and trade in the old one:", 2);
            System.out.printf("%-45s %d\n", "Report your phone as lost, stolen, or found:", 3);
            System.out.printf("%-45s %d\n", "Add a person to your account:", 4);
            System.out.printf("%-45s %d\n", "Change your account\'s plan:", 5);
            System.out.printf("%-45s %d\n", "View your account\'s billing information", 6);
            System.out.printf("%-45s %d\n", "Go back to the interface screen", -1);
            int response = FormValidation.getNumericInput("");
            if (response == -1) {
                return true;
            } else if (response < 1 || response > 6) {
                System.out.println("Please enter a valid choice!");
            } else {
                getChoice(response);
            }
        }
    }

    /**
     * Allows the user to find his/her information (name and id) from the DB. If the name isn't found, the user is
     * given the option to open an account.
     *
     * @return A string array containing the customer's name and ID in the 0 and 1st index respectively.
     */
    private String[] getCustomerNameAndId() {
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
                boolean isGoingToOpenAccount = FormValidation.getTrueOrFalse();
                if (!isGoingToOpenAccount) {
                    return null;
                } else {
                    performOpenAccount(null);
                    return null;
                }
            }
        }
    }

    @Override
    boolean isResidential() {
        return true;
    }

    private void getChoice(int choice) {
        switch (choice) {
            case 1:
                performOpenAccount(customerId);
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
            case 6:
                performShowBilling();
                break;
            default:
                throw new IllegalArgumentException("Invalid choice entered. Found " + choice);
        }
    }

    private void performUpgradePhone() {
        System.out.println("Here are the different models from which you may choose:");
        Object[][] phonesForSale = residentialCustomerDatabase.getPhoneModelsForSale();
        while (true) {
            int response = FormValidation.getNumericInput("Please enter the Phone ID of the phone you would like " +
                    "to buy, or enter -1 to return:");
            if (response == -1) {
                return;
            } else if (residentialCustomerDatabase.isPhoneStocked(response, phonesForSale)) {
                int phoneToBuy = (int) phonesForSale[response - 1][0];
                Object[][] userPhones = residentialCustomerDatabase.getCustomerPhones(customerId);
                while (true) {
                    response = FormValidation.getNumericInput("Please select which of your phones you would like to upgrade:");
                    if (residentialCustomerDatabase.doesUserOwnPhone(response, userPhones)) {
                        long oldMeid = (long) userPhones[response - 1][0];
                        long phoneNumber = (long) userPhones[response - 1][3];
                        if (residentialCustomerDatabase.replaceNewPhone(phoneToBuy, customerId, oldMeid, phoneNumber, storeNumber)) {
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
                Object[][] phones = residentialCustomerDatabase.getCustomerPhones(customerId);
                if (phones == null) {
                    System.out.println("You have no phones to report!");
                    System.out.println();
                    return;
                }
                switch (response) {
                    case 1:
                        while (true) {
                            response = FormValidation.getNumericInput("Please select a phone to be reported as lost: ");
                            if (residentialCustomerDatabase.doesUserOwnPhone(response, phones)) {
                                if (residentialCustomerDatabase.reportLostPhone(phones[response - 1])) {
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
                            if (residentialCustomerDatabase.doesUserOwnPhone(response, phones)) {
                                if (residentialCustomerDatabase.reportStolenPhone(phones[response - 1])) {
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
                            if (residentialCustomerDatabase.doesUserOwnPhone(response, phones)) {
                                if (residentialCustomerDatabase.reportFoundPhone(phones[response - 1])) {
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
        Object[][] customerAccounts = residentialCustomerDatabase.getCustomerAccounts(customerId);
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
                        ArrayList<Integer> customerIdList = residentialCustomerDatabase.getCustomerIdsForName(name);
                        if (customerIdList != null) {
                            int id = FormValidation.getNumericInput("Please enter the customer\'s ID from the list " +
                                    "or enter -1 to research:");
                            if (residentialCustomerDatabase.isValidCustomerId(customerIdList, id)) {
                                name = residentialCustomerDatabase.getNameFromCustomerId(id);
                                String address = residentialCustomerDatabase.getAddressFromCustomerId(id);
                                int desiredPhone = pickNewPhone();
                                if (residentialCustomerDatabase.addCustomerToAccount(id, name, address, desiredPhone,
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
                    if (residentialCustomerDatabase.addCustomerToAccount(-1, name, address, desiredPhone,
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

    private void changeAccountPlan() {
        System.out.println("Here are the accounts of which you are listed as an owner:");
        String[][] accounts = residentialCustomerDatabase.getAccountsWhereCustomerIsOwner(Integer.parseInt(customerId));
        if (accounts == null) {
            System.out.println("Returning to the home screen...");
            System.out.println();
            return;
        }
        int accountId = getAccountId(accounts);
        String plan = getPhonePlan();
        if (residentialCustomerDatabase.changePlan(plan, accountId)) {
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

    private void performShowBilling() {
        System.out.println("Here are the accounts of which you are listed as an owner:");
        String[][] accounts = residentialCustomerDatabase.getAccountsWhereCustomerIsOwner(Integer.parseInt(customerId));
        if (accounts == null) {
            System.out.println("Returning to the home screen...");
            System.out.println();
            return;
        }
        int accountId = getAccountId(accounts);
        String billingPeriod = FormValidation.getBillingPeriod("Please enter the billing period from which you would " +
                "like to see your bill.");
        residentialCustomerDatabase.showBillingCharges(accountId, billingPeriod);
        System.out.println("Returning to the home screen...");
        System.out.println();
    }

}
