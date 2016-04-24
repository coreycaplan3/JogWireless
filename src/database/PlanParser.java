package database;

/**
 * A class used to parse the data from the scheme relating to a plan and output a human readable form of the plan's
 * specifications.
 */
public class PlanParser {

    private final String planName;
    private final int hardLimit;
    private final int limitTexts;
    private final int limitCallsSeconds;
    private final int limitInternetUsage;
    private final double rateTexts;
    private final double rateCallsSeconds;
    private final double rateInternetMegabytes;
    private final double overdraftRateTexts;
    private final double overdraftRateCalls;
    private final double overdraftRateInternet;
    private final double baseRate;

    /**
     * The default constructor invoked when a plan needs to be parsed for the user. Any value that is entered with the
     * value <b>-1</b> corresponds to a <b>null</b> in the database.
     *
     * @param planName              The name of the plan.
     * @param hardLimit             1 if the plan has a hard limit on the accounts usage or 0 if it doesn't. Note,
     *                              passing 1 means that <b>all</b> fields have a hard limit.
     * @param limitTexts            The maximum number of texts that any customer can send or receive for the given
     *                              billing period.
     * @param limitCallsSeconds     The maximum number of calls the user my receive or send (in seconds) for a given
     *                              billing period.
     * @param limitInternetUsage    The maximum amount of data the user may use (in megabytes) for a given billing
     *                              period.
     * @param rateTexts             The rate at which the user should be charged for texts. This rate can either be a
     *                              base rate that charges the user on an "as used" basis, or for overdraft when the
     *                              user goes over the maximum limit and the plan doesn't suspend service when the
     *                              limit is reached.
     * @param rateCallsSeconds      The rate at which the user should be charged for incoming and outgoing calls for
     *                              every second spent on the phone. This rate can either be a base rate that charges
     *                              the user on an "as used" basis, or for overdraft when the user goes over the
     *                              maximum limit and the plan doesn't suspend service when the limit is reached.
     * @param rateInternetMegabytes The rate at which the user should be charged for data usage. This rate can either
     *                              be a base rate that charges the user on an "as used" basis, or for overdraft when
     *                              the user goes over the maximum limit and the plan doesn't suspend service when the
     *                              limit is reached.
     * @param overdraftRateTexts    The overdraft rate that the customer will be charged if he/she goes over the usage
     *                              limit. If there is no limit, this rate should be 0.
     * @param overdraftRateCalls    The overdraft rate that the customer will be charged if he/she goes over the usage
     *                              limit. If there is no limit, this rate should be 0.
     * @param overdraftRateInternet The overdraft rate that the customer will be charged if he/she goes over the usage
     *                              limit. If there is no limit, this rate should be 0.
     * @param baseRate              The rate that the user should incur on a daily basis for maintaining an account
     *                              with Jog. This can be used for plans that charge a set amount monthly and have
     *                              preset limits, or when the user pays one amount and has unlimited data.
     */
    public PlanParser(String planName, int hardLimit, int limitTexts, int limitCallsSeconds, int limitInternetUsage,
                      double rateTexts, double rateCallsSeconds, double rateInternetMegabytes, double overdraftRateTexts,
                      double overdraftRateCalls, double overdraftRateInternet, double baseRate) {
        this.planName = planName;
        this.hardLimit = hardLimit;
        this.limitTexts = limitTexts;
        this.limitCallsSeconds = limitCallsSeconds;
        this.limitInternetUsage = limitInternetUsage;
        this.rateTexts = rateTexts;
        this.rateCallsSeconds = rateCallsSeconds;
        this.rateInternetMegabytes = rateInternetMegabytes;
        this.overdraftRateTexts = overdraftRateTexts;
        this.overdraftRateCalls = overdraftRateCalls;
        this.overdraftRateInternet = overdraftRateInternet;
        this.baseRate = baseRate;
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
        String retVal = "";
        if (baseRate == 0) {
            retVal += "Get charged for only what you use, with an upper limit for texts, calls, and internet usage.\n";
            retVal += "Texts are $" + rateTexts + " per incoming and outgoing text.\n";
            retVal += "Phone calls are $" + rateCallsSeconds + " per second on the phone.\n";
            retVal += "Data costs are $" + (rateInternetMegabytes * 1024) + " per gigabyte of data.\n";
            retVal += "Additionally, your service will be suspended for each corresponding category whose limits you " +
                    "you exceed.\n";
            retVal += "The limit for incoming and outgoing text messages is " + limitTexts + " each month.\n";
            retVal += "The limit for incoming and outgoing phone calls (in minutes) is " + (limitCallsSeconds / 60) +
                    " each month.\n";
            retVal += "The limit for data usage (in gigabytes) is " + (limitInternetUsage / 1024) + " each month.";
        } else {
            retVal += "Get charged a preset amount of $" + baseRate + " that is evenly accumulated throughout the " +
                    "month.\n";
            retVal += "Additionally, your service will be suspended for each corresponding category whose limits you " +
                    "you exceed.\n";
            retVal += "The limit for incoming and outgoing text messages is " + limitTexts + " each month.\n";
            retVal += "The limit for incoming and outgoing phone calls (in minutes) is " + (limitCallsSeconds / 60) +
                    " each month.\n";
            retVal += "The limit for data usage (in gigabytes) is " + (limitInternetUsage / 1024) + " each month.";
        }
        return retVal;
    }

    private String parseNoHardLimit() {
        String retVal = "";
        if (baseRate == 0) {

            retVal += "Incoming and outgoing text messages are $" + rateTexts + " per text.\n";
            if (limitTexts == 0) {
                retVal += "There are no limits to how many texts you may send or receive, so there are no overdraft " +
                        "charges!\n";
            } else {
                retVal += "If you go over the limit of " + limitTexts + ", you will be charged a rate of $" +
                        overdraftRateTexts + " per text message\n";
            }

            retVal += "Incoming and outgoing phone calls are $" + (rateCallsSeconds * 60) + " per minute.\n";
            if (limitCallsSeconds == 0) {
                retVal += "There are no limits to the duration of the phone calls you make and receive, so there are " +
                        "no overdraft charged!\n";
            } else {
                retVal += "If you go over the limit of " + (limitCallsSeconds * 60) + " minutes, you will be charged " +
                        "an overdraft rate of $" + overdraftRateCalls + " per second.\n";
            }

            retVal += "Internet usage is packaged at $" + (rateInternetMegabytes * 1024) + " per gigabyte.";
            if (limitInternetUsage == 0) {
                retVal += "There are no limits to the amount of internet you access, so there are no overdraft " +
                        "charges!\n";
            } else {
                retVal += "If you go over the limit of " + (limitInternetUsage / 1024) + ", you will be charged an " +
                        "overdraft rate of $" + (overdraftRateInternet * 1024) + " per gigabyte.";
            }
        } else {
            retVal += "Get charged a preset amount of $" + baseRate + " that is evenly accumulated throughout the " +
                    "month.\n";

            if (limitTexts == 0 && rateTexts != 0) {
                retVal += "Incoming and outgoing text messages are $" + rateTexts + " per text.\n";
                retVal += "There are no limits to how many texts you may send or receive, so there are no overdraft " +
                        "charges!\n";
            } else if (rateTexts != 0) {
                retVal += "Incoming and outgoing text messages are $" + rateTexts + " per text.\n";
                retVal += "If you go over the limit of " + limitTexts + ", you will be charged a rate of $" +
                        overdraftRateTexts + " per text message\n";
            }

            if (limitCallsSeconds == 0 && rateCallsSeconds != 0) {
                retVal += "Incoming and outgoing phone calls are $" + (rateCallsSeconds * 60) + " per minute.\n";
                retVal += "There are no limits to the duration of the phone calls you make and receive, so there are " +
                        "no overdraft charged!\n";
            } else if (rateCallsSeconds != 0) {
                retVal += "Incoming and outgoing phone calls are $" + (rateCallsSeconds * 60) + " per minute.\n";
                retVal += "If you go over the limit of " + (limitCallsSeconds * 60) + " minutes, you will be charged " +
                        "an overdraft rate of $" + overdraftRateCalls + " per second.\n";
            }

            if (limitInternetUsage == 0 && rateInternetMegabytes != 0) {
                retVal += "Internet usage is packaged at $" + (rateInternetMegabytes * 1024) + " per gigabyte.";
                retVal += "There are no limits to the amount of internet you access, so there are no overdraft " +
                        "charges!\n";
            } else if (rateInternetMegabytes != 0) {
                retVal += "Internet usage is packaged at $" + (rateInternetMegabytes * 1024) + " per gigabyte.";
                retVal += "If you go over the limit of " + (limitInternetUsage / 1024) + ", you will be charged an " +
                        "overdraft rate of $" + (overdraftRateInternet * 1024) + " per gigabyte.";
            }
        }
        return retVal;
    }

}
