package use_case.undovisit;

import entity.User;
import entity.Visit;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Interactor for the Undo Visit Use Case.
 * Implements the business logic for removing a visit from a user's history.
 * This class adheres to the Single Responsibility Principle by focusing
 * solely on the undo visit business logic.
 */
public class UndoVisitInteractor implements UndoVisitInputBoundary {

    private final UndoVisitUserDataAccessInterface userDataAccess;
    private final UndoVisitOutputBoundary outputBoundary;

    // Date format matching the UI requirements
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMMM dd yyyy, HH:mm");

    /**
     * Constructs the Undo Visit interactor.
     * Dependencies are injected to support Dependency Inversion Principle.
     *
     * @param userDataAccess provides access to user data
     * @param outputBoundary handles presentation of results
     */
    public UndoVisitInteractor(UndoVisitUserDataAccessInterface userDataAccess,
                                UndoVisitOutputBoundary outputBoundary) {
        this.userDataAccess = userDataAccess;
        this.outputBoundary = outputBoundary;
    }

    /**
     * Executes the undo visit use case.
     * Removes the specified visit and returns updated history.
     *
     * @param inputData contains the username and visit ID to remove
     */
    @Override
    public void execute(UndoVisitInputData inputData) {
        String username = inputData.getUsername();
        String visitId = inputData.getVisitId();

        // Retrieve user from data access
        User user = userDataAccess.get(username);

        if (user == null) {
            outputBoundary.prepareFailView("User not found: " + username);
            return;
        }

        // Find and remove the visit with the specified ID
        List<Visit> visits = user.getVisits();
        boolean removed = visits.removeIf(visit -> visit.getVisitId().equals(visitId));

        if (!removed) {
            outputBoundary.prepareFailView("Visit not found with ID: " + visitId);
            return;
        }

        // Save the updated user data
        userDataAccess.save(user);

        // Convert remaining visits to DTOs for display
        List<UndoVisitOutputData.VisitDTO> visitDTOs = new ArrayList<>();

        visits.stream()
                .sorted(Comparator.comparing(Visit::getVisitedAt).reversed())
                .forEach(visit -> {
                    String formattedDate = DISPLAY_FORMATTER
                            .withZone(ZoneId.systemDefault())
                            .format(visit.getVisitedAt());

                    visitDTOs.add(new UndoVisitOutputData.VisitDTO(
                            visit.getVisitId(),
                            visit.getLandmark().getLandmarkName(),
                            formattedDate
                    ));
                });

        // Prepare and present success output
        UndoVisitOutputData outputData = new UndoVisitOutputData(
                username,
                visitDTOs,
                "Visit removed successfully"
        );
        outputBoundary.prepareSuccessView(outputData);
    }
}
