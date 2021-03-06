package interfaces;

import database.CustomerUsageDatabase;
import database.CustomerUsageDatabase.UsageResult;
import validation.FormValidation;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Created by coreycaplan on 4/28/16.
 * <p></p>
 * An interface used to represent the transactions that occur when a user of the program wants to manually upload
 * customer usage information to the database.
 */
public class StreamInputInterface extends BaseInterface {

    private CustomerUsageDatabase customerUsageDatabase;

    private TreeMap<Long, Integer> accountsTreeMap;

    private enum UsageType {
        TYPE_TEXT, TYPE_CALL, TYPE_INTERNET, TYPE_COMMENT, TYPE_UNKNOWN_USAGE, TYPE_INVALID_FORMAT, TYPE_INVALID_DATE,
        TYPE_INVALID_PHONE, TYPE_NO_ACCOUNT, TYPE_NO_SERVICE, TYPE_UNKNOWN_SQL_ERROR, TYPE_SOURCE_AND_DEST_SAME,
        TYPE_END_BEFORE_START, TYPE_TOO_MANY_BYTES, TYPE_TOO_MANY_MEGABYTES, TYPE_TOO_FEW_BYTES, TYPE_TOO_FEW_MEGABYTES,
        TYPE_INVALID_BYTES, TYPE_INVALID_MEGABYTES
    }

    public StreamInputInterface() {
        System.out.println("************** Welcome to the Jog Wireless data stream! **************");
        customerUsageDatabase = new CustomerUsageDatabase();
        accountsTreeMap = customerUsageDatabase.getAllPhoneNumbersWithAccounts();
    }

    @Override
    public boolean performTransaction() {
        System.out.println("To begin please enter a file name that contains customer usage information:");
        System.out.println("Note, please be sure it is in the \"usage\" folder!");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String fileName;
            System.out.println("Enter a file name, or -l to see the files in the \"usage\" directory. You may also " +
                    "type -q to quit:");
            fileName = scanner.nextLine();
            if (fileName.equals("-q")) {
                return true;
            } else if (fileName.equals("-l")) {
                File file = new File("usage/");
                File[] files = file.listFiles();
                if (files == null) {
                    System.out.println("Sorry, there are no files in the directory!");
                    continue;
                }
                System.out.println("Here are the files in the directory:");
                for (File file1 : files) {
                    System.out.println(file1.getName());
                }
                System.out.println();
                continue;
            }
            fileName = "usage/" + fileName;
            BufferedReader bufferedReader = null;
            FileReader inputFile = null;

            FileWriter errorWriter = null;
            FileWriter databaseWriter = null;
            try {
                inputFile = new FileReader(fileName);
                bufferedReader = new BufferedReader(inputFile);
                errorWriter = new FileWriter("usage/error.txt", true);
                databaseWriter = new FileWriter("usage/usage_information.txt", true);
                processFile(bufferedReader, databaseWriter, errorWriter);
                System.out.println("Finished processing file!");
                System.out.println("You may view a more detailed output of the successful operations in the " +
                        "\"usage_information.txt\" file.");
                System.out.println("You may view a more detailed output of the failed operations in the " +
                        "\"error.txt\" file.");
                System.out.println();
            } catch (FileNotFoundException e) {
                System.out.println("Sorry, \"" + fileName + "\" could not be found. Please try again.");
            } catch (IOException e) {
                System.out.println("Sorry, there was an error reading that file. Please enter a different one.");
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
    }

    private void processFile(BufferedReader bufferedReader, FileWriter databaseWriter, FileWriter errorWriter)
            throws IOException {
        String line;
        int lineCount = 0;
        while ((line = bufferedReader.readLine()) != null) {
            lineCount++;
            UsageType usageType = getUsageType(line);
            String[] information;
            UsageResult usageResult;
            if (usageType == UsageType.TYPE_TEXT) {
                information = getTextInformation(line);
                long sourcePhone = Long.parseLong(information[0]);
                long destinationPhone = Long.parseLong(information[1]);
                String endTime = FormValidation.getUsageEndDate(information[2], 1);
                String startTime = information[2];
                int bytes = Integer.parseInt(information[3]);
                if (!accountsTreeMap.containsKey(sourcePhone) && !accountsTreeMap.containsKey(destinationPhone)) {
                    printError(UsageType.TYPE_NO_ACCOUNT, lineCount, line, errorWriter);
                    continue;
                }
                usageResult = customerUsageDatabase.sendTextMessage(sourcePhone, destinationPhone, startTime, endTime,
                        bytes);
            } else if (usageType == UsageType.TYPE_CALL) {
                information = getCallInformation(line);
                long sourcePhone = Long.parseLong(information[0]);
                long destinationPhone = Long.parseLong(information[1]);
                String startTime = information[2];
                String endTime = information[3];
                if (!accountsTreeMap.containsKey(sourcePhone) && !accountsTreeMap.containsKey(destinationPhone)) {
                    printError(UsageType.TYPE_NO_ACCOUNT, lineCount, line, errorWriter);
                    continue;
                }
                usageResult = customerUsageDatabase.sendPhoneCall(sourcePhone, destinationPhone, startTime, endTime);
            } else if (usageType == UsageType.TYPE_INTERNET) {
                information = getInternetInformation(line);
                long sourcePhone = Long.parseLong(information[0]);
                String usageDate = information[1];
                int megabytes = Integer.parseInt(information[2]);
                if (!accountsTreeMap.containsKey(sourcePhone)) {
                    printError(UsageType.TYPE_NO_ACCOUNT, lineCount, line, errorWriter);
                    continue;
                }
                usageResult = customerUsageDatabase.useInternet(sourcePhone, usageDate, megabytes);
            } else if (usageType != UsageType.TYPE_COMMENT) {
                printError(usageType, lineCount, line, errorWriter);
                continue;
            } else {
                //A comment
                continue;
            }
            if (usageResult == UsageResult.SUCCESS) {
                printUsage(usageType, lineCount, line, databaseWriter);
            } else if (usageResult == UsageResult.NO_SERVICE) {
                printError(UsageType.TYPE_NO_SERVICE, usageType, lineCount, line, errorWriter);
            } else {
                printError(UsageType.TYPE_UNKNOWN_SQL_ERROR, lineCount, line, errorWriter);
            }
        }
    }

    private void printError(UsageType serviceError, UsageType typeOfServiceError, int lineCount, String line,
                            FileWriter errorWriter) {
        String errorType;
        if (UsageType.TYPE_NO_SERVICE != serviceError) {
            throw new IllegalArgumentException("Invalid argument. Expected no service!");
        }
        if (typeOfServiceError == UsageType.TYPE_TEXT) {
            errorType = "<HARD LIMIT FOR TEXTS REACHED>";
        } else if (typeOfServiceError == UsageType.TYPE_CALL) {
            errorType = "<HARD LIMIT FOR CALLS REACHED>";
        } else if (typeOfServiceError == UsageType.TYPE_INTERNET) {
            errorType = "<HARD LIMIT FOR INTERNET REACHED>";
        } else {
            throw new IllegalArgumentException("Invalid argument, found: " + typeOfServiceError.toString());
        }
        try {
            errorWriter.append(FormValidation.getDate()).append(" - Error at line <").append(String.valueOf(lineCount))
                    .append("> ").append(errorType).append(" : ").append(line).append('\n');
        } catch (IOException ignored) {
        }
    }

    private void printError(UsageType typeOfError, int lineCount, String line, FileWriter errorWriter) {
        String errorType;
        System.out.println("Error processing file at line " + lineCount + ".");
        if (UsageType.TYPE_INVALID_FORMAT == typeOfError) {
            errorType = "<INVALID FORMATTING>";
        } else if (UsageType.TYPE_UNKNOWN_USAGE == typeOfError) {
            errorType = "<UNKNOWN USAGE TYPE>";
        } else if (UsageType.TYPE_NO_ACCOUNT == typeOfError) {
            errorType = "<SOURCE/DESTINATION PHONE HAS NO ACCOUNT>";
        } else if (UsageType.TYPE_INVALID_DATE == typeOfError) {
            errorType = "<DATE INVALID>";
        } else if (UsageType.TYPE_UNKNOWN_SQL_ERROR == typeOfError) {
            errorType = "<SQL ERROR DURING EXECUTION>";
        } else if (UsageType.TYPE_SOURCE_AND_DEST_SAME == typeOfError) {
            errorType = "<SOURCE AND DESTINATION THE SAME>";
        } else if (UsageType.TYPE_END_BEFORE_START == typeOfError) {
            errorType = "<END TIME BEFORE START TIME>";
        } else if (UsageType.TYPE_INVALID_BYTES == typeOfError) {
            errorType = "<INVALID BYTES SPECIFIED>";
        } else if (UsageType.TYPE_TOO_FEW_BYTES == typeOfError) {
            errorType = "<TOO FEW BYTES SPECIFIED>";
        } else if (UsageType.TYPE_TOO_MANY_BYTES == typeOfError) {
            errorType = "<TOO MANY BYTES SPECIFIED>";
        } else if (UsageType.TYPE_INVALID_MEGABYTES == typeOfError) {
            errorType = "<INVALID MEGABYTES SPECIFIED>";
        } else if (UsageType.TYPE_TOO_FEW_MEGABYTES == typeOfError) {
            errorType = "<TOO FEW MEGABYTES SPECIFIED>";
        } else if (UsageType.TYPE_TOO_MANY_MEGABYTES == typeOfError) {
            errorType = "<TOO MANY MEGABYTES SPECIFIED>";
        } else {
            errorType = "<UNKNOWN ERROR>";
        }
        try {
            errorWriter.append(FormValidation.getDate()).append(" - Error at line <").append(String.valueOf(lineCount))
                    .append("> ").append(errorType).append(" : ").append(line).append('\n');
        } catch (IOException ignored) {
        }
    }

    private void printUsage(UsageType usageType, int lineCount, String line, FileWriter usageWriter) throws IOException {
        String usageString;
        if (usageType == UsageType.TYPE_TEXT) {
            usageString = "<TEXT>";
        } else if (usageType == UsageType.TYPE_CALL) {
            usageString = "<CALL>";
        } else if (usageType == UsageType.TYPE_INTERNET) {
            usageString = "<INTERNET>";
        } else {
            throw new IllegalArgumentException("Invalid argument, found: " + usageType.toString());
        }
        usageWriter.append(FormValidation.getDate()).append(" at line ").append(String.valueOf(lineCount)).append(" ")
                .append(usageString).append(" : ").append(line).append('\n');
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
                return UsageType.TYPE_UNKNOWN_USAGE;
        }
    }

    private UsageType verifyText(String[] tokens) {
        if (tokens.length != 5) {
            return UsageType.TYPE_INVALID_FORMAT;
        }

        if (!isPhoneValid(tokens[1])) {
            return UsageType.TYPE_INVALID_PHONE;
        }

        if (!isPhoneValid(tokens[2])) {
            return UsageType.TYPE_INVALID_PHONE;
        }

        if (getPhone(tokens[1]).equals(getPhone(tokens[2]))) {
            return UsageType.TYPE_SOURCE_AND_DEST_SAME;
        }

        if (!isTimeValid(tokens[3])) {
            return UsageType.TYPE_INVALID_DATE;
        }

        UsageType usageType = isBytesValid(tokens[4]);
        if (usageType != null) {
            return usageType;
        }
        return UsageType.TYPE_TEXT;
    }

    private UsageType isBytesValid(String rawBytes) {
        if (rawBytes == null || rawBytes.length() < 2) {
            return UsageType.TYPE_INVALID_BYTES;
        }

        if (!rawBytes.substring(rawBytes.length() - 1).equals("B")) {
            return UsageType.TYPE_INVALID_BYTES;
        }

        int bytes;
        try {
            bytes = Integer.parseInt(rawBytes.substring(0, rawBytes.length() - 1));
        } catch (NumberFormatException e) {
            return UsageType.TYPE_INVALID_BYTES;
        }
        if (bytes < 0) {
            return UsageType.TYPE_TOO_FEW_BYTES;
        } else if (bytes > 100000) {
            return UsageType.TYPE_TOO_MANY_BYTES;
        }
        return null;
    }

    private boolean isPhoneValid(String rawPhone) {
        return FormValidation.isPhoneNumberValid(rawPhone, false);

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
        if (year < 2000 || year > 2100) {
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
        if (tokens.length != 5) {
            return UsageType.TYPE_INVALID_FORMAT;
        }

        if (!isPhoneValid(tokens[1])) {
            return UsageType.TYPE_INVALID_PHONE;
        }

        if (!isPhoneValid(tokens[2])) {
            return UsageType.TYPE_INVALID_PHONE;
        }

        if (getPhone(tokens[1]).equals(getPhone(tokens[2]))) {
            return UsageType.TYPE_SOURCE_AND_DEST_SAME;
        }

        if (!isTimeValid(tokens[3])) {
            return UsageType.TYPE_INVALID_DATE;
        }
        String[] timePieces = tokens[3].split(";");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate;
        try {
            startDate = dateFormat.parse(timePieces[0] + " " + timePieces[1]);
        } catch (ParseException e) {
            return UsageType.TYPE_INVALID_DATE;
        }

        if (!isTimeValid(tokens[4])) {
            return UsageType.TYPE_INVALID_DATE;
        }
        timePieces = tokens[4].split(";");
        Date endDate;
        try {
            endDate = dateFormat.parse(timePieces[0] + " " + timePieces[1]);
        } catch (ParseException e) {
            return UsageType.TYPE_INVALID_DATE;
        }

        //Check that the dates make sense (end date is after the start date)
        if (endDate.getTime() - startDate.getTime() <= 0) {
            return UsageType.TYPE_END_BEFORE_START;
        }

        return UsageType.TYPE_CALL;
    }

    private UsageType verifyInternet(String[] tokens) {
        if (tokens.length != 4) {
            return UsageType.TYPE_INVALID_FORMAT;
        }

        if (!isPhoneValid(tokens[1])) {
            return UsageType.TYPE_INVALID_PHONE;
        }

        if (!isTimeValid(tokens[2])) {
            return UsageType.TYPE_INVALID_DATE;
        }

        UsageType usageType = isMegabytesValid(tokens[3]);
        if (usageType != null) {
            return usageType;
        }

        return UsageType.TYPE_INTERNET;
    }

    private UsageType isMegabytesValid(String rawMegabytes) {
        if (rawMegabytes == null || rawMegabytes.length() < 2) {
            return UsageType.TYPE_INVALID_MEGABYTES;
        }

        if (!rawMegabytes.substring(rawMegabytes.length() - 2).equals("MB")) {
            return UsageType.TYPE_INVALID_MEGABYTES;
        }

        int megabytes;
        try {
            megabytes = Integer.parseInt(rawMegabytes.substring(0, rawMegabytes.length() - 2));
        } catch (NumberFormatException e) {
            return UsageType.TYPE_INVALID_MEGABYTES;
        }

        if (megabytes < 0) {
            return UsageType.TYPE_TOO_FEW_MEGABYTES;
        } else if (megabytes > 100000) {
            return UsageType.TYPE_TOO_MANY_MEGABYTES;
        }
        return UsageType.TYPE_INTERNET;
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
