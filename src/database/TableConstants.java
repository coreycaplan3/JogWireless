package database;

import static database.ColumnTypes.*;

/**
 * An interface filled with constants used to represent the different tables and types of data that can be stored in
 * SQL tables.
 */
@SuppressWarnings("unused")
interface TableConstants {

    interface Account {

        String A_ID = "A_ID";
        String A_STATUS = "A_STATUS";
        String PRIMARY_NUMBER = "PRIMARY_NUMBER";
        String CURRENT_PLAN = "CURRENT_PLAN";

        ColumnTypes A_ID_TYPE = INTEGER;
        ColumnTypes A_STATUS_TYPE = STRING;
        ColumnTypes PRIMARY_NUMBER_TYPE = LONG;
        ColumnTypes CURRENT_PLAN_TYPE = STRING;

    }

    interface Bill {
        String A_ID = "A_ID";
        String BILL_ID = "BILL_ID";
        String BILL_PERIOD = "BILL_PERIOD";
        String IS_PAID = "IS_PAID";
        String PLAN = "PLAN";
        String ACCUMULATED_CHARGES = "ACCUMULATED_CHARGES";

        ColumnTypes A_ID_TYPE = INTEGER;
        ColumnTypes BILL_ID_TYPE = INTEGER;
        ColumnTypes BILL_PERIOD_TYPE = DATE;
        ColumnTypes IS_PAID_TYPE = INTEGER;
        ColumnTypes PLAN_TYPE = STRING;
        ColumnTypes ACCUMULATED_CHARGES_TYPE = DOUBLE;
    }

    interface Customer {
        String ID = "C_ID";
        String ADDRESS = "ADDRESS";
        String NAME = "NAME";

        ColumnTypes ID_TYPE = INTEGER;
        ColumnTypes ADDRESS_TYPE = STRING;
        ColumnTypes NAME_TYPE = STRING;
    }

    interface PhoneModel {

        String PHONE_ID = "PHONE_ID";
        String MANUFACTURER = "MANUFACTURER";
        String MODEL = "MODEL";

        ColumnTypes PHONE_ID_TYPE = INTEGER;
        ColumnTypes MANUFACTURER_TYPE = STRING;
        ColumnTypes MODEL_TYPE = STRING;
    }

    interface PhoneNumber {
        String PHONE_NUMBER = "PHONE_NUMBER";
        String IS_IN_SERVICE = "IS_IN_SERVICE";

        ColumnTypes PHONE_NUMBER_TYPE = LONG;
        ColumnTypes IS_IN_SERVICE_TYPE = INTEGER;
    }

    interface PhoneProduct {

        String MEID = "MEID";
        String P_STATUS = "P_STATUS";

        ColumnTypes MEID_TYPE = LONG;
        ColumnTypes P_STATUS_TYPE = STRING;

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

        ColumnTypes P_TYPE_TYPE = STRING;
        ColumnTypes HARD_LIMIT_TYPE = INTEGER;
        ColumnTypes LIMIT_TEXTS_TYPE = INTEGER;
        ColumnTypes LIMIT_CALLS_SECONDS_TYPE = INTEGER;
        ColumnTypes LIMIT_INTERNET_MEGABYTES_TYPE = INTEGER;
        ColumnTypes RATE_TEXTS_TYPE = ColumnTypes.DOUBLE;
        ColumnTypes RATE_CALLS_SECONDS_TYPE = ColumnTypes.DOUBLE;
        ColumnTypes RATE_INTERNET_MEGABYTES_TYPE = ColumnTypes.DOUBLE;
        ColumnTypes BASE_RATE_TYPE = ColumnTypes.DOUBLE;
        ColumnTypes IS_RESIDENTIAL_TYPE = INTEGER;

    }


}
