package interfaces;

/**
 * An interface used to represent the transactions that a business would perform when managing its account.
 */
public class BusinessManagingInterface extends BaseInterface {
    @Override
    public boolean performTransaction() {
        return false;
    }
}
