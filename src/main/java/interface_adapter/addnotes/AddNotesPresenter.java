// src/java/interface_adapter/addnotes/AddNotesPresenter.java
package interface_adapter.addnotes;

import interface_adapter.ViewManagerModel;
import use_case.addnotes.AddNotesOutputBoundary;
import use_case.addnotes.AddNotesOutputData;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AddNotesPresenter implements AddNotesOutputBoundary {

    private final AddNotesViewModel viewModel;
    private final ViewManagerModel viewManagerModel;

    // All notes will be shown in this format, e.g. "2025-11-21 11:23"
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public AddNotesPresenter(AddNotesViewModel viewModel,
                             ViewManagerModel viewManagerModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void present(AddNotesOutputData data) {
        AddNotesState state = new AddNotesState();
        state.setUsername(data.getUsername());
        state.setLandmarkName(data.getLandmarkName());
        state.setLandmarkDescription(data.getLandmarkDescription());
        state.setAddress(data.getAddress());
        state.setOpenHours(data.getOpenHours());
        state.setErrorMessage(data.getErrorMessage());
        state.setSuccessMessage(data.getSuccessMessage());

        // Map NoteDTO -> NoteVM with normalized date format
        List<AddNotesState.NoteVM> vms = new ArrayList<>();
        for (AddNotesOutputData.NoteDTO dto : data.getNotes()) {
            AddNotesState.NoteVM vm = new AddNotesState.NoteVM();
            vm.content = dto.content;
            vm.createdAt = normalizeCreatedAt(dto.createdAt);
            vms.add(vm);
        }
        state.setNotes(vms);

        viewModel.setState(state);
        viewModel.firePropertyChange();

        // stay on Notes view
        viewManagerModel.setState("notes");
        viewManagerModel.firePropertyChange();
    }

    /**
     * Make all date strings look the same.
     * Handles:
     *  - ISO Instant strings from JSON, e.g. "2025-11-21T16:23:57.102173Z"
     *  - Already formatted "yyyy-MM-dd HH:mm"
     * Falls back to the original string if parsing fails.
     */
    private String normalizeCreatedAt(String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }

        // 1) Try parse as Instant (ISO-8601)
        try {
            Instant inst = Instant.parse(raw);
            return DISPLAY_FORMATTER
                    .withZone(ZoneId.systemDefault())
                    .format(inst);
        } catch (Exception ignored) {
            // not an Instant
        }

        // 2) Try parse as already "yyyy-MM-dd HH:mm"
        try {
            LocalDateTime ldt = LocalDateTime.parse(raw, DISPLAY_FORMATTER);
            return DISPLAY_FORMATTER.format(ldt);
        } catch (Exception ignored) {
            // not in that pattern either
        }

        // 3) If both fail, just show the raw string
        return raw;
    }
}
