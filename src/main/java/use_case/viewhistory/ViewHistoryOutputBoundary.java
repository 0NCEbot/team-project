package use_case.viewhistory;

/**
 * Output Boundary for the View History Use Case.
 * Defines how the use case interactor communicates results to the presenter.
 * This interface enforces the Dependency Inversion Principle by allowing
 * the interactor to remain independent of presentation concerns.
 */
public interface ViewHistoryOutputBoundary {

    /**
     * Prepares the success view with visit history data.
     *
     * @param outputData contains the list of visits to be displayed
     */
    void prepareSuccessView(ViewHistoryOutputData outputData);

    /**
     * Prepares the failure view when an error occurs.
     *
     * @param errorMessage the error message to display
     */
    void prepareFailView(String errorMessage);
}
