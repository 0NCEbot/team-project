package use_case.selectedplace;

import data_access.LandmarkDataAccessInterface;
import data_access.UserDataAccessInterface;
import entity.Landmark;
import entity.LandmarkInfo;
import entity.Note;
import entity.User;
import entity.Visit;

import java.util.List;
import java.util.stream.Collectors;

public class SelectedPlaceInteractor implements SelectedPlaceInputBoundary {

    private final LandmarkDataAccessInterface landmarkDAO;
    private final UserDataAccessInterface userDAO;
    private final SelectedPlaceOutputBoundary presenter;

    public SelectedPlaceInteractor(LandmarkDataAccessInterface landmarkDAO,
                                   UserDataAccessInterface userDAO,
                                   SelectedPlaceOutputBoundary presenter) {
        this.landmarkDAO = landmarkDAO;
        this.userDAO = userDAO;
        this.presenter = presenter;
    }

    @Override
    public void selectPlace(SelectedPlaceInputData inputData) {
        Landmark landmark = landmarkDAO.findByName(inputData.getLandmarkName());
        if (landmark == null) {
            System.out.println("[INTERACTOR] Landmark not found: " + inputData.getLandmarkName());
            return;
        }

        LandmarkInfo info = landmark.getLandmarkInfo();

        SelectedPlaceOutputData output = new SelectedPlaceOutputData(
                inputData.getUsername(),
                landmark.getLandmarkName(),
                info.getDescription(),
                info.getAddress(),
                info.getOpenHours()
        );

        presenter.presentPlace(output);
    }

    @Override
    public void checkIn(SelectedPlaceInputData inputData) {
        String username = inputData.getUsername();
        String landmarkName = inputData.getLandmarkName();

        User user = userDAO.get(username);
        if (user == null) {
            System.out.println("[INTERACTOR] Cannot check in: user not found " + username);
            return;
        }

        Landmark landmark = landmarkDAO.findByName(landmarkName);
        if (landmark == null) {
            System.out.println("[INTERACTOR] Cannot check in: landmark not found " + landmarkName);
            return;
        }

        List<Visit> visits = user.getVisits();
        if (visits == null) {
            System.out.println("[INTERACTOR] User visits list is null; cannot record visit.");
        } else {
            visits.add(new Visit(landmark));
            System.out.println("[INTERACTOR] Added visit for user " + username +
                    " at landmark " + landmarkName);
        }

        userDAO.save(user);

        LandmarkInfo info = landmark.getLandmarkInfo();
        SelectedPlaceOutputData output = new SelectedPlaceOutputData(
                username,
                landmark.getLandmarkName(),
                info.getDescription(),
                info.getAddress(),
                info.getOpenHours()
        );
        presenter.presentPlace(output);
    }

    @Override
    public void notes(SelectedPlaceInputData inputData) {
        String username = inputData.getUsername();
        String landmarkName = inputData.getLandmarkName();

        // 1. Load user
        User user = userDAO.get(username);
        if (user == null) {
            System.out.println("[NOTES] User not found: " + username);
            return;
        }

        // 2. Load landmark
        Landmark landmark;
        try {
            landmark = landmarkDAO.findByName(landmarkName);
        } catch (RuntimeException ex) {
            System.out.println("[NOTES] Landmark not found: " + landmarkName);
            return;
        }

        // 3. Filter THIS USER's notes for THIS LANDMARK
        List<Note> allNotes = user.getPrivateNotes();
        List<Note> filtered = allNotes.stream()
                .filter(n -> n.getLandmark() != null
                        && n.getLandmark().getLandmarkName().equalsIgnoreCase(landmarkName))
                .collect(Collectors.toList());

        // 4. Build output with landmark info + notes
        LandmarkInfo info = landmark.getLandmarkInfo();
        SelectedPlaceOutputData output = new SelectedPlaceOutputData(
                user.getUsername(),
                landmark.getLandmarkName(),
                info.getDescription(),
                info.getAddress(),
                info.getOpenHours(),
                filtered
        );

        // 5. Presenter will switch to Notes view and visualize these notes
        presenter.presentNotes(output);

        System.out.println("[NOTES] Navigating to Notes view for " +
                user.getUsername() + " @ " + landmark.getLandmarkName() +
                " with " + filtered.size() + " notes");
    }
}
