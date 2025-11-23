package interface_adapter.viewhistory;

import use_case.viewhistory.ViewHistoryInputBoundary;
import use_case.viewhistory.ViewHistoryInputData;
import use_case.undovisit.UndoVisitInputBoundary;
import use_case.undovisit.UndoVisitInputData;

/**
 * Controller for the View History screen.
 * Handles user input events from the view and delegates to the appropriate
 * use case interactors. This class is part of the Interface Adapter layer
 * and serves as the bridge between the UI and business logic.
 *
 * This controller adheres to the Single Responsibility Principle by
 * focusing solely on coordinating between the view and use cases.
 */
public class ViewHistoryController {

    private final ViewHistoryInputBoundary viewHistoryInteractor;
    private final UndoVisitInputBoundary undoVisitInteractor;

    /**
     * Constructs the View History controller.
     * Dependencies are injected to support testability and flexibility.
     *
     * @param viewHistoryInteractor handles the view history use case
     * @param undoVisitInteractor handles the undo visit use case
     */
    public ViewHistoryController(ViewHistoryInputBoundary viewHistoryInteractor,
                                  UndoVisitInputBoundary undoVisitInteractor) {
        this.viewHistoryInteractor = viewHistoryInteractor;
        this.undoVisitInteractor = undoVisitInteractor;
    }

    /**
     * Executes the view history use case for the specified user.
     * Called when the view needs to load or refresh visit history.
     *
     * @param username the username whose visit history should be displayed
     */
    public void execute(String username) {
        ViewHistoryInputData inputData = new ViewHistoryInputData(username);
        viewHistoryInteractor.execute(inputData);
    }

    /**
     * Executes the undo visit use case to remove a visit.
     * Called when the user confirms they want to remove a visit from history.
     *
     * @param username the username whose visit should be removed
     * @param visitId the unique identifier of the visit to remove
     */
    public void undoVisit(String username, String visitId) {
        UndoVisitInputData inputData = new UndoVisitInputData(username, visitId);
        undoVisitInteractor.execute(inputData);
    }
}
