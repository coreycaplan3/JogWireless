package interfaces;

import database.CustomerInStoreDatabase;
import validation.FormValidation;

import java.util.Scanner;

/**
 * An interface used to represent the interactions a customer would have inside of a store.
 */
public class CustomerInStoreInterface extends BaseInterface {

    private static int storeNumber = (int) ((Math.random() * 99) + 2);

    private Scanner scanner;
    private String name;
    private String id;
    private CustomerInStoreDatabase customerInStoreDatabase;

    public CustomerInStoreInterface() {
        System.out.println("Greetings customer, welcome to store number " + storeNumber + "!");
        scanner = new Scanner(System.in);
        customerInStoreDatabase = new CustomerInStoreDatabase();
    }

    @Override
    public boolean performTransaction() {
        if (name == null && id == null) {
            getCustomerNameAndId();
        }
        System.out.println("Welcome " + name + ", you look great today!");
        while (true) {
            System.out.println("Customer Selection Options:");
            System.out.printf("%-45s %d\n", "Open a new account up with Jog:", 1);
            System.out.printf("%-45s %d\n", "Upgrade your phone and trade in the old one:", 2);
            System.out.printf("%-45s %d\n", "Report your phone as lost, stolen, or found:", 3);
            System.out.printf("%-45s %d\n", "Add a person to your account:", 4);
            System.out.printf("%-45s %d\n", "Go back to the interface screen", -1);
            int response = FormValidation.getNumericInput();
            if (response == -1) {
                return true;
            } else if (response < 1 || response > 4) {
                System.out.println("Please enter a valid choice!");
            } else {
                getChoice(response);
            }

        }
    }

    private void getCustomerNameAndId() {
        System.out.println("Please enter your name: ");
        while (true) {
            name = getName();
            if (customerInStoreDatabase.getCustomerIdsForName(name)) {
                System.out.println("Please enter your ID from the list, -1 to enter a different name, or -2 to " +
                        "open a new account:");
                int response = FormValidation.getNumericInput();
                if (response == -2) {
                    performOpenAccount();
                } else if (customerInStoreDatabase.isValidCustomerId(response)) {
                    id = response + "";
                    return;
                } else if (response != -1) {
                    System.out.println("Please enter a valid number from the list.");
                }
            } else {
                System.out.println("It appears you aren't in our system. Would you like to open an account?");
                System.out.println("Please enter 0 for no, or 1 for yes:");
                while (true) {
                    int response = FormValidation.getNumericInput();
                    if (response == 0) {
                        System.out.println("Please enter your name:");
                        break;
                    } else if (response == 1) {
                        if (!performOpenAccount()) {
                            System.out.println("Please enter your name:");
                        }
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
            default:
                throw new IllegalArgumentException("Invalid choice entered. Found " + choice);
        }
    }

    /**
     * @return True if the account was successfully created or false if it was cancelled.
     */
    private boolean performOpenAccount() {
        System.out.println("Thank you for wanting to sign up with Jog!");
        System.out.println("Please enter your name, or enter -q to return");
        String name = getName();
        if (name.equals("-q")) {
            return false;
        }
        System.out.println("Please enter your address:");
        String address = getAddress();
        int desiredPhone = pickNewPhone();

        System.out.println("Please select a plan for your account:");
        System.out.printf("%-120s %d\n", "Resident - Base rate of $30/month. Includes 300 " +
                "minutes, 1000 texts, 5GB data. There are major overdraft fees.", 1);
        System.out.printf("%-120s %d\n", "Resident - As used. $0.04 per minute, $0.01 per " +
                "text, and $1.00 per GB of data", 2);
        while (true) {
            int response = FormValidation.getNumericInput();
            if (response > 2 || response < 1) {
                System.out.println("Please enter a valid response.");
            } else {
                String desiredPlan;
                if (response == 1) {
                    desiredPlan = "RESIDENT_LIMIT";
                } else {
                    desiredPlan = "RESIDENT_AS_USED";
                }
                if (customerInStoreDatabase.createAccount(name, address, desiredPhone, storeNumber,
                        desiredPlan)) {
                    System.out.println("Congrats, your account as been successfully created!");
                    System.out.println("Welcome to Jog Wireless!");
                }
                System.out.println();
                return true;
            }
        }
    }

    private void performUpgradePhone() {
        System.out.println("Here are the different models from which you may choose:");
        Object[][] phonesForSale = customerInStoreDatabase.getPhoneModelsForSale();
        System.out.println("Please enter the Phone ID of the phone you would like to buy, or enter -1 to return:");
        while (true) {
            int response = FormValidation.getNumericInput();
            if (response == -1) {
                return;
            } else if (customerInStoreDatabase.isPhoneStocked(response, phonesForSale)) {
                int phoneToBuy = (int) phonesForSale[response - 1][0];
                System.out.println("Please select which of your phones you would like to upgrade:");
                Object[][] userPhones = customerInStoreDatabase.getCustomerPhones(id);
                while (true) {
                    response = FormValidation.getNumericInput();
                    if (customerInStoreDatabase.doesUserOwnPhone(response, userPhones)) {
                        long oldMeid = (long) userPhones[response - 1][0];
                        long phoneNumber = (long) userPhones[response - 1][3];
                        if (customerInStoreDatabase.replaceNewPhone(phoneToBuy, id, oldMeid, phoneNumber, storeNumber)) {
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
            int response = FormValidation.getNumericInput();
            if (response == -1) {
                return;
            } else if (response < 1 || response > 3) {
                System.out.println("Please enter a valid choice.");
            } else {
                Object[][] phones = customerInStoreDatabase.getCustomerPhones(id);
                if (phones == null) {
                    System.out.println("You have no phones to report!");
                    System.out.println();
                    return;
                }
                switch (response) {
                    case 1:
                        System.out.println("Please select a phone to be reported as lost: ");
                        while (true) {
                            response = FormValidation.getNumericInput();
                            if (customerInStoreDatabase.doesUserOwnPhone(response, phones)) {
                                if (customerInStoreDatabase.reportLostPhone(phones[response - 1])) {
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
                        System.out.println("Please select a phone to be reported as stolen: ");
                        while (true) {
                            response = FormValidation.getNumericInput();
                            if (customerInStoreDatabase.doesUserOwnPhone(response, phones)) {
                                if (customerInStoreDatabase.reportStolenPhone(phones[response - 1])) {
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
                        System.out.println("Please select a phone to be reported as found: ");
                        while (true) {
                            response = FormValidation.getNumericInput();
                            if (customerInStoreDatabase.doesUserOwnPhone(response, phones)) {
                                if (customerInStoreDatabase.reportFoundPhone(phones[response - 1])) {
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
        Object[][] customerAccounts = customerInStoreDatabase.getCustomerAccounts(id);
        if (customerAccounts == null) {
            System.out.println();
            return;
        }
        System.out.println("Enter the account ID of the account you would like to add a customer, or -1 to return:");
        while (true) {
            int accountId = FormValidation.getNumericInput();
            if (accountId == -1) {
                return;
            } else if (customerInStoreDatabase.isAccountIdValid(customerAccounts, accountId)) {
                System.out.println("Enter the name of the new person you would like to add to your account, or enter " +
                        "-s to search from our existing customer base: ");
                String name = getName();
                if (name.equals("-s")) {
                    while (true) {
                        System.out.println("Please enter the name of the customer you would like to find:");
                        name = getName();
                        if (customerInStoreDatabase.getCustomerIdsForName(name)) {
                            System.out.println("Please enter the customer\'s ID from the list or enter -1 to research:");
                            int id = FormValidation.getNumericInput();
                            if (customerInStoreDatabase.isValidCustomerId(id)) {
                                name = customerInStoreDatabase.getNameFromCustomerId(id);
                                String address = customerInStoreDatabase.getAddressFromCustomerId(id);
                                int desiredPhone = pickNewPhone();
                                if (customerInStoreDatabase.addCustomerToAccount(id, name, address, desiredPhone,
                                        accountId, storeNumber)) {
                                    System.out.println("Successfully added the " + name + " to your account!");
                                }
                                System.out.println();
                                return;
                            } else if (id != -1) {
                                System.out.println("Please enter a valid customer ID from the list.");
                            }
                        }
                    }
                } else {
                    System.out.println("Please enter the person\'s address:");
                    String address = getAddress();
                    int desiredPhone = pickNewPhone();
                    if (customerInStoreDatabase.addCustomerToAccount(-1, name, address, desiredPhone,
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

    private int pickNewPhone() {
        Object[][] phonesForSale = customerInStoreDatabase.getPhoneModelsForSale();
        System.out.println("Please enter the Phone ID of the phone you would like to buy:");
        while (true) {
            int desiredPhone = FormValidation.getNumericInput();
            if (desiredPhone >= 1 && desiredPhone <= phonesForSale.length) {
                return desiredPhone;
            } else {
                System.out.println("Please enter a valid phone choice.");
            }
        }
    }

    private String getAddress() {
        String address;
        while (true) {
            address = scanner.nextLine();
            if (address.length() >= 5) {
                return address;
            } else {
                System.out.println("The address must be longer!");
            }
        }
    }

    private String getName() {
        String name;
        while (true) {
            name = scanner.nextLine();
            if (name.length() >= 2) {
                return name;
            } else {
                System.out.println("Your name must be at least two characters long!");
            }
        }
    }

}
