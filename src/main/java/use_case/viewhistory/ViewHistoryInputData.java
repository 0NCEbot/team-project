package use_case.viewhistory;

/**
 * Input Data Transfer Object for the View History Use Case.
 * Encapsulates the data required to retrieve a user's visit history.
 * This class follows the Data Transfer Object pattern to keep use case
 * boundaries clean and testable.
 */
public class ViewHistoryInputData {

    private final String username;

    /**
     * Constructs input data for viewing visit history.
     *
     * @param username the username whose visit history should be retrieved
     */
    public ViewHistoryInputData(String username) {
        this.username = username;
    }

    /**
     * Gets the username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }
}
