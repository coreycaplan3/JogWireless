package database;

/**
 * A class used to parse the data from the scheme relating to a plan and output a human readable form of the plan's
 * specifications.
 */
public class PlanParser {

    private final String planName;
    private final int hardLimit;
    private final int limitTexts;
    private final int limitCallsMinutes;
    private final int limitInternetGigabytes;
    private final double rateTexts;
    private final double rateCallsMinutes;
    private final double rateInternetGigabytes;
    private final String rateTextsString;
    private final String rateCallsMinutesString;
    private final String rateInternetString;
    private final String overdraftRateTextsString;
    private final String overdraftRateCallsString;
    private final String overdraftRateInternetString;
    private final double baseRate;
    private final String baseRateString;

    /**
     * The constructor invoked when a plan is either retrieved from the database or created by a CEO and needs to be
     * parsed for the user. Any value that is entered with the value <b>0</b> corresponds to a <b>null</b> in the SQL.
     *
     * @param planName                       The name of the plan.
     * @param hardLimit                      1 if the plan has a hard limit on the accounts usage or 0 if it doesn't.
     *                                       Note, passing 1 means that <b>all</b> fields have a hard limit.
     * @param limitTexts                     The maximum number of texts that any customer can send or receive for the
     *                                       given billing period.
     * @param limitCallsMinutes              The maximum number of calls the user my receive or send (in seconds) for a
     *                                       given billing period.
     * @param limitInternetGigabytes         The maximum amount of data the user may use (in megabytes) for a given
     *                                       billing period.
     * @param rateTexts                      The rate at which the user should be charged for incoming and outgoing
     *                                       texts.
     * @param rateCallsMinutes               The rate at which the user should be charged for incoming and outgoing
     *                                       calls for every second spent on the phone.
     * @param rateInternetGigabytes          The rate at which the user should be charged for data usage
     *                                       (per gigabyte of data).
     * @param overdraftRateTexts             The overdraft rate that the customer will be charged if he/she goes over
     *                                       the usage limit. If there is no limit, this rate should be 0.
     * @param overdraftRateCallsMinutes      The overdraft rate that the customer will be charged if he/she goes over
     *                                       the usage limit. If there is no limit, this rate should be 0.
     * @param overdraftRateInternetGigabytes The overdraft rate that the customer will be charged if he/she goes over
     *                                       the usage limit. If there is no limit, this rate should be 0.
     * @param baseRate                       The rate that the user should incur on a daily basis for maintaining an
     *                                       account with Jog. This can be used for plans that charge a set amount
     *                                       monthly and have preset limits, or when the user pays one amount and has
     *                                       unlimited data.
     */
    public PlanParser(String planName, int hardLimit, int limitTexts, int limitCallsMinutes, int limitInternetGigabytes,
                      double rateTexts, double rateCallsMinutes, double rateInternetGigabytes, double overdraftRateTexts,
                      double overdraftRateCallsMinutes, double overdraftRateInternetGigabytes, double baseRate) {
        this.planName = planName;
        this.hardLimit = hardLimit;
        this.limitTexts = limitTexts;
        this.limitCallsMinutes = limitCallsMinutes;
        this.limitInternetGigabytes = limitInternetGigabytes;
        this.rateTexts = rateTexts;
        this.rateCallsMinutes = rateCallsMinutes;
        this.rateInternetGigabytes = rateInternetGigabytes;
        this.baseRate = baseRate;
        rateTextsString = String.format("$%.4f", rateTexts);
        rateCallsMinutesString = String.format("$%.4f", rateCallsMinutes);
        rateInternetString = String.format("$%.4f", rateInternetGigabytes);
        overdraftRateTextsString = String.format("$%.4f", overdraftRateTexts);
        overdraftRateCallsString = String.format("$%.4f", overdraftRateCallsMinutes);
        overdraftRateInternetString = String.format("$%.4f", overdraftRateInternetGigabytes);
        baseRateString = String.format("$%.4f", baseRate);
    }

    /**
     * @return A human readable description that parses the data of the plan and presents it in a meaningful way.
     */
    public String parse() {
        if (hardLimit == 1) {
            return planName + " - " + parseHardLimit();
        } else {
            return planName + " - " + parseNoHardLimit();
        }
    }

    private String parseHardLimit() {
        StringBuilder stringBuilder = new StringBuilder();
        if (baseRate != 0) {
            stringBuilder.append("Get charged a preset amount of ").append(baseRateString).append(" that is evenly " +
                    "accumulated throughout the month.\n");
        }
        if (rateTexts != 0) {
            stringBuilder.append("Texts are ").append(rateTextsString).append(" per incoming and outgoing text.\n");
        }
        if (rateCallsMinutes != 0) {
            stringBuilder.append("Phone calls are ").append(rateCallsMinutesString).append(" per minute on the phone.\n");
        }
        if (rateInternetGigabytes != 0) {
            stringBuilder.append("Data costs are ").append(rateInternetString).append(" per gigabyte of data.\n");
        }
        stringBuilder.append("Additionally, your service will be suspended for each corresponding category " +
                "whose limits you you exceed.\n");
        stringBuilder.append("The limit for incoming and outgoing text messages is ").append(limitTexts)
                .append(" each month.\n");
        stringBuilder.append("The limit for incoming and outgoing phone calls (in minutes) is ")
                .append(limitCallsMinutes).append(" each month.\n");
        stringBuilder.append("The limit for data usage (in gigabytes) is ").append(limitInternetGigabytes)
                .append(" each month.\n");

        return stringBuilder.toString();
    }

    private String parseNoHardLimit() {
        StringBuilder stringBuilder = new StringBuilder();
        if (baseRate != 0) {
            stringBuilder.append("Get charged a preset amount of ").append(baseRateString).append(" that is evenly " +
                    "accumulated throughout the month.\n");
        }
        if (rateTexts != 0) {
            stringBuilder.append("Incoming and outgoing text messages are ").append(rateTextsString)
                    .append(" per text.\n");
        }
        if (limitTexts != 0) {
            stringBuilder.append("If you go over the limit of ").append(limitTexts).append(" texts, you will be charged " +
                    "a rate of ").append(overdraftRateTextsString).append(" per text message.\n");
        } else {
            stringBuilder.append("There are no limits to how many texts you may send or receive, so there are no " +
                    "overdraft charges!\n");
        }

        if (rateCallsMinutes != 0) {
            stringBuilder.append("Incoming and outgoing phone calls are ").append(rateCallsMinutesString)
                    .append(" per minute.\n");
        }
        if (limitCallsMinutes != 0) {
            stringBuilder.append("If you go over the limit of ").append(limitCallsMinutes).append(" minutes, " +
                    "you will be charged ").append("an overdraft rate of ").append(overdraftRateCallsString)
                    .append(" per minute.\n");
        } else {
            stringBuilder.append("There are no limits to the duration of the phone calls you make and receive, so " +
                    "there are no overdraft charges!\n");
        }

        if (rateInternetGigabytes != 0) {
            stringBuilder.append("Internet usage is packaged at ").append(rateInternetString)
                    .append(" per gigabyte.\n");
        }
        if (limitInternetGigabytes != 0) {
            stringBuilder.append("If you go over the limit of ").append(limitInternetGigabytes).append(" gigabytes, you " +
                    "will be charged an overdraft rate of ").append(overdraftRateInternetString)
                    .append(" per gigabyte.\n");
        } else {
            stringBuilder.append("There are no limits to the amount of internet you access, so there are no " +
                    "overdraft charges!\n");
        }
        return stringBuilder.toString();
    }

}
