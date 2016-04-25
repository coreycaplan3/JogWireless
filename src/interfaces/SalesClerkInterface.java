package interfaces;

import database.SalesClerkDatabase;

/**
 * An interface used to represent the transactions that would occur with a sales clerk in one of Jog's walk-in
 * stores.
 */
public class SalesClerkInterface extends BaseInterface {

    private SalesClerkDatabase salesClerkDatabase;

    public SalesClerkInterface() {
        salesClerkDatabase = new SalesClerkDatabase();
    }

    @Override
    public boolean performTransaction() {
        return false;
    }

}
