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
        ColumnTypes CURRENT_PLAN_TYPE = INTEGER;

    }

    interface Bill {
        String A_ID = "A_ID";
        String BILL_ID = "BILL_ID";
        String BILL_PERIOD = "BILL_PERIOD";
        String IS_PAID = "IS_PAID";
        String PLAN_ID = "PLAN_ID";
        String ACCUMULATED_CHARGES = "ACCUMULATED_CHARGES";

        ColumnTypes A_ID_TYPE = INTEGER;
        ColumnTypes BILL_ID_TYPE = INTEGER;
        ColumnTypes BILL_PERIOD_TYPE = DATE;
        ColumnTypes IS_PAID_TYPE = INTEGER;
        ColumnTypes PLAN_ID_TYPE = INTEGER;
        ColumnTypes ACCUMULATED_CHARGES_TYPE = DOUBLE_MONEY;
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

    interface Service {
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

        String PLAN_ID = "PLAN_ID";
        String P_NAME = "P_NAME";
        String HARD_LIMIT = "HARD_LIMIT";
        String LIMIT_TEXTS = "LIMIT_TEXTS";
        String LIMIT_CALLS_SECONDS = "LIMIT_CALLS_SECONDS";
        String LIMIT_INTERNET_MB = "LIMIT_INTERNET_MB";
        String RATE_TEXTS = "RATE_TEXTS";
        String RATE_CALLS_SECONDS = "RATE_CALLS_SECONDS";
        String RATE_INTERNET_MB = "RATE_INTERNET_MB";
        String OVERDRAFT_RATE_TEXTS = "OVERDRAFT_RATE_TEXTS";
        String OVERDRAFT_RATE_CALLS_SECONDS = "OVERDRAFT_RATE_CALLS_SECONDS";
        String OVERDRAFT_RATE_INTERNET_MB = "OVERDRAFT_RATE_INTERNET_MB";
        String BASE_RATE = "BASE_RATE";
        String IS_RESIDENTIAL = "IS_RESIDENTIAL";

        ColumnTypes PLAN_ID_TYPE = INTEGER;
        ColumnTypes P_NAME_TYPE = STRING;
        ColumnTypes HARD_LIMIT_TYPE = INTEGER;
        ColumnTypes LIMIT_TEXTS_TYPE = INTEGER;
        ColumnTypes LIMIT_CALLS_SECONDS_TYPE = INTEGER;
        ColumnTypes LIMIT_INTERNET_MB_TYPE = INTEGER;
        ColumnTypes RATE_TEXTS_TYPE = ColumnTypes.DOUBLE;
        ColumnTypes RATE_CALLS_SECONDS_TYPE = ColumnTypes.DOUBLE;
        ColumnTypes RATE_INTERNET_MB_TYPE = ColumnTypes.DOUBLE;
        ColumnTypes OVERDRAFT_RATE_TEXTS_TYPE = ColumnTypes.DOUBLE;
        ColumnTypes OVERDRAFT_RATE_CALLS_SECONDS_TYPE = ColumnTypes.DOUBLE;
        ColumnTypes OVERDRAFT_RATE_INTERNET_MB_TYPE = ColumnTypes.DOUBLE;
        ColumnTypes BASE_RATE_TYPE = ColumnTypes.DOUBLE;
        ColumnTypes IS_RESIDENTIAL_TYPE = INTEGER;

    }


}
