// src/java/interface_adapter/addnotes/AddNotesState.java
package interface_adapter.addnotes;

import java.util.ArrayList;
import java.util.List;

public class AddNotesState {
    private String username = "";
    private String landmarkName = "";

    // info about landmark (so NotesView can show card on the right)
    private String landmarkDescription = "";
    private String address = "";
    private String openHours = "";

    // text currently typed into textarea
    private String content = "";

    // messages
    private String errorMessage = null;
    private String successMessage = null;

    public static class NoteVM {
        public String content;
        public String createdAt;
    }

    private List<NoteVM> notes = new ArrayList<>();

    public List<NoteVM> getNotes() { return notes; }
    public void setNotes(List<NoteVM> notes) { this.notes = notes; }

    public AddNotesState() {}

    public AddNotesState(AddNotesState copy) {
        this.username = copy.username;
        this.landmarkName = copy.landmarkName;
        this.landmarkDescription = copy.landmarkDescription;
        this.address = copy.address;
        this.openHours = copy.openHours;
        this.content = copy.content;
        this.errorMessage = copy.errorMessage;
        this.successMessage = copy.successMessage;

        this.notes = new ArrayList<>();
        if (copy.notes != null) {
            this.notes.addAll(copy.notes);
        }
    }

    // ===== getters & setters =====
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getLandmarkName() { return landmarkName; }
    public void setLandmarkName(String landmarkName) { this.landmarkName = landmarkName; }

    public String getLandmarkDescription() { return landmarkDescription; }
    public void setLandmarkDescription(String landmarkDescription) { this.landmarkDescription = landmarkDescription; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getOpenHours() { return openHours; }
    public void setOpenHours(String openHours) { this.openHours = openHours; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getSuccessMessage() { return successMessage; }
    public void setSuccessMessage(String successMessage) { this.successMessage = successMessage; }
}
