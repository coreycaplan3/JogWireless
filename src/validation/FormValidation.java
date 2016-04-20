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
        while (true) {
            System.out.println(prompt);
            String s = scanner.nextLine();
            if (isNumberValid(s)) {
                return Integer.parseInt(s);
            }
        }
    }

    /**
     * @param prompt        The prompt to display to the user before requesting information.
     * @param desiredResult The type of string desired by the user. Can be a name, address, etc.
     * @param maxLength     The maximum length that the string can be.
     * @return A string that was entered by the user and has been sanitized of all things that could negatively
     * effect the insertion of data into the database.
     */
    public static String getStringInput(String prompt, String desiredResult, int maxLength) {
        while (true) {
            System.out.println(prompt);
            String s = scanner.nextLine();
            if (s.contains("\'") || s.contains("\"")) {
                System.out.println("Please enter a valid " + desiredResult);
            } else if (s.length() < 2) {
                System.out.println("Sorry, your input was too short.");
            } else if (s.length() >= maxLength) {
                System.out.println("Sorry, your input was too long.");
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

    /**
     * Retrieves a valid string that can be sent to the database for parsing as a date. The string is returned in the
     * form of yyyy-MM-dd HH:mm:ss
     *
     * @param prompt
     * @return
     */
    public static String getBillingPeriod(String prompt) {
        System.out.println(prompt);
        while (true) {
            System.out.println("Please enter the date in the form of yyyy-MM:");
            String billingPeriod = scanner.nextLine().trim();
            if (billingPeriod.length() != 7) {
                System.out.println("Incorrect formatting of the date.");
            } else {
                try {
                    int year = Integer.parseInt(billingPeriod.substring(0, 4));
                    int month = Integer.parseInt(billingPeriod.substring(5, 7));
                    if (year < 1900 || year > 2100) {
                        System.out.println("Please enter a valid year.");
                        continue;
                    }
                    if (month < 1 || month > 12) {
                        System.out.println("Please enter a valid month.");
                        continue;
                    }
                    if (billingPeriod.charAt(4) != '-') {
                        System.out.println("Please be sure to enter the dash between the year and the month.");
                        continue;
                    }
                    billingPeriod += "-01 00:00:00";
                    return billingPeriod;
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number for the year and month.");
                }
            }
        }
    }

}
