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
     * Gets true or false input from the user.
     *
     * @return True or false...
     */
    public static boolean getTrueOrFalse() {
        while (true) {
            int choice = getNumericInput("Please enter 0 for no or 1 for yes.");
            if (choice != 0 && choice != 1) {
                System.out.println("Please enter a valid option.");
            } else {
                return choice == 1;
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
            return true;
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
            return false;
        }
    }

    /**
     * Retrieves a valid string that can be sent to the database for parsing as a date. The string is returned in the
     * form of yyyy-MM-dd HH:mm:ss
     *
     * @param prompt The prompt that should be displayed to the user upon asking him/her for the billing period.
     * @return A string that has been properly formatted for insertion/retrieval from SQL. It's in the form
     * yyyy-MM-dd HH:mm:ss
     */
    public static String getBillingPeriod(String prompt) {
        System.out.println(prompt);
        while (true) {
            System.out.println("Please enter the date in the form of yyyy-MM:");
            String billingPeriod = scanner.nextLine().trim();
            if (billingPeriod.length() != 7) {
                System.out.println("Incorrect formatting of the date.");
            } else {
                if (isDateValid(billingPeriod)) {
                    return billingPeriod + "-01 00:00:00";
                }
            }
        }
    }

    private static boolean isDateValid(String dateToCheck) {
        //Assumes the proper formatting is "yyyy-MM-dd"
        try {
            int year = Integer.parseInt(dateToCheck.substring(0, 4));
            int month = Integer.parseInt(dateToCheck.substring(5, 7));
            if (year < 1900 || year > 2100) {
                System.out.println("Please enter a valid year.");
                return false;
            }
            if (month < 1 || month > 12) {
                System.out.println("Please enter a valid month.");
                return false;
            }
            if (dateToCheck.charAt(4) != '-') {
                System.out.println("Please be sure to enter the \"-\" between the year and the month.");
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number for the year and month.");
            return false;
        }
    }

    /**
     * Retrieves a phone number from the user.
     *
     * @param prompt The prompt that should be displayed to the user when entering the phone number.
     * @return The phone number as a long.
     */
    public static long getPhoneNumber(String prompt) {
        System.out.println(prompt);
        System.out.println("The phone number can be in any form, but it must contain 10 digits.");
        System.out.println("Some valid forms include \"(XXX) XXX XXXX\" or \"XXX-XXX-XXXX\"");
        while (true) {
            String phoneNumber = "";
            String input = scanner.nextLine();
            for (int i = 0; i < input.length(); i++) {
                if (Character.isDigit(input.charAt(i))) {
                    phoneNumber += input.charAt(i);
                }
            }
            if (phoneNumber.length() == 10) {
                return Long.parseLong(phoneNumber);
            } else {
                System.out.println("Please enter a valid 10 digit phone number.");
            }
        }
    }

    /**
     * Gets a start and end date from the user that can be used for any of the usage tables. The
     *
     * @param prompt The prompt that should be displayed to the user.
     * @return A String array containing the start date and end date in the 0 and 1st index respectively.
     */
    public static String[] getUsageDate(String prompt) {
        System.out.println(prompt);
        System.out.println("The date must be in the form \"yyyy-MM-dd\" in order to be valid.");
        while (true) {
            String usageDate = scanner.nextLine().trim();
            if (isDateValid(usageDate)) {
                int randomHour = (int) (Math.random() * 12) + 10;
                int randomMinute = (int) (Math.random() * 50);
                int randomSecond = (int) (Math.random() * 20);
                usageDate += (randomHour + "") + ()
                return usageDate;
            }
        }
    }

}