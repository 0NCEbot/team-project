// src/java/interface_adapter/selectedplace/SelectedPlacePresenter.java
package interface_adapter.selectedplace;

import data_access.UserDataAccessInterface;
import entity.Note;
import entity.User;
import interface_adapter.ViewManagerModel;
import interface_adapter.addnotes.AddNotesState;
import interface_adapter.addnotes.AddNotesViewModel;
import use_case.selectedplace.SelectedPlaceOutputBoundary;
import use_case.selectedplace.SelectedPlaceOutputData;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SelectedPlacePresenter implements SelectedPlaceOutputBoundary {

    private final SelectedPlaceViewModel viewModel;
    private final ViewManagerModel viewManagerModel;
    private final AddNotesViewModel addNotesViewModel;
    private final UserDataAccessInterface userDataAccessObject;

    // same display format we used in AddNotesPresenter
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public SelectedPlacePresenter(SelectedPlaceViewModel viewModel,
                                  AddNotesViewModel notesViewModel,
                                  ViewManagerModel viewManagerModel,
                                  UserDataAccessInterface userDataAccessObject) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
        this.addNotesViewModel = notesViewModel;
        this.userDataAccessObject = userDataAccessObject;
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

        viewManagerModel.setState(viewModel.getViewName());
        viewManagerModel.firePropertyChange();
    }

    @Override
    public void presentNotes(SelectedPlaceOutputData data) {
        // --- Fill AddNotesViewModel with user + landmark + notes ---

        AddNotesState state = addNotesViewModel.getState();
        state.setUsername(data.getUsername());
        state.setLandmarkName(data.getLandmarkName());
        state.setLandmarkDescription(data.getDescription());
        state.setAddress(data.getAddress());
        state.setOpenHours(data.getOpenHours());

        // Load this user’s notes and filter by landmark
        User user = userDataAccessObject.get(data.getUsername());
        List<AddNotesState.NoteVM> notesVM = new ArrayList<>();

        if (user != null && user.getPrivateNotes() != null) {
            for (Note n : user.getPrivateNotes()) {
                if (n.getLandmark() != null &&
                        n.getLandmark().getLandmarkName()
                                .equalsIgnoreCase(data.getLandmarkName())) {

                    AddNotesState.NoteVM vm = new AddNotesState.NoteVM();
                    vm.content = n.getContent();
                    vm.createdAt = normalizeCreatedAt(
                            n.getCreatedAt() != null ? n.getCreatedAt().toString() : ""
                    );
                    notesVM.add(vm);
                }
            }
        }

        state.setNotes(notesVM);

        // clear old messages/content when entering Notes view
        state.setContent("");
        state.setErrorMessage(null);
        state.setSuccessMessage(null);

        addNotesViewModel.setState(state);
        addNotesViewModel.firePropertyChange();

        viewManagerModel.setState("notes");
        viewManagerModel.firePropertyChange();
    }

    /** Make sure any date string (ISO or already formatted) becomes "yyyy-MM-dd HH:mm". */
    private String normalizeCreatedAt(String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }

        // 1) Try ISO instant, e.g. 2025-11-21T16:23:57.102173Z
        try {
            Instant inst = Instant.parse(raw);
            return DISPLAY_FORMATTER
                    .withZone(ZoneId.systemDefault())
                    .format(inst);
        } catch (Exception ignored) {}

        // 2) Try already "yyyy-MM-dd HH:mm"
        try {
            LocalDateTime ldt = LocalDateTime.parse(raw, DISPLAY_FORMATTER);
            return DISPLAY_FORMATTER.format(ldt);
        } catch (Exception ignored) {}

        // 3) Fallback: show raw
        return raw;
    }
}
