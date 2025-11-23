package use_case.undovisit;

/**
 * Output Boundary for the Undo Visit Use Case.
 * Defines how the interactor communicates results to the presenter.
 * This interface enforces the Dependency Inversion Principle.
 */
public interface UndoVisitOutputBoundary {

    /**
     * Prepares the success view after successfully removing a visit.
     *
     * @param outputData contains updated visit history after removal
     */
    void prepareSuccessView(UndoVisitOutputData outputData);

    /**
     * Prepares the failure view when an error occurs.
     *
     * @param errorMessage the error message to display
     */
    void prepareFailView(String errorMessage);
}
