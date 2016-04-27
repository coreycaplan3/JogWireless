package interfaces;

import database.SalesClerkDatabase;
import validation.FormValidation;

/**
 * An interface used to represent the transactions that would occur with a sales clerk in one of Jog's walk-in
 * stores.
 */
public class SalesClerkInterface extends BaseInterface {

    private SalesClerkDatabase salesClerkDatabase;
    private int storeNumber = -1;

    public SalesClerkInterface() {
        System.out.println("**********************************************************************");
        System.out.println("Greetings Jog manager!");
        salesClerkDatabase = new SalesClerkDatabase();
    }

    @Override
    public boolean performTransaction() {
        if (storeNumber == -1) {
            getStoreNumber();
        }

        System.out.println("Please enter an option:");
        System.out.printf("%-50s %d\n", "See your store\'s inventory", 1);
        System.out.printf("%-50s %d\n", "Replenish your store\'s inventory", 2);
        System.out.printf("%-50s %d\n", "Go back to the main menu", -1);
        while (true) {
            int choice = FormValidation.getIntegerInput("Please enter an option:", 5);
            if (choice == 1) {
                performGetInventory();
                break;
            } else if (choice == 2) {
                performReplenishInventory();
                break;
            } else if (choice == -1) {
                return true;
            } else {

            }
        }
        return false;
    }

    private void getStoreNumber() {
        while (true) {
            int choice = FormValidation.getIntegerInput("To start, please enter a store number between 2 and 10", 12);
            if (choice < 2 || choice > 10) {
                System.out.println("Please enter a valid store number.");
            } else {
                storeNumber = choice;
                return;
            }
        }
    }

    private void performGetInventory() {
        Object[][] phones = salesClerkDatabase.getPhoneModels();
        int phoneId = getPhoneId(phones);
        salesClerkDatabase.getInventory(storeNumber, phoneId);
        System.out.println("Returning to the selection screen...");
        System.out.println();
    }

    private void performReplenishInventory() {
        Object[][] phones = salesClerkDatabase.getPhoneModels();
        int phoneId = getPhoneId(phones);
        int quantity;
        while (true) {
            quantity = FormValidation.getIntegerInput("How many phones would you like to purchase for your store?",
                    100);
            if(quantity < 1) {
                System.out.println("Please enter a valid quantity.");
            } else {
                break;
            }
        }
        salesClerkDatabase.buyMoreInventory(storeNumber, phoneId, quantity);
    }

    private int getPhoneId(Object[][] phones) {
        while (true) {
            int phoneId = FormValidation.getIntegerInput("Please enter the ID of the phone whose inventory you\'d " +
                    "like to see:", 1000000);
            for (Object[] phone : phones) {
                if (((Integer) phone[0]) == phoneId) {
                    return phoneId;
                }
            }
            System.out.println("Invalid ID entered!");
        }
    }

}
