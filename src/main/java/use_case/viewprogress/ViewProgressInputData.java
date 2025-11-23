package use_case.viewprogress;

/**
 * Input Data Transfer Object for the View Progress Use Case.
 * Encapsulates the data required to retrieve a user's progress summary.
 * This class follows the Data Transfer Object pattern to keep use case
 * boundaries clean and testable.
 */
public class ViewProgressInputData {

    private final String username;

    /**
     * Constructs input data for viewing progress.
     *
     * @param username the username whose progress should be retrieved
     */
    public ViewProgressInputData(String username) {
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
