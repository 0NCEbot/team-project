package use_case.selectedplace;

import data_access.LandmarkDataAccessInterface;
import data_access.UserDataAccessInterface;
import entity.Landmark;
import entity.LandmarkInfo;
import entity.User;
import entity.Visit;

import java.util.List;

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
            // in a real app you’d call an error presenter
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

        // get user
        User user = userDAO.get(username);
        if (user == null) {
            System.out.println("[INTERACTOR] Cannot check in: user not found " + username);
            return;
        }

        //get the landmark
        Landmark landmark = landmarkDAO.findByName(landmarkName);
        if (landmark == null) {
            System.out.println("[INTERACTOR] Cannot check in: landmark not found " + landmarkName);
            return;
        }

        // 3. append visit
        List<Visit> visits = user.getVisits();
        if (visits == null) {
            System.out.println("[INTERACTOR] User visits list is null; cannot record visit.");
        } else {
            visits.add(new Visit(landmark));
            System.out.println("[INTERACTOR] Added visit for user " + username +
                    " at landmark " + landmarkName);
        }

        //save updated user (overwrites existing entry)
        userDAO.save(user);

        //re-present the same place data so the view stays in sync
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

        // 1. Get the user
        User user = userDAO.get(inputData.getUsername());
        if (user == null) {
            System.out.println("[NOTES] User not found: " + inputData.getUsername());
            return;
        }

        // 2. Get the landmark
        Landmark landmark;
        try {
            landmark = landmarkDAO.findByName(inputData.getLandmarkName());
        } catch (RuntimeException ex) {
            System.out.println("[NOTES] Landmark not found: " + inputData.getLandmarkName());
            return;
        }

        // 3. Create a minimal SelectedPlaceOutputData for the presenter
        // This is required because UI needs username + landmarkName to build the next view.
        SelectedPlaceOutputData output = new SelectedPlaceOutputData(
                user.getUsername(),
                landmark.getLandmarkName(),
                landmark.getLandmarkInfo().getDescription(),
                landmark.getLandmarkInfo().getAddress(),
                landmark.getLandmarkInfo().getOpenHours()
        );

        // 4. Tell Presenter to switch to NotesView
        presenter.presentNotes(output);

        System.out.println("[NOTES] Navigating to Notes view for " +
                user.getUsername() + " @ " + landmark.getLandmarkName());
    }
}
