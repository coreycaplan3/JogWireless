package database;

/**
 *
 */
interface TableConstants {

    interface Account {

        String A_ID = "A_ID";
        String A_STATUS = "A_STATUS";
        String PRIMARY_NUMBER = "PRIMARY_NUMBER";
        String CURRENT_PLAN = "CURRENT_PLAN";

        ColumnTypes A_ID_TYPE = ColumnTypes.INTEGER;
        ColumnTypes A_STATUS_TYPE = ColumnTypes.STRING;
        ColumnTypes PRIMARY_NUMBER_TYPE = ColumnTypes.LONG;
        ColumnTypes CURRENT_PLAN_TYPE = ColumnTypes.STRING;

    }

    interface Customer {
        String ID = "C_ID";
        String ADDRESS = "ADDRESS";
        String NAME = "NAME";

        ColumnTypes ID_TYPE = ColumnTypes.INTEGER;
        ColumnTypes ADDRESS_TYPE = ColumnTypes.STRING;
        ColumnTypes NAME_TYPE = ColumnTypes.STRING;
    }

    interface PhoneModel {

        String PHONE_ID = "PHONE_ID";
        String MANUFACTURER = "MANUFACTURER";
        String MODEL = "MODEL";

        ColumnTypes PHONE_ID_TYPE = ColumnTypes.INTEGER;
        ColumnTypes MANUFACTURER_TYPE = ColumnTypes.STRING;
        ColumnTypes MODEL_TYPE = ColumnTypes.STRING;
    }

    interface PhoneNumber {
        String PHONE_NUMBER = "PHONE_NUMBER";
        String IS_IN_SERVICE = "IS_IN_SERVICE";

        ColumnTypes PHONE_NUMBER_TYPE = ColumnTypes.LONG;
        ColumnTypes IS_IN_SERVICE_TYPE = ColumnTypes.INTEGER;
    }

    interface PhoneProduct {

        String MEID = "MEID";
        String P_STATUS = "P_STATUS";

        ColumnTypes MEID_TYPE = ColumnTypes.LONG;
        ColumnTypes P_STATUS_TYPE = ColumnTypes.STRING;

    }

    interface Plans {

        String P_TYPE = "P_TYPE";
        String HARD_LIMIT = "HARD_LIMIT";
        String LIMIT_TEXTS = "LIMIT_TEXTS";
        String LIMIT_CALLS_SECONDS = "LIMIT_CALLS_SECONDS";
        String LIMIT_INTERNET_MEGABYTES = "LIMIT_INTERNET_MEGABYTES";
        String RATE_TEXTS = "RATE_TEXTS";
        String RATE_CALLS_SECONDS = "RATE_CALLS_SECONDS";
        String RATE_INTERNET_MEGABYTES = "RATE_INTERNET_MEGABYTES";
        String BASE_RATE = "BASE_RATE";
        String IS_RESIDENTIAL = "IS_RESIDENTIAL";

        ColumnTypes P_TYPE_TYPE = ColumnTypes.STRING;
        ColumnTypes HARD_LIMIT_TYPE = ColumnTypes.INTEGER;
        ColumnTypes LIMIT_TEXTS_TYPE = ColumnTypes.INTEGER;
        ColumnTypes LIMIT_CALLS_SECONDS_TYPE = ColumnTypes.INTEGER;
        ColumnTypes LIMIT_INTERNET_MEGABYTES_TYPE = ColumnTypes.INTEGER;
        ColumnTypes RATE_TEXTS_TYPE = ColumnTypes.DOUBLE;
        ColumnTypes RATE_CALLS_SECONDS_TYPE = ColumnTypes.DOUBLE;
        ColumnTypes RATE_INTERNET_MEGABYTES_TYPE = ColumnTypes.DOUBLE;
        ColumnTypes BASE_RATE_TYPE = ColumnTypes.DOUBLE;
        ColumnTypes IS_RESIDENTIAL_TYPE = ColumnTypes.INTEGER;

    }


}
