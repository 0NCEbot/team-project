package interface_adapter.viewprogress;

import use_case.viewprogress.ViewProgressInputBoundary;
import use_case.viewprogress.ViewProgressInputData;

/**
 * Controller for the View Progress screen.
 * Handles user input events from the view and delegates to the use case interactor.
 * This class is part of the Interface Adapter layer and serves as the bridge
 * between the UI and business logic.
 *
 * This controller adheres to the Single Responsibility Principle by
 * focusing solely on coordinating between the view and use case.
 */
public class ViewProgressController {

    private final ViewProgressInputBoundary viewProgressInteractor;

    /**
     * Constructs the View Progress controller.
     * Dependencies are injected to support testability and flexibility.
     *
     * @param viewProgressInteractor handles the view progress use case
     */
    public ViewProgressController(ViewProgressInputBoundary viewProgressInteractor) {
        this.viewProgressInteractor = viewProgressInteractor;
    }

    /**
     * Executes the view progress use case for the specified user.
     * Called when the view needs to load or refresh progress data.
     *
     * @param username the username whose progress should be displayed
     */
    public void execute(String username) {
        ViewProgressInputData inputData = new ViewProgressInputData(username);
        viewProgressInteractor.execute(inputData);
    }
}
