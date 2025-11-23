package use_case.viewhistory;

/**
 * Input Boundary for the View History Use Case.
 * Defines the contract for retrieving a user's visit history.
 * This interface enforces the Dependency Inversion Principle by allowing
 * the use case to be invoked without depending on concrete implementations.
 */
public interface ViewHistoryInputBoundary {

    /**
     * Executes the view history use case.
     * Retrieves all visits for the specified user and presents them.
     *
     * @param inputData contains the username whose history should be retrieved
     */
    void execute(ViewHistoryInputData inputData);
}
