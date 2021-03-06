package database;

import validation.FormValidation;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static database.ColumnTypes.*;

/**
 * A utility class for printing out the results of a result set nicely.
 */
final class ResultSetHelper {

    private ResultSet resultSet;
    private final List<String> columnNames;
    private final List<ColumnTypes> columnTypes;

    ResultSetHelper(ResultSet resultSet, List<String> columnNames, List<ColumnTypes> columnTypes) {
        this.resultSet = resultSet;
        this.columnNames = columnNames;
        this.columnTypes = columnTypes;
    }

    /**
     * Prints the results of a query nicely with proper formatting.
     *
     * @param minimumSpacing The amount of space that should separate each of the columns.
     * @return A 2d array of Objects that contains all of the results of the query.
     * @throws SQLException
     */
    @SuppressWarnings("Duplicates")
    Object[][] printResults(int minimumSpacing) throws SQLException {
        ArrayList<Object[]> arrayList = new ArrayList<>();
        for (String columnName : columnNames) {
            System.out.printf("%-" + minimumSpacing + "s", columnName);
        }
        System.out.println();

        while (resultSet.next()) {
            Object[] object = new Object[columnNames.size()];
            for (int i = 0; i < columnTypes.size(); i++) {
                object[i] = new Object();
                if (columnTypes.get(i) == INTEGER) {
                    object[i] = resultSet.getInt(columnNames.get(i));
                    System.out.printf("%-" + minimumSpacing + "s", object[i]);
                } else if (columnTypes.get(i) == STRING) {
                    object[i] = resultSet.getString(columnNames.get(i));
                    System.out.printf("%-" + minimumSpacing + "s", object[i]);
                } else if (columnTypes.get(i) == LONG) {
                    object[i] = resultSet.getLong(columnNames.get(i));
                    if ((object[i] + "").length() == 10) {
                        System.out.printf("%-" + minimumSpacing + "s", FormValidation
                                .convertPhoneNumberFromDatabase((Long) object[i]));
                    } else {
                        System.out.printf("%-" + minimumSpacing + "s", object[i]);
                    }
                } else if (columnTypes.get(i) == DOUBLE) {
                    object[i] = resultSet.getDouble(columnNames.get(i));
                    System.out.printf("%-" + minimumSpacing + ".2f", object[i]);
                } else if (columnTypes.get(i) == DATE) {
                    Date date = resultSet.getDate(columnNames.get(i));
                    String dateString = String.format("%tb, %tY", date, date);
                    System.out.printf("%-" + minimumSpacing + "s", dateString);
                } else if (columnTypes.get(i) == DOUBLE_MONEY) {
                    object[i] = resultSet.getDouble(columnNames.get(i));
                    System.out.printf("$%-" + minimumSpacing + ".2f", object[i]);
                }

            }
            arrayList.add(object);
            System.out.println();
        }

        Object[][] result = new Object[arrayList.size()][columnTypes.size()];
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                result[i][j] = arrayList.get(i)[j];
            }
        }

        return result;
    }

    /**
     * Prints the results of a query nicely with proper formatting and numbers on the right for selecting one of the
     * items.
     *
     * @param minimumSpacing The amount of space that should separate each of the columns.
     * @return A 2d array of Objects that contains all of the results of the query.
     * @throws SQLException
     */
    @SuppressWarnings("Duplicates")
    Object[][] printResultsWithOptions(int minimumSpacing) throws SQLException {
        List<Object[]> list = new ArrayList<>();
        for (int i = 0; i < columnNames.size() + 1; i++) {
            if (i < columnNames.size()) {
                System.out.printf("%-" + minimumSpacing + "s", columnNames.get(i));
            } else {
                System.out.printf("%-" + minimumSpacing + "s", "OPTION");
            }
        }
        System.out.println();
        int rowCount = 0;
        while (resultSet.next()) {
            Object[] object = new Object[columnTypes.size() + 1];
            for (int i = 0; i < columnTypes.size() + 1; i++) {
                if (i < columnTypes.size()) {
                    if (columnTypes.get(i) == INTEGER) {
                        object[i] = resultSet.getInt(columnNames.get(i));
                        System.out.printf("%-" + minimumSpacing + "s", object[i]);
                    } else if (columnTypes.get(i) == STRING) {
                        object[i] = resultSet.getString(columnNames.get(i));
                        System.out.printf("%-" + minimumSpacing + "s", object[i]);
                    } else if (columnTypes.get(i) == LONG) {
                        object[i] = resultSet.getLong(columnNames.get(i));
                        if ((object[i] + "").length() == 10) {
                            System.out.printf("%-" + minimumSpacing + "s", FormValidation
                                    .convertPhoneNumberFromDatabase((Long) object[i]));
                        } else {
                            System.out.printf("%-" + minimumSpacing + "s", object[i]);
                        }
                    } else if (columnTypes.get(i) == DOUBLE) {
                        object[i] = resultSet.getDouble(columnNames.get(i));
                        System.out.printf("%.2-" + minimumSpacing + "f", object[i]);
                    } else if (columnTypes.get(i) == DATE) {
                        System.out.printf("%-" + minimumSpacing + "tb, %tY", object[i], object[i]);
                    } else if (columnTypes.get(i) == DOUBLE_MONEY) {
                        object[i] = resultSet.getDouble(columnNames.get(i));
                        System.out.printf("$%-" + minimumSpacing + ".2f", object[i]);
                    }

                } else {
                    object[i] = (rowCount + 1);
                    System.out.printf("%-" + minimumSpacing + "d", object[i]);
                }
            }
            list.add(object);
            System.out.println();
            rowCount++;
        }

        Object[][] results = new Object[list.size()][columnTypes.size() + 1];
        for (int i = 0; i < results.length; i++) {
            for (int j = 0; j < results[i].length; j++) {
                results[i][j] = list.get(i)[j];
            }
        }
        return results;
    }

    /**
     * Checks if a given result set is valid.
     *
     * @param resultSet The result set to verify.
     * @return True if it's valid and its fetch size is greater than 0. False if it's invalid.
     */
    static boolean isResultSetValid(ResultSet resultSet, String errorMessage) {
        try {
            if (!resultSet.isBeforeFirst()) {
                System.out.println(errorMessage);
                return false;
            } else {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    static ArrayList<String> makeColumnNames(String... columnNames) {
        ArrayList<String> columns = new ArrayList<>();
        Collections.addAll(columns, columnNames);
        return columns;
    }

    static ArrayList<ColumnTypes> makeColumnTypes(ColumnTypes... columnTypes) {
        ArrayList<ColumnTypes> columns = new ArrayList<>();
        Collections.addAll(columns, columnTypes);
        return columns;
    }

}
