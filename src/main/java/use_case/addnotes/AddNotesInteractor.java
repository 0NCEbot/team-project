// src/java/use_case/addnotes/AddNotesInteractor.java
package use_case.addnotes;

import data_access.LandmarkDataAccessInterface;
import data_access.UserDataAccessInterface;
import entity.Landmark;
import entity.Note;
import entity.User;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AddNotesInteractor implements AddNotesInputBoundary {

    private final UserDataAccessInterface userDAO;
    private final LandmarkDataAccessInterface landmarkDAO;
    private final AddNotesOutputBoundary presenter;

    public AddNotesInteractor(UserDataAccessInterface userDAO,
                              LandmarkDataAccessInterface landmarkDAO,
                              AddNotesOutputBoundary presenter) {
        this.userDAO = userDAO;
        this.landmarkDAO = landmarkDAO;
        this.presenter = presenter;
    }

    @Override
    public void addNote(AddNotesInputData inputData) {
        String username = inputData.getUsername();
        String landmarkName = inputData.getLandmarkName();
        String content = inputData.getContent() == null
                ? ""
                : inputData.getContent().trim();

        // First, fetch user & landmark (needed both for success and error cases)
        User user = userDAO.get(username);
        if (user == null) {
            presenter.present(new AddNotesOutputData(
                    username,
                    landmarkName,
                    "",
                    "",
                    "",
                    new ArrayList<>(),
                    "User not found.",
                    null
            ));
            return;
        }

        Landmark landmark = landmarkDAO.findByName(landmarkName);
        if (landmark == null) {
            presenter.present(new AddNotesOutputData(
                    username,
                    landmarkName,
                    "",
                    "",
                    "",
                    new ArrayList<>(),
                    "Landmark not found.",
                    null
            ));
            return;
        }

        // Build list of THIS USER’s notes for THIS LANDMARK
        List<AddNotesOutputData.NoteDTO> noteDTOs = buildNoteDTOs(user, landmarkName);

        // If content is empty => validation error, but KEEP existing notes/info
        if (content.isEmpty()) {
            var info = landmark.getLandmarkInfo();

            presenter.present(new AddNotesOutputData(
                    username,
                    landmark.getLandmarkName(),
                    info.getDescription(),
                    info.getAddress(),
                    info.getOpenHours(),
                    noteDTOs,                    // <-- keep notes
                    "Note cannot be empty.",     // error
                    null                         // no success
            ));
            return;
        }

        // ===== SUCCESS PATH =====

        // create & attach new note
        Note newNote = new Note(landmark, content);
        user.getPrivateNotes().add(newNote);
        userDAO.save(user);

        // rebuild notes list including the new one
        noteDTOs = buildNoteDTOs(user, landmarkName);

        var info = landmark.getLandmarkInfo();

        presenter.present(new AddNotesOutputData(
                username,
                landmark.getLandmarkName(),
                info.getDescription(),
                info.getAddress(),
                info.getOpenHours(),
                noteDTOs,
                null,
                "Note added successfully."
        ));
    }

    // helper to convert private notes -> DTOs
    private List<AddNotesOutputData.NoteDTO> buildNoteDTOs(User user, String landmarkName) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                .withZone(java.time.ZoneId.systemDefault());

        List<AddNotesOutputData.NoteDTO> noteDTOs = new ArrayList<>();
        user.getPrivateNotes().stream()
                .filter(n -> n.getLandmark().getLandmarkName()
                        .equalsIgnoreCase(landmarkName))
                .forEach(n -> noteDTOs.add(
                        new AddNotesOutputData.NoteDTO(
                                fmt.format(n.getCreatedAt()),
                                n.getContent()
                        )
                ));

        return noteDTOs;
    }
}
