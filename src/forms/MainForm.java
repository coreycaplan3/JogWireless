package forms;

import database.DatabaseInitializer;
import interfaces.BaseInterface;
import interfaces.CustomerInStoreInterface;
import validation.FormValidation;

import java.util.Scanner;

/**
 *
 */
public class MainForm {

    public static void main(String[] args) {
        System.out.println("Welcome to Corey\'s CSE 341 Project. To begin, please enter a username or password");
        String username;
        String password;
        String userResponse;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Username: ");
            username = scanner.nextLine();
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
            int x = 100;
            System.out.printf("%-45s %d\n", "A customer in a store", 1);
            System.out.printf("%-45s %d\n", "A sales clerk/cashier in a store", 2);
            System.out.printf("%-45s %d\n", "A manager of a store", 3);
            System.out.printf("%-45s %d\n", "A customer trying to get started with Jog", 4);
            System.out.printf("%-45s %d\n", "A customer changing account information", 5);
            System.out.printf("%-45s %d\n", "A business trying to get started with Jog", 6);
            System.out.printf("%-45s %d\n", "A business changing account information", 7);
            System.out.printf("%-45s %d\n", "Quit this program", -1);
            int response = FormValidation.getNumericInput();
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
        System.out.println("Thank you for using this program. Good Bye!");
    }

    private static BaseInterface getInterfaceForResponse(int choice) {
        switch (choice) {
            case 1:
                return new CustomerInStoreInterface();
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            default:
                throw new IllegalArgumentException("Invalid choice entered!");
        }
        return null;
    }

    /**
     * Starts the designated user interface.
     *
     * @param baseInterface The {@link BaseInterface} the user chose to work with
     * @return True if the user would like to go back to the home screen or false if the user would like to continue
     * navigating.
     */
    private static boolean restartInterface(BaseInterface baseInterface) {
        return baseInterface.performTransaction();
    }

}
