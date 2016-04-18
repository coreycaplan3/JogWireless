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

}
