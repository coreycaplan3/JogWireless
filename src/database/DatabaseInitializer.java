package database;

/**
 * A class used as an accessor to the {@link DatabaseApi} class so the user of this project can login to an account
 * on Lehigh's Edgar1 server.
 */
public class DatabaseInitializer {

    /**
     * Logs the user into Edgar1.
     *
     * @param username The user's username.
     * @param password The user's password.
     * @return True if the login was successful.
     */
    public static boolean login(String username, String password) {
        System.out.println("Attempting login...");
        return DatabaseApi.initializeInstance(username, password);
    }

    public static void logout() {
        DatabaseApi databaseApi = DatabaseApi.getInstance();
        databaseApi.logout();
    }

}
