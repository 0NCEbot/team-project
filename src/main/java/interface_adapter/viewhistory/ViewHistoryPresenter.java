package interface_adapter.viewhistory;

import interface_adapter.ViewManagerModel;
import use_case.viewhistory.ViewHistoryOutputBoundary;
import use_case.viewhistory.ViewHistoryOutputData;
import use_case.undovisit.UndoVisitOutputBoundary;
import use_case.undovisit.UndoVisitOutputData;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for the View History screen.
 * Implements both ViewHistoryOutputBoundary and UndoVisitOutputBoundary
 * to handle output from both related use cases.
 *
 * This class is part of the Interface Adapter layer and is responsible
 * for converting use case output data into view model state.
 * It adheres to the Single Responsibility Principle by focusing
 * solely on presentation logic for visit history.
 */
public class ViewHistoryPresenter implements ViewHistoryOutputBoundary, UndoVisitOutputBoundary {

    private final ViewHistoryViewModel viewModel;
    private final ViewManagerModel viewManagerModel;

    /**
     * Constructs the View History presenter.
     *
     * @param viewModel manages the state for the view history screen
     * @param viewManagerModel manages which view is currently active
     */
    public ViewHistoryPresenter(ViewHistoryViewModel viewModel,
                                 ViewManagerModel viewManagerModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
    }

    /**
     * Prepares the success view for displaying visit history.
     * Converts use case output data into view model state.
     *
     * @param outputData contains the visits to display
     */
    @Override
    public void prepareSuccessView(ViewHistoryOutputData outputData) {
        ViewHistoryState state = new ViewHistoryState();
        state.setUsername(outputData.getUsername());
        state.setErrorMessage(null);
        state.setSuccessMessage(null);

        // Convert DTOs to view models
        List<ViewHistoryState.VisitVM> visitVMs = new ArrayList<>();
        for (ViewHistoryOutputData.VisitDTO dto : outputData.getVisits()) {
            ViewHistoryState.VisitVM vm = new ViewHistoryState.VisitVM(
                    dto.visitId,
                    dto.landmarkName,
                    dto.visitedAt
            );
            visitVMs.add(vm);
        }
        state.setVisits(visitVMs);

        viewModel.setState(state);
        viewModel.firePropertyChange();

        // Navigate to the view history screen
        viewManagerModel.setState("view history");
        viewManagerModel.firePropertyChange();
    }

    /**
     * Prepares the failure view when an error occurs in view history use case.
     *
     * @param errorMessage the error message to display
     */
    @Override
    public void prepareFailView(String errorMessage) {
        ViewHistoryState state = viewModel.getState();
        state.setErrorMessage(errorMessage);
        state.setSuccessMessage(null);
        viewModel.firePropertyChange();
    }

    /**
     * Prepares the success view after successfully undoing a visit.
     * Updates the view model with the refreshed visit list.
     *
     * @param outputData contains the updated visit list and success message
     */
    public void prepareSuccessView(UndoVisitOutputData outputData) {
        ViewHistoryState state = new ViewHistoryState();
        state.setUsername(outputData.getUsername());
        state.setErrorMessage(null);
        state.setSuccessMessage(outputData.getSuccessMessage());

        // Convert DTOs to view models
        List<ViewHistoryState.VisitVM> visitVMs = new ArrayList<>();
        for (UndoVisitOutputData.VisitDTO dto : outputData.getVisits()) {
            ViewHistoryState.VisitVM vm = new ViewHistoryState.VisitVM(
                    dto.visitId,
                    dto.landmarkName,
                    dto.visitedAt
            );
            visitVMs.add(vm);
        }
        state.setVisits(visitVMs);

        viewModel.setState(state);
        viewModel.firePropertyChange();

        // Stay on the view history screen
        viewManagerModel.setState("view history");
        viewManagerModel.firePropertyChange();
    }
}
