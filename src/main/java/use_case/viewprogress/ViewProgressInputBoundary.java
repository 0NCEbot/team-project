package use_case.viewprogress;

/**
 * Input Boundary for the View Progress Use Case.
 * Defines the contract for executing the view progress business logic.
 * This interface is part of the Use Case layer and maintains the dependency
 * inversion principle by allowing the outer layers to depend on this abstraction.
 */
public interface ViewProgressInputBoundary {

    /**
     * Executes the view progress use case.
     * Retrieves and calculates progress statistics for the specified user.
     *
     * @param inputData contains the username whose progress should be displayed
     */
    void execute(ViewProgressInputData inputData);
}
