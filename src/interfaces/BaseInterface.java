package interfaces;

/**
 * An <i>abstract</i> class used a base for all of the user interfaces.
 */
public abstract class BaseInterface {

    protected BaseInterface() {
    }

    /**
     * Performs a transaction in the given user interface.
     *
     * @return True if the user is finished processing transaction, or false if the user would like to continue
     * making transactions.
     */
    public abstract boolean performTransaction();

}
