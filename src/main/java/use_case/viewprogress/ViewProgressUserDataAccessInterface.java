package use_case.viewprogress;

import entity.User;

/**
 * Data Access Interface for retrieving user data in the View Progress Use Case.
 * Defines the contract for accessing user information from the data layer.
 * This interface follows the Interface Segregation Principle by exposing only
 * the methods needed for this specific use case.
 */
public interface ViewProgressUserDataAccessInterface {

    /**
     * Retrieves a user by username.
     *
     * @param username the username to look up
     * @return the User object, or null if not found
     */
    User get(String username);
}
