package use_case.selectedplace;

import entity.Note;
import java.util.Collections;
import java.util.List;

public class SelectedPlaceOutputData {

    private final String username;
    private final String landmarkName;
    private final String description;
    private final String address;
    private final String openHours;
    // NEW: notes for this user+landmark
    private final List<Note> notes;

    // Old-style constructor (no notes) – used by selectPlace/checkIn
    public SelectedPlaceOutputData(String username,
                                   String landmarkName,
                                   String description,
                                   String address,
                                   String openHours) {
        this(username, landmarkName, description, address, openHours,
                Collections.emptyList());
    }

    // NEW constructor with notes
    public SelectedPlaceOutputData(String username,
                                   String landmarkName,
                                   String description,
                                   String address,
                                   String openHours,
                                   List<Note> notes) {
        this.username = username;
        this.landmarkName = landmarkName;
        this.description = description;
        this.address = address;
        this.openHours = openHours;
        this.notes = notes;
    }

    public String getUsername() { return username; }
    public String getLandmarkName() { return landmarkName; }
    public String getDescription() { return description; }
    public String getAddress() { return address; }
    public String getOpenHours() { return openHours; }

    // NEW
    public List<Note> getNotes() {
        return notes;
    }
}
