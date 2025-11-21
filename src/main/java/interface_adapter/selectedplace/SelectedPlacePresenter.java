package interface_adapter.selectedplace;

import interface_adapter.ViewManagerModel;
import interface_adapter.addnotes.AddNotesViewModel;
import use_case.selectedplace.SelectedPlaceOutputBoundary;
import use_case.selectedplace.SelectedPlaceOutputData;

public class SelectedPlacePresenter implements SelectedPlaceOutputBoundary {

    private final SelectedPlaceViewModel viewModel;
    private final ViewManagerModel viewManagerModel;
    private final AddNotesViewModel addNotesViewModel;

    public SelectedPlacePresenter(SelectedPlaceViewModel viewModel,
                                  AddNotesViewModel notesViewModel, ViewManagerModel viewManagerModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
        this.addNotesViewModel = notesViewModel;
    }

    @Override
    public void presentPlace(SelectedPlaceOutputData data) {
        SelectedPlaceState state = viewModel.getState();
        state.setUsername(data.getUsername());
        state.setLandmarkName(data.getLandmarkName());
        state.setDescription(data.getDescription());
        state.setAddress(data.getAddress());
        state.setOpenHours(data.getOpenHours());

        viewModel.setState(state);
        viewModel.firePropertyChange();

        // switch view
        viewManagerModel.setState(viewModel.getViewName());
        viewManagerModel.firePropertyChange();
    }
    @Override
    public void presentNotes(SelectedPlaceOutputData data) {
        addNotesViewModel.getState().setUsername(data.getUsername());
        addNotesViewModel.getState().setLandmarkName(data.getLandmarkName());

        addNotesViewModel.firePropertyChange();
        viewManagerModel.setState("notes");
        viewManagerModel.firePropertyChange();
    }
}
