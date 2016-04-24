package interfaces;

import database.ChiefExecutiveDatabase;
import database.PlanParser;
import validation.FormValidation;

/**
 * An interface that represents the transactions that would occur with a CEO or big-shot accountant.
 */
public class ChiefExecutiveInterface extends BaseInterface {

    private ChiefExecutiveDatabase chiefExecutiveDatabase;

    public ChiefExecutiveInterface() {
        System.out.println("Welcome Jog Wireless CEO, you look great today!");
        System.out.println();
        chiefExecutiveDatabase = new ChiefExecutiveDatabase();
    }

    @Override
    public boolean performTransaction() {
        System.out.println("Please select an option:");
        System.out.printf("%-75s %d", "See all of Jog\'s accounts receivable", 1);
        System.out.printf("%-75s %d", "Get an in-depth breakdown of Jog\'s accounts receivable (by customer)", 2);
        System.out.printf("%-75s %d", "See all of Jog\'s cash collected", 3);
        System.out.printf("%-75s %d", "Get an in-depth break down of Jog\'s cash collected (by customer)", 4);
        System.out.printf("%-75s %d", "See Jog\'s customers whose bills are past due", 5);
        System.out.printf("%-75s %d", "Create a new billing plan", 6);
        System.out.printf("%-75s %d", "Return to the interface selection screen", -1);
        while (true) {
            int choice = FormValidation.getIntegerInput("Enter an option:", 10);
            if (choice == 1) {
                getAccountsReceivable();
                break;
            } else if (choice == 2) {
                chiefExecutiveDatabase.getAccountsReceivableByCustomer();
                break;
            } else if (choice == 3) {
                getCashCollected();
                break;
            } else if (choice == 4) {
                getCashCollectedByCustomer();
                break;
            } else if (choice == 5) {
                getBillsPastDue();
                break;
            } else if (choice == 6) {
                createNewBillingPlan();
            } else if (choice == -1) {
                return true;
            } else {
                System.out.println("Please enter a valid selection.");
            }
        }
        return false;
    }

    private void getAccountsReceivable() {
        chiefExecutiveDatabase.getAccountsReceivable();
        System.out.println("Returning to the selection screen...");
        System.out.println();
    }

    private void getAccountsReceivableByCustomer() {
        chiefExecutiveDatabase.getAccountsReceivableByCustomer();
        System.out.println("Returning to the selection screen...");
        System.out.println();
    }

    private void getCashCollected() {
        chiefExecutiveDatabase.getCashCollected();
        System.out.println("Returning to the selection screen...");
        System.out.println();
    }

    private void getCashCollectedByCustomer() {
        chiefExecutiveDatabase.getCashCollectedByCustomer();
        System.out.println("Returning to the selection screen...");
        System.out.println();
    }

    private void getBillsPastDue() {
        String currentDate = FormValidation.getUsageStartDate("Please enter the current date:");
        chiefExecutiveDatabase.getBillsPastDue(currentDate);
    }

    private void createNewBillingPlan() {
        String planName = FormValidation.getStringInput("Please enter the plan\'s name or -q to quit:", "plan name",
                50);
        if (planName.equals("-q")) {
            return;
        }
        System.out.println("Is this plan for residential accounts (as opposed to corporate)?");
        boolean isResidential = FormValidation.getTrueOrFalse();
        System.out.println("Does this plan have a hard limit?");
        boolean isHardLimit = getHardLimit();
        int limitTexts = getPlanLimit(isHardLimit, 100, 0);
        int limitCalls = getPlanLimit(isHardLimit, 10, 1);
        int limitInternet = getPlanLimit(isHardLimit, 1, 2);
        double baseRate = getPlanBaseRate();
        double rateTexts = getPlanRate(isHardLimit, limitTexts, baseRate, 0);
        double rateCalls = getPlanRate(isHardLimit, limitCalls, baseRate, 1);
        double rateInternet = getPlanRate(isHardLimit, limitInternet, baseRate, 2);
        double overdraftTexts = getOverdraftRate(isHardLimit, limitTexts, 0);
        double overdraftCalls = getOverdraftRate(isHardLimit, limitCalls, 1);
        double overdraftInternet = getOverdraftRate(isHardLimit, limitInternet, 2);
        System.out.println("Here is how your new plan would be presented to a customer:");
        PlanParser planParser = new PlanParser(planName, isHardLimit ? 1 : 0, limitTexts, limitCalls * 60,
                limitInternet * 1024, rateTexts, rateCalls, rateInternet, overdraftTexts, overdraftCalls,
                overdraftInternet, baseRate);
        System.out.println(planParser.parse());
        System.out.println("Would you like to create this plan?");
        boolean shouldCreate = FormValidation.getTrueOrFalse();
        if (shouldCreate) {
            chiefExecutiveDatabase.createNewBillingPlan(planName, isHardLimit, limitTexts, limitCalls * 60,
                    limitInternet * 1024, rateTexts, rateCalls, rateInternet, overdraftTexts, overdraftCalls, baseRate,
                    overdraftInternet, isResidential);
        }
        System.out.println("Returning to the selection screen...");
        System.out.println();
    }

    private boolean getHardLimit() {
        while (true) {
            int choice = FormValidation.getIntegerInput("Please enter 0 for no, 1 for yes, or 2 for an explanation " +
                    "of hard limits:", 3);
            if (choice == 0) {
                return false;
            } else if (choice == 1) {
                return true;
            } else if (choice == 2) {
                System.out.println("A hard limit allows you to suspend service to those customers who go above their " +
                        "monthly limit.");
                System.out.println("For example, if a customer has a limit of 1000 texts ");
            }
        }
    }

    /**
     * Gets the limit for a given plan.
     *
     * @param isHardLimit         True if the plan has a hard limit on texts, calls, and internet.
     * @param minimumForHardLimit The minimum amount for the given item, if there's a hard limit.
     * @param limitType           The type of item being created. Should be 0 for texts, 1 for calls, or 2 for
     *                            internet.
     * @return The limit the user decides to create.
     */
    private int getPlanLimit(boolean isHardLimit, int minimumForHardLimit, int limitType) {
        int limit;
        String prompt;
        if (limitType == 0) {
            prompt = "Please enter the monthly limit for text messages";
            prompt += isHardLimit ? "." : ", or enter 0 if there is no limit";
        } else if (limitType == 1) {
            prompt = "Please enter the monthly limit for calls, in minutes";
            prompt += isHardLimit ? "." : ", or enter 0 if there is no limit";
        } else if (limitType == 2) {
            prompt = "Please enter the monthly limit for internet usage, in gigabytes";
            prompt += isHardLimit ? "." : ", or enter 0 if there is no limit";
        } else {
            throw new IllegalArgumentException("Invalid argument, found: " + limitType);
        }
        while (true) {
            limit = FormValidation.getIntegerInput(prompt, 100000);
            if (isHardLimit) {
                if (limit < minimumForHardLimit) {
                    System.out.println("Please enter a number greater than " + minimumForHardLimit + ".");
                } else {
                    break;
                }
            } else {
                if (limit >= 0) {
                    break;
                } else {
                    System.out.println("Please enter a number greater than or equal to 0.");
                }
            }
        }
        return limit;
    }

    private double getPlanBaseRate() {
        while (true) {
            double baseRate = FormValidation.getDoubleInput("Please enter the base rate for the plan or 0 if there is " +
                    "no base rate. You may also enter -1 to get an explanation of what a  base rate is.", 100000);
            if (baseRate == -1) {
                System.out.println("A base rate is a guaranteed amount that gets charged to the account every " +
                        "month.\nLater, if you specify a rate for texts, calls, and internet of 0, then this implies " +
                        "the customer\ndoesn\'t get charged on an as-used basis. Rather, the customer only gets " +
                        "charged\na single amount per month, regardless of the amount of service that the customer " +
                        "uses.\nCharges are also accumulated and distributed evenly throughout the month.");
            } else if (baseRate >= 0) {
                return (Math.round(baseRate * 100.0) / 100.0);
            } else {
                System.out.println("Please enter a base rate greater than or equal to 0");
            }
        }
    }

    /**
     * Gets the rate for the given item on the phone plan being created.
     *
     * @param isHardLimit      True if there is a hard limit on this plan, or false if there is not one.
     * @param limitForRateType The limit that was already chosen for the given rateType
     * @param baseRate         The base rate that the customer is already being charged on a monthly basis.
     * @param rateType         The type of rate being calculate. Should be 0 for texts, 1 for calls, or 2 for
     *                         internet usage.
     * @return The rate for the given rateType.
     */
    private double getPlanRate(boolean isHardLimit, int limitForRateType, double baseRate, int rateType) {
        String prompt;
        String itemType;
        String itemTypePlural;
        String limitType;
        boolean isZeroRateAllowed;
        if (rateType == 0) {
            itemType = "text message";
            itemTypePlural = "text messages";
            limitType = "text message limit";
        } else if (rateType == 1) {
            itemType = "minute for phone calls";
            itemTypePlural = "minutes";
            limitType = "call duration limit";
        } else if (rateType == 2) {
            itemType = "gigabyte for internet usage";
            itemTypePlural = "gigabytes";
            limitType = "internet usage limit";
        } else {
            throw new IllegalArgumentException("Invalid argument, found: " + rateType);
        }
        prompt = "Please enter the amount the customer should be charged per " + itemType + ".\n";
        if (isHardLimit) {
            if (baseRate == 0) {
                prompt += "Since you did not specify a base rate, these charges imply the customer will only\n" +
                        "get charged for what he or she uses. However, the user still cannot go above the hard " +
                        "limit of " + limitForRateType + " " + itemTypePlural + " per month.";
                isZeroRateAllowed = false;
            } else {
                prompt += "This rate will be an addition to the base rate you specified of $" + baseRate + " per " +
                        "month.\nHowever, the user still cannot go above the hard limit of " + limitForRateType +
                        " " + itemTypePlural + " per month. If you would like to only charge the user for the\n" +
                        "base rate, you may enter 0";
                isZeroRateAllowed = true;
            }
        } else {
            if (baseRate == 0 && limitForRateType == 0) {
                prompt += "Since you did not specify a base rate or a " + limitType + " , these charges imply\n" +
                        "the customer will be billed for what he or she uses. There are no overdraft charges.";
                isZeroRateAllowed = false;
            } else if (baseRate == 0) {
                prompt += "Since you did not specify a base rate, these charges imply the customer will only\n" +
                        "get charged for what he or shes uses, plus an overdraft fee when the user goes over the\n" +
                        "limit of " + limitForRateType + " " + itemTypePlural + " per month. You will specify " +
                        "the overdraft rate shortly after this.";
                isZeroRateAllowed = false;
            } else if (limitForRateType == 0) {
                prompt += "Since you specified a base rate of $" + baseRate + " per month, these charges will be\n" +
                        "made in addition to the base rate. However, there is no upper limit, so the user will " +
                        "also get charged on an as-used basis, without penalties. You may enter 0 here if you\n" +
                        "only want to charge users the base rate.";
                isZeroRateAllowed = true;
            } else {
                prompt += "Since you specified a base rate of $" + baseRate + " per month, these charges will be\n" +
                        "made in addition the base rate. Moreover, the user will be charged an overdraft fee\n" +
                        "whenever he or she goes over the limit of " + limitForRateType + " " + itemTypePlural +
                        " per\nmonth. You may enter 0 here if you only want to charge an overdraft fee and base \n" +
                        " rate for your plan. You will specify the overdraft rate shortly after this.";
                isZeroRateAllowed = true;
            }
        }

        while (true) {
            double rate = FormValidation.getDoubleInput(prompt, 10);
            if (rate < 0) {
                if (isZeroRateAllowed) {
                    System.out.println("Please enter a number greater than or equal to 0.");
                } else {
                    System.out.println("Please enter a number greater than 0.");
                }
            } else if (rate == 0) {
                if (isZeroRateAllowed) {
                    return rate;
                } else {
                    System.out.println("Please enter a number greater than 0.");
                }
            } else {
                return rate;
            }
        }
    }

    /**
     * Gets the overdraft rate for the given item on the phone plan being created.
     *
     * @param isHardLimit      True if there is a hard limit on this plan, or false if there is not one.
     * @param limitForRateType The limit that was already chosen for the given rateType
     * @param rateType         The type of rate being calculate. Should be 0 for texts, 1 for calls, or 2 for
     *                         internet usage.
     * @return The overdraft rate for the given rateType.
     */
    private double getOverdraftRate(boolean isHardLimit, int limitForRateType, int rateType) {
        if (isHardLimit || limitForRateType == 0) {
            return 0;
        } else {
            String itemType;
            if (rateType == 0) {
                itemType = "text message";
            } else if (rateType == 1) {
                itemType = "phone call";
            } else if (rateType == 2) {
                itemType = "internet usage";
            } else {
                throw new IllegalArgumentException("Invalid argument, found: " + rateType);
            }
            String prompt = "Please specify a " + itemType + " limit";
            while (true) {
                double overdraftRate = FormValidation.getDoubleInput(prompt, 100);
                if (overdraftRate > 0) {
                    return overdraftRate;
                } else {
                    System.out.println("Please enter a number that is greater than 0");
                }
            }
        }
    }

}
