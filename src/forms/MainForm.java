package forms;

import database.DatabaseInitializer;
import interfaces.*;
import validation.FormValidation;

import java.util.Scanner;

/**
 *
 */
public class MainForm {

    public static void main(String[] args) {
        System.out.println("Welcome to Corey\'s CSE 341 Project. To begin, please enter cdc218\'s password");
        String username = "cdc218";
        String password;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Password: ");
            password = scanner.nextLine();
            if (DatabaseInitializer.login(username, password)) {
                System.out.println("Login successful!");
                break;
            } else {
                System.out.println("Login failed, try again!");
            }
        }
        while (true) {
            System.out.println("Select an interface to use:");
            System.out.printf("%-75s %d\n", "A residential customer walking into a store", 1);
            System.out.printf("%-75s %d\n", "A sales clerk/cashier working in a store", 2);
            System.out.printf("%-75s %d\n", "An accountant or CFO looking at financial information", 3);
            System.out.printf("%-75s %d\n", "A residential customer trying to get started with Jog online", 4);
            System.out.printf("%-75s %d\n", "A business hoping to get started with Jog", 5);
            System.out.printf("%-75s %d\n", "A business managing its account information", 6);
            System.out.printf("%-75s %d\n", "Send text messages, make phone calls, or use the internet as any " +
                    "customer", 7);
            System.out.printf("%-75s %d\n", "Quit this program", -1);
            int response = FormValidation.getNumericInput("");
            if (response == -1) {
                break;
            } else if (response >= 1 && response <= 7) {
                BaseInterface baseInterface = getInterfaceForResponse(response);
                while (true) {
                    if (baseInterface.performTransaction()) {
                        System.out.println("Welcome back to the home screen!");
                        break;
                    }
                }
            } else {
                System.out.println("Invalid number entered, try again!");
            }
        }
        System.out.println("Thank you for running with Jog. We hope to see you again soon. Good Bye!");
        DatabaseInitializer.logout();
    }

    private static BaseInterface getInterfaceForResponse(int choice) {
        switch (choice) {
            case 1:
                return new CustomerInStoreInterface();
            case 2:
                return new SalesClerkInterface();
            case 3:
                return new AccountantInterface();
            case 4:
                return new NewCustomerInterface();
            case 5:
                return new NewBusinessInterface();
            case 6:
                return new BusinessManagingInterface();
            case 7:
                return new UsePhoneInterface();
            default:
                throw new IllegalArgumentException("Invalid choice entered!");
        }
    }
}
