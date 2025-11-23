package use_case.viewprogress;

/**
 * Output Boundary for the View Progress Use Case.
 * Defines the contract for presenting progress data to the user.
 * This interface is part of the Use Case layer and is implemented by
 * presenters in the Interface Adapter layer.
 */
public interface ViewProgressOutputBoundary {

    /**
     * Prepares the success view with progress data.
     * Called when progress data is successfully retrieved and calculated.
     *
     * @param outputData contains the progress statistics to display
     */
    void prepareSuccessView(ViewProgressOutputData outputData);

    /**
     * Prepares the failure view with an error message.
     * Called when an error occurs during progress retrieval.
     *
     * @param errorMessage description of the error that occurred
     */
    void prepareFailView(String errorMessage);
}
