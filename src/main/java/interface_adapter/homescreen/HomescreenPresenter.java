package interface_adapter.homescreen;

import interface_adapter.ViewManagerModel;
import interface_adapter.browselandmarks.BrowseLandmarksState;
import interface_adapter.browselandmarks.BrowseLandmarksViewModel;
import interface_adapter.viewhistory.ViewHistoryController;
import interface_adapter.viewprogress.ViewProgressController;
import use_case.homescreen.HomescreenOutputBoundary;
import use_case.homescreen.HomescreenOutputData;

public class HomescreenPresenter implements HomescreenOutputBoundary {
    private final HomescreenViewModel viewModel;
    private final ViewManagerModel viewManagerModel;
    private final BrowseLandmarksViewModel browseLandmarksViewModel;
    private ViewHistoryController viewHistoryController;
    private ViewProgressController viewProgressController;

    public HomescreenPresenter(HomescreenViewModel viewModel, ViewManagerModel viewManagerModel, BrowseLandmarksViewModel browseLandmarksViewModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
        this.browseLandmarksViewModel = browseLandmarksViewModel;
    }

    /**
     * Sets the view history controller for navigation purposes.
     * This is called during app initialization to wire navigation dependencies.
     *
     * @param viewHistoryController the controller to use when navigating to view history
     */
    public void setViewHistoryController(ViewHistoryController viewHistoryController) {
        this.viewHistoryController = viewHistoryController;
    }

    /**
     * Sets the view progress controller for navigation purposes.
     * This is called during app initialization to wire navigation dependencies.
     *
     * @param viewProgressController the controller to use when navigating to view progress
     */
    public void setViewProgressController(ViewProgressController viewProgressController) {
        this.viewProgressController = viewProgressController;
    }

    @Override
    public void prepareSuccessView(HomescreenOutputData outputData) {
        //navigate to the target view (when views exist)
        System.out.println("Navigated to: " + outputData.getViewToNavigateTo());
        //uncomment when views exist:
        HomeScreenState homeState = viewModel.getState();
        String username = homeState.getUsername();

        String targetView = outputData.getViewToNavigateTo();

        // Initialize view-specific state based on target view
        if ("browse landmarks".equals(targetView)) {
            BrowseLandmarksState blState = browseLandmarksViewModel.getState();
            blState.setUsername(username);
            browseLandmarksViewModel.setState(blState);
            browseLandmarksViewModel.firePropertyChange();
        } else if ("my progress".equals(targetView) && viewProgressController != null) {
            // Load progress data for the user
            viewProgressController.execute(username);
            return; // Controller will handle navigation
        } else if ("view history".equals(targetView) && viewHistoryController != null) {
            // Load visit history for the user
            viewHistoryController.execute(username);
            return; // Controller will handle navigation
        }

        viewManagerModel.setState(targetView);
        viewManagerModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String error) {
        HomeScreenState state = viewModel.getState();
        state.setErrorMessage(error);
        viewModel.firePropertyChange();
        System.out.println("Error: " + error);
    }
}