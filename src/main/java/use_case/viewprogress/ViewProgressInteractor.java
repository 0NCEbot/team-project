package use_case.viewprogress;

import entity.ProgressSummary;
import entity.User;
import entity.Visit;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

/**
 * Interactor for the View Progress Use Case.
 * Implements the business logic for retrieving and calculating a user's exploration progress.
 * This class adheres to the Single Responsibility Principle by focusing solely
 * on the view progress business logic.
 *
 * The interactor orchestrates data retrieval from multiple sources (user data and landmark data)
 * and calculates progress metrics, demonstrating the core principle of Use Case interactors
 * coordinating between entities and data access objects.
 */
public class ViewProgressInteractor implements ViewProgressInputBoundary {

    private final ViewProgressUserDataAccessInterface userDataAccess;
    private final ViewProgressLandmarkDataAccessInterface landmarkDataAccess;
    private final ViewProgressOutputBoundary outputBoundary;

    // Date format for displaying last visit: "February 15 2025, 23:59"
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMMM dd yyyy, HH:mm");

    /**
     * Constructs the View Progress interactor.
     * Dependencies are injected to support Dependency Inversion Principle.
     *
     * @param userDataAccess provides access to user data
     * @param landmarkDataAccess provides access to landmark data
     * @param outputBoundary handles presentation of results
     */
    public ViewProgressInteractor(ViewProgressUserDataAccessInterface userDataAccess,
                                   ViewProgressLandmarkDataAccessInterface landmarkDataAccess,
                                   ViewProgressOutputBoundary outputBoundary) {
        this.userDataAccess = userDataAccess;
        this.landmarkDataAccess = landmarkDataAccess;
        this.outputBoundary = outputBoundary;
    }

    /**
     * Executes the view progress use case.
     * Retrieves user data, calculates progress statistics, and presents them.
     *
     * @param inputData contains the username whose progress should be retrieved
     */
    @Override
    public void execute(ViewProgressInputData inputData) {
        String username = inputData.getUsername();

        // Retrieve user from data access
        User user = userDataAccess.get(username);

        if (user == null) {
            outputBoundary.prepareFailView("User not found: " + username);
            return;
        }

        // Get total number of landmarks available
        int totalLandmarks = landmarkDataAccess.getLandmarks().size();

        // Get user's visits
        List<Visit> visits = user.getVisits();

        // Calculate total visits (all visit entries)
        int totalVisits = visits.size();

        // Calculate unique landmarks visited
        int visitedCount = (int) visits.stream()
                .map(visit -> visit.getLandmark().getLandmarkName())
                .distinct()
                .count();

        // Find the most recent visit timestamp
        Instant lastVisitedAt = null;
        if (!visits.isEmpty()) {
            lastVisitedAt = visits.stream()
                    .map(Visit::getVisitedAt)
                    .max(Comparator.naturalOrder())
                    .orElse(null);
        }

        // Create progress summary entity
        ProgressSummary summary = new ProgressSummary(visitedCount, totalLandmarks, lastVisitedAt);

        // Format the data for presentation
        String completionPercent = String.format("%.1f%%", summary.getCompletionPercent());

        String formattedLastVisit = null;
        if (summary.getLastVisitedAt() != null) {
            formattedLastVisit = DISPLAY_FORMATTER
                    .withZone(ZoneId.systemDefault())
                    .format(summary.getLastVisitedAt());
        }

        // Create output data and present
        ViewProgressOutputData outputData = new ViewProgressOutputData(
                username,
                summary.getVisitedCount(),
                summary.getTotalLandmarks(),
                totalVisits,
                completionPercent,
                formattedLastVisit,
                summary.hasVisits()
        );

        outputBoundary.prepareSuccessView(outputData);
    }
}
