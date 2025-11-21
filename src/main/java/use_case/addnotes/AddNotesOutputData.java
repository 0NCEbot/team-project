// src/java/use_case/addnotes/AddNotesOutputData.java
package use_case.addnotes;

import java.util.List;

public class AddNotesOutputData {

    public static class NoteDTO {
        public final String createdAt;
        public final String content;

        public NoteDTO(String createdAt, String content) {
            this.createdAt = createdAt;
            this.content = content;
        }
    }

    private final String username;
    private final String landmarkName;
    private final String landmarkDescription;
    private final String address;
    private final String openHours;
    private final List<NoteDTO> notes;
    private final String errorMessage;
    private final String successMessage;

    public AddNotesOutputData(String username,
                              String landmarkName,
                              String landmarkDescription,
                              String address,
                              String openHours,
                              List<NoteDTO> notes,
                              String errorMessage,
                              String successMessage) {
        this.username = username;
        this.landmarkName = landmarkName;
        this.landmarkDescription = landmarkDescription;
        this.address = address;
        this.openHours = openHours;
        this.notes = notes;
        this.errorMessage = errorMessage;
        this.successMessage = successMessage;
    }

    public String getUsername() { return username; }
    public String getLandmarkName() { return landmarkName; }
    public String getLandmarkDescription() { return landmarkDescription; }
    public String getAddress() { return address; }
    public String getOpenHours() { return openHours; }
    public List<NoteDTO> getNotes() { return notes; }
    public String getErrorMessage() { return errorMessage; }
    public String getSuccessMessage() { return successMessage; }
}
