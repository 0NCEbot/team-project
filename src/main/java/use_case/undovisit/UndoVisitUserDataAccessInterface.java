package use_case.undovisit;

import entity.User;

/**
 * Data Access Interface for the Undo Visit Use Case.
 * Defines the contract for accessing and modifying user data.
 * This interface enforces Interface Segregation Principle by defining
 * only the operations needed for this specific use case.
 */
public interface UndoVisitUserDataAccessInterface {

    /**
     * Retrieves a user by username.
     *
     * @param username the username to look up
     * @return the User object, or null if not found
     */
    User get(String username);

    /**
     * Saves updated user data.
     *
     * @param user the user to save
     */
    void save(User user);

    /**
     * Gets the currently logged-in username.
     *
     * @return the current username
     */
    String getCurrentUsername();
}
