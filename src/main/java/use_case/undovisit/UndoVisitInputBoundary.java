package use_case.undovisit;

/**
 * Input Boundary for the Undo Visit Use Case.
 * Defines the contract for removing a visit from a user's history.
 * This interface enforces the Dependency Inversion Principle.
 */
public interface UndoVisitInputBoundary {

    /**
     * Executes the undo visit use case.
     * Removes the specified visit from the user's history.
     *
     * @param inputData contains the username and visit ID to remove
     */
    void execute(UndoVisitInputData inputData);
}
