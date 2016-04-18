package validation;

import java.util.Scanner;

/**
 * A utility class used for validation and sanitizing user input.
 */
public final class FormValidation {

    private static Scanner scanner = new Scanner(System.in);

    private FormValidation() {
    }

    /**
     * This method loops indefinitely until the user enters a valid number.
     *
     * @return A number that the user entered.
     */
    public static int getNumericInput() {
        while (true) {
            String s = scanner.nextLine();
            if (isNumberValid(s)) {
                return Integer.parseInt(s);
            }
        }
    }

    /**
     * Checks if the String the user entered is valid.
     *
     * @param userInput The string the user entered.
     * @return True if it's valid or false if it's not. Prints out an error message if the number is invalid as well.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static boolean isNumberValid(String userInput) {
        try {
            Integer.parseInt(userInput);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
            return false;
        }
        return true;
    }

}
