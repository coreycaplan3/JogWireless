package validation;

import database.DatabaseInitializer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

/**
 * A utility class used for validation and sanitizing user input.
 */
public final class FormValidation {

    private static Scanner scanner = new Scanner(System.in);

    private FormValidation() {
    }

    /**
     * @return A string in the form of <i>2016-01-20 11:15:23</i> that represents the current date.
     */
    public static String getDate() {
        Calendar calendar = Calendar.getInstance();
        return getOnlyDateForDatabase(calendar) + " " + getOnlyTimeForDatabase(calendar);
    }

    /**
     * This method loops indefinitely until the user enters a valid integer.
     *
     * @param prompt The prompt to display to the user before requesting information.
     * @return An integer that the user entered.
     */
    public static int getIntegerInput(String prompt, int maxNumber) {
        while (true) {
            System.out.println(prompt);
            String s = scanner.nextLine();
            if (isIntegerValid(s)) {
                if (Integer.parseInt(s) < maxNumber) {
                    return Integer.parseInt(s);
                } else {
                    System.out.println("Please enter a number that is less than " + maxNumber);
                }
            }
        }
    }

    /**
     * This method loops indefinitely until the user enters a valid double.
     *
     * @param prompt The prompt to display to the user before requesting information.
     * @return A double that the user entered.
     */
    public static double getDoubleInput(String prompt, int maxNumber) {
        while (true) {
            System.out.println(prompt);
            String s = scanner.nextLine();
            if (isDoubleValid(s)) {
                if (Double.parseDouble(s) < maxNumber) {
                    return Double.parseDouble(s);
                } else {
                    System.out.println("Please enter a number that is less than " + maxNumber);
                }
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
            int choice = getIntegerInput("Please enter 0 for no or 1 for yes.", 2);
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
            if (!isValidString(s)) {
                System.out.println("Invalid character detected! Please enter a valid " + desiredResult + ".");
            } else if (s.length() < 2) {
                System.out.println("Sorry, your input was too short.");
            } else if (s.length() >= maxLength) {
                System.out.println("Sorry, your input was too long.");
            } else if (s.trim().equalsIgnoreCase("null")) {
                System.out.println("You cannot enter null as a string!");
            } else {
                return s;
            }
        }
    }

    private static boolean isValidString(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isLetterOrDigit(s.charAt(i)) && !Character.isSpaceChar(s.charAt(i)) && s.charAt(i) != '-'
                    && s.charAt(i) != '(' && s.charAt(i) != ')' && s.charAt(i) != '_') {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the String the user entered is valid.
     *
     * @param userInput The string the user entered.
     * @return True if it's valid or false if it's not. Prints out an error message if the number is invalid as well.
     */
    @SuppressWarnings({"ResultOfMethodCallIgnored", "Duplicates"})
    private static boolean isIntegerValid(String userInput) {
        try {
            Integer.parseInt(userInput);
            return true;
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
            return false;
        }
    }

    /**
     * Checks if the String the user entered is valid.
     *
     * @param userInput The string the user entered.
     * @return True if it's valid or false if it's not. Prints out an error message if the number is invalid as well.
     */
    @SuppressWarnings({"ResultOfMethodCallIgnored", "Duplicates"})
    private static boolean isDoubleValid(String userInput) {
        try {
            Double.parseDouble(userInput);
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
            String billingPeriod = getStringInput("Please enter the date in the form of \"yyyy-MM\"", "date", 8);
            if (billingPeriod.length() != 7) {
                System.out.println("Incorrect formatting of the date.");
            } else {
                if (isDateValid(billingPeriod, false)) {
                    return billingPeriod + "-01 00:00:00";
                }
            }
        }
    }

    private static boolean isDateValid(String dateToCheck, boolean containsDay) {
        //Assumes the proper formatting is "yyyy-MM-dd"
        if (dateToCheck.length() != 7 && dateToCheck.length() != 10) {
            System.out.println("Please enter the date with valid formatting.");
            return false;
        }
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
            if (containsDay) {
                if (dateToCheck.length() != 10) {
                    System.out.println("Please enter the date with valid formatting.");
                    return false;
                }
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month - 1);

                int day = Integer.parseInt(dateToCheck.substring(8, 10));
                if (day < 1 || day > calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    System.out.println("Please enter a valid day of the month.");
                    return false;
                }
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
     * Gets a start date and time that can be read by the database. The String takes the form as
     * "yyyy-MM-dd HH24:mi:ss" so it an be converted to an SQL date object.
     *
     * @param prompt The prompt that should be displayed to the user.
     * @return A String containing the start date in the form "yyyy-MM-dd HH:mm:ss".
     */
    public static String getUsageStartDate(String prompt) {
        System.out.println(prompt);
        System.out.println("The date must be in the form \"yyyy-MM-dd\" in order to be valid.");
        while (true) {
            String usageDate = scanner.nextLine().trim();
            if (isDateValid(usageDate, true)) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date date;
                try {
                    date = format.parse(usageDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return null;
                }
                int randomHour = (int) (Math.random() * 24);
                int randomMinute = (int) (Math.random() * 60);
                int randomSecond = (int) (Math.random() * 60);

                Calendar startDate = Calendar.getInstance();
                startDate.setTimeInMillis(date.getTime());
                startDate.set(Calendar.HOUR_OF_DAY, randomHour);
                startDate.set(Calendar.MINUTE, randomMinute);
                startDate.set(Calendar.SECOND, randomSecond);

                return getFullDateForDatabase(startDate);
            }
        }
    }

    /**
     * Parses the startDate and returns an appropriate end date formatted for the database in the form
     * "yyyy-MM-dd HH:mm:ss"
     *
     * @param startDate       A sanitized start date in proper database format.
     * @param durationSeconds The distance between the start date and end date in seconds.
     * @return A formatted string that contains the same time as the inputted start date plus the number of seconds
     * added.
     */
    public static String getUsageEndDate(String startDate, int durationSeconds) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        try {
            date = format.parse(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime() + (durationSeconds * 1000));
        return getFullDateForDatabase(calendar);
    }

    /**
     * @param calendar A {@link Calendar} object that holds the event's "calendar" date.
     * @return A String in the form of "2015-01-19 02:45:00" for the database.
     */
    private static String getFullDateForDatabase(Calendar calendar) {
        return getOnlyDateForDatabase(calendar) + " " + getOnlyTimeForDatabase(calendar);
    }

    /**
     * Parses a given {@link Calendar} object to match the format that the database expects.
     *
     * @param calendar The {@link Calendar} object that needs to be parsed.
     * @return A formatted String that meets the formatting specifications of the database in the
     * form of "2015-01-19".
     */
    private static String getOnlyDateForDatabase(Calendar calendar) {
        return String.format(Locale.US, "%1$tY-%1$tm-%1$td", calendar);
    }

    /**
     * Parses a given {@link Calendar} object's time field to match the format that the database expects.
     *
     * @param calendar The {@link Calendar} object that needs to be parsed.
     * @return The time in the form of a String, like "02:45:00".
     */
    private static String getOnlyTimeForDatabase(Calendar calendar) {
        return String.format("%1$tH:%1$tM:%1$tS", calendar);
    }

}