package interfaces;

import database.CustomerUsageDatabase;
import validation.FormValidation;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;

/**
 * Created by coreycaplan on 4/28/16.
 * <p></p>
 * An interface used to represent the transactions that occur when a user of the program wants to manually upload
 * customer usage information to the database.
 */
public class StreamInputInterface extends BaseInterface {

    private CustomerUsageDatabase customerUsageDatabase;

    private TreeMap<Long, Integer> accountHashMap;

    private enum UsageType {
        TYPE_TEXT, TYPE_CALL, TYPE_INTERNET, TYPE_COMMENT, TYPE_UNKNOWN, TYPE_INVALID_FORMAT, TYPE_NO_ACCOUNT
    }

    public StreamInputInterface() {
        System.out.println("Welcome to the Jog Wireless data stream!");
        customerUsageDatabase = new CustomerUsageDatabase();
        accountHashMap = customerUsageDatabase.getAllPhoneNumbersWithAccounts();
    }

    @Override
    public boolean performTransaction() {
        System.out.println("To begin please enter a file name that contains customer usage information:");
        System.out.println("Note, please be sure it is in the usage folder!");
        while (true) {
            String fileName = FormValidation.getStringInput("File Name:", "file", 150);
            fileName = "usage/" + fileName;
            BufferedReader bufferedReader = null;
            FileReader inputFile = null;

            FileWriter errorWriter = null;
            FileWriter databaseWriter = null;
            try {
                inputFile = new FileReader(fileName);
                bufferedReader = new BufferedReader(inputFile);
                errorWriter = new FileWriter("error.log");
                databaseWriter = new FileWriter("usage_information.log");
                String line;
                int lineCount = 0;
                while ((line = bufferedReader.readLine()) != null) {
                    lineCount++;
                    UsageType usageType = getUsageType(line);
                    String[] information;
                    if (usageType == UsageType.TYPE_TEXT) {
                        information = getTextInformation(line);
                        long sourcePhone = Long.parseLong(information[0]);
                        if (accountHashMap.get(sourcePhone) == null) {
                            printError(UsageType.TYPE_NO_ACCOUNT, lineCount, line, errorWriter);
                        }

                        long destPhone = Long.parseLong(information[1]);
                        String endTime = FormValidation.getUsageEndDate(information[2], 1);
                        String startTime = information[2];
                        int bytes = Integer.parseInt(information[3]);
                        customerUsageDatabase.sendTextMessage(sourcePhone, destPhone, startTime, endTime, bytes);
                    } else if (usageType == UsageType.TYPE_CALL) {
                        information = getCallInformation(line);
                        customerUsageDatabase.sendPhoneCall(Long.parseLong(information[0]),
                                Long.parseLong(information[1]), information[2], information[3]);
                    } else if (usageType == UsageType.TYPE_INTERNET) {
                        information = getInternetInformation(line);
                        customerUsageDatabase.useInternet(Long.parseLong(information[0]), information[1],
                                Integer.parseInt(information[2]));
                    } else {
                        printError(usageType, lineCount, line, errorWriter);
                    }
                }
                break;
            } catch (FileNotFoundException e) {
                System.out.println("Sorry, \"" + fileName + "\" could not be found. Please try again.");
            } catch (IOException e) {
                System.out.println("Sorry there was an error reading that file. Please enter a different one.");
            } finally {
                try {
                    if (inputFile != null) {
                        inputFile.close();
                    }
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                    if (errorWriter != null) {
                        errorWriter.close();
                    }
                    if (databaseWriter != null) {
                        databaseWriter.close();
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return true;
    }

    private void printError(UsageType typeOfError, int lineCount, String line, FileWriter errorWriter) {
        String errorType;
        if (UsageType.TYPE_INVALID_FORMAT == typeOfError) {
            errorType = "<INVALID FORMATTING>";
        } else {
            errorType = "<UNKNOWN USAGE TYPE>";
        }
        String error = new Date().toString() + " - Error at line <" + lineCount + "> " + errorType +
                " : " + line;
        try {
            errorWriter.write(error);
        } catch (IOException ignored) {
        }
    }

    private UsageType getUsageType(String line) {
        if (line == null || line.trim().length() == 0) {
            return UsageType.TYPE_INVALID_FORMAT;
        }

        if (line.charAt(0) == '-') {
            return UsageType.TYPE_COMMENT;
        }

        String[] tokens = line.split("\\s");
        if (tokens.length == 0 || tokens[0] == null) {
            return UsageType.TYPE_INVALID_FORMAT;
        }

        switch (tokens[0]) {
            case "TEXT":
                return verifyText(tokens);
            case "CALL":
                return verifyCall(tokens);
            case "INTERNET":
                return verifyInternet(tokens);
            default:
                return UsageType.TYPE_UNKNOWN;
        }
    }

    private UsageType verifyText(String[] tokens) {
        if (tokens.length != 4) {
            return UsageType.TYPE_INVALID_FORMAT;
        }

        if (!isPhoneValid(tokens[1])) {
            return UsageType.TYPE_INVALID_FORMAT;
        }

        if (!isPhoneValid(tokens[2])) {
            return UsageType.TYPE_INVALID_FORMAT;
        }

        if (!isTimeValid(tokens[3])) {
            return UsageType.TYPE_INVALID_FORMAT;
        }

        if (!isBytesValid(tokens[4])) {
            return UsageType.TYPE_INVALID_FORMAT;
        }
        return UsageType.TYPE_TEXT;
    }

    private boolean isBytesValid(String rawBytes) {
        if (rawBytes == null || rawBytes.length() < 2) {
            return false;
        }

        if (!rawBytes.substring(rawBytes.length() - 1).equals("B")) {
            return false;
        }

        int bytes;
        try {
            bytes = Integer.parseInt(rawBytes.substring(0, rawBytes.length() - 1));
        } catch (NumberFormatException e) {
            return false;
        }
        return !(bytes < 0 || bytes > 1000000);
    }

    private boolean isPhoneValid(String rawPhone) {
        if (rawPhone == null || rawPhone.length() == 0) {
            return false;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < rawPhone.length(); i++) {
            if (Character.isDigit(rawPhone.charAt(i))) {
                builder.append(rawPhone.charAt(i));
            }
        }
        String phone = builder.toString();
        return phone.length() == 10;
    }

    private boolean isTimeValid(String rawTime) {
        if (rawTime == null) {
            return false;
        }
        String[] dateTimePieces = rawTime.split(";");
        if (dateTimePieces.length != 2) {
            return false;
        }

        String[] datePieces = dateTimePieces[0].split("-");
        if (datePieces.length != 3) {
            return false;
        }

        String[] timePieces = dateTimePieces[1].split(":");
        if (timePieces.length != 3) {
            return false;
        }

        Calendar calendar = Calendar.getInstance();
        int year;
        int month;
        int day;
        int hour;
        int minute;
        int second;

        try {
            year = Integer.parseInt(datePieces[0]);
        } catch (NumberFormatException e) {
            return false;
        }
        if (year < 1900 || year > 2100) {
            return false;
        }
        calendar.set(Calendar.YEAR, year);

        try {
            month = Integer.parseInt(datePieces[1]);
        } catch (NumberFormatException e) {
            return false;
        }
        if (month < 1 || month > 12) {
            return false;
        }
        calendar.set(Calendar.MONTH, month);

        try {
            day = Integer.parseInt(datePieces[2]);
        } catch (NumberFormatException e) {
            return false;
        }
        if (day < 1 || day > calendar.getMaximum(Calendar.DAY_OF_MONTH)) {
            return false;
        }

        try {
            hour = Integer.parseInt(timePieces[0]);
        } catch (NumberFormatException e) {
            return false;
        }
        if (hour < 0 || hour > 23) {
            return false;
        }

        try {
            minute = Integer.parseInt(timePieces[1]);
        } catch (NumberFormatException e) {
            return false;
        }
        if (minute < 0 || minute > 59) {
            return false;
        }

        try {
            second = Integer.parseInt(timePieces[2]);
        } catch (NumberFormatException e) {
            return false;
        }

        return !(second < 0 || second > 59);
    }

    private UsageType verifyCall(String[] tokens) {
        if (tokens.length != 4) {
            return UsageType.TYPE_INVALID_FORMAT;
        }

        if (!isPhoneValid(tokens[1])) {
            return UsageType.TYPE_INVALID_FORMAT;
        }

        if (!isPhoneValid(tokens[2])) {
            return UsageType.TYPE_INVALID_FORMAT;
        }

        if (!isTimeValid(tokens[3])) {
            return UsageType.TYPE_INVALID_FORMAT;
        }
        String[] timePieces = tokens[3].split(";");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate;
        try {
            startDate = dateFormat.parse(timePieces[0] + " " + timePieces[2]);
        } catch (ParseException e) {
            return UsageType.TYPE_INVALID_FORMAT;
        }

        if (!isTimeValid(tokens[4])) {
            return UsageType.TYPE_INVALID_FORMAT;
        }
        timePieces = tokens[4].split(";");
        Date endDate;
        try {
            endDate = dateFormat.parse(timePieces[0] + " " + timePieces[2]);
        } catch (ParseException e) {
            return UsageType.TYPE_INVALID_FORMAT;
        }

        //Check that the dates make sense (end date is after the start date)
        if (endDate.getTime() - startDate.getTime() <= 0) {
            return UsageType.TYPE_INVALID_FORMAT;
        }

        return UsageType.TYPE_CALL;
    }

    private UsageType verifyInternet(String[] tokens) {
        if (tokens.length != 3) {
            return UsageType.TYPE_INVALID_FORMAT;
        }

        if (!isPhoneValid(tokens[1])) {
            return UsageType.TYPE_INVALID_FORMAT;
        }

        if (!isTimeValid(tokens[2])) {
            return UsageType.TYPE_INVALID_FORMAT;
        }

        if (!isInternetBytesValid(tokens[3])) {
            return UsageType.TYPE_INVALID_FORMAT;
        }

        return UsageType.TYPE_INTERNET;
    }

    private boolean isInternetBytesValid(String rawMegabytes) {
        if (rawMegabytes == null || rawMegabytes.length() < 2) {
            return false;
        }

        if (!rawMegabytes.substring(rawMegabytes.length() - 2).equals("MB")) {
            return false;
        }

        int megabytes;
        try {
            megabytes = Integer.parseInt(rawMegabytes.substring(0, rawMegabytes.length() - 2));
        } catch (NumberFormatException e) {
            return false;
        }
        return !(megabytes <= 0 || megabytes > 100000);
    }

    private String[] getTextInformation(String sanitizedLine) {
        String[] tokens = sanitizedLine.split("\\s");
        String sourcePhone = getPhone(tokens[1]);
        String destPhone = getPhone(tokens[2]);
        String time = getTime(tokens[3]);
        String bytes = tokens[4].substring(0, tokens[4].length() - 1);
        return new String[]{sourcePhone, destPhone, time, bytes};
    }

    private String[] getCallInformation(String sanitizedLine) {
        String[] tokens = sanitizedLine.split("\\s");
        String sourcePhone = getPhone(tokens[1]);
        String destPhone = getPhone(tokens[2]);
        String startTime = getTime(tokens[3]);
        String endTime = getTime(tokens[4]);
        return new String[]{sourcePhone, destPhone, startTime, endTime};
    }

    private String[] getInternetInformation(String sanitizedLine) {
        String[] tokens = sanitizedLine.split("\\s");
        String sourcePhone = getPhone(tokens[1]);
        String timeUsed = getTime(tokens[2]);
        String megabytes = tokens[3].substring(0, tokens[3].length() - 2);
        return new String[]{sourcePhone, timeUsed, megabytes};
    }

    private String getPhone(String sanitizedRawPhone) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < sanitizedRawPhone.length(); i++) {
            if (Character.isDigit(sanitizedRawPhone.charAt(i))) {
                builder.append(sanitizedRawPhone.charAt(i));
            }
        }
        return builder.toString();
    }

    private String getTime(String sanitizedRawDate) {
        String[] tokens = sanitizedRawDate.split(";");
        return tokens[0] + " " + tokens[1];
    }

}
