package use_case.viewhistory;

import entity.User;

/**
 * Data Access Interface for the View History Use Case.
 * Defines the contract for retrieving user data needed to display visit history.
 * This interface enforces Interface Segregation Principle by defining only
 * the minimal operations needed for this specific use case.
 */
public interface ViewHistoryUserDataAccessInterface {

    /**
     * Retrieves a user by username.
     *
     * @param username the username to look up
     * @return the User object, or null if not found
     */
    User get(String username);

    /**
     * Gets the currently logged-in username.
     *
     * @return the current username
     */
    String getCurrentUsername();
}
