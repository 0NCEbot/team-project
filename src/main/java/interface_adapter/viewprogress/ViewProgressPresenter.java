package interface_adapter.viewprogress;

import interface_adapter.ViewManagerModel;
import use_case.viewprogress.ViewProgressOutputBoundary;
import use_case.viewprogress.ViewProgressOutputData;

/**
 * Presenter for the View Progress screen.
 * Implements ViewProgressOutputBoundary to handle output from the use case.
 *
 * This class is part of the Interface Adapter layer and is responsible
 * for converting use case output data into view model state.
 * It adheres to the Single Responsibility Principle by focusing
 * solely on presentation logic for progress display.
 */
public class ViewProgressPresenter implements ViewProgressOutputBoundary {

    private final ViewProgressViewModel viewModel;
    private final ViewManagerModel viewManagerModel;

    /**
     * Constructs the View Progress presenter.
     *
     * @param viewModel manages the state for the view progress screen
     * @param viewManagerModel manages which view is currently active
     */
    public ViewProgressPresenter(ViewProgressViewModel viewModel,
                                  ViewManagerModel viewManagerModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
    }

    /**
     * Prepares the success view for displaying progress statistics.
     * Converts use case output data into view model state.
     *
     * @param outputData contains the progress data to display
     */
    @Override
    public void prepareSuccessView(ViewProgressOutputData outputData) {
        ViewProgressState state = new ViewProgressState();
        state.setUsername(outputData.getUsername());
        state.setVisitedCount(outputData.getVisitedCount());
        state.setTotalLandmarks(outputData.getTotalLandmarks());
        state.setTotalVisits(outputData.getTotalVisits());
        state.setCompletionPercent(outputData.getCompletionPercent());
        state.setLastVisitedAt(outputData.getLastVisitedAt());
        state.setHasVisits(outputData.hasVisits());
        state.setErrorMessage(null);

        viewModel.setState(state);
        viewModel.firePropertyChange();

        // Navigate to the view progress screen
        viewManagerModel.setState("my progress");
        viewManagerModel.firePropertyChange();
    }

    /**
     * Prepares the failure view when an error occurs in view progress use case.
     *
     * @param errorMessage the error message to display
     */
    @Override
    public void prepareFailView(String errorMessage) {
        ViewProgressState state = viewModel.getState();
        state.setErrorMessage(errorMessage);
        viewModel.firePropertyChange();
    }
}
