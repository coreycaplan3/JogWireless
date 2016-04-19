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
     * @param prompt The prompt to display to the user before requesting information.
     * @return A number that the user entered.
     */
    public static int getNumericInput(String prompt) {
        System.out.println(prompt);
        while (true) {
            String s = scanner.nextLine();
            if (isNumberValid(s)) {
                return Integer.parseInt(s);
            }
        }
    }

    /**
     * @param prompt        The prompt to display to the user before requesting information.
     * @param desiredResult The type of string desired by the user. Can be a name, address, etc.
     * @return A string that was entered by the user and has been sanitized of all things that could negatively
     * effect the insertion of data into the database.
     */
    public static String getStringInput(String prompt, String desiredResult) {
        System.out.println(prompt);
        while (true) {
            String s = scanner.nextLine();
            if (s.contains("\'") || s.contains("\"")) {
                System.out.println("Please enter a valid " + desiredResult);
            } else if (s.length() < 2) {
                System.out.println("Sorry, your input was too short.");
            } else {
                return s;
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
