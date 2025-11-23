package use_case.viewhistory;

import entity.User;
import entity.Visit;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Interactor for the View History Use Case.
 * Implements the business logic for retrieving and formatting a user's visit history.
 * This class adheres to the Single Responsibility Principle by focusing solely
 * on the view history business logic.
 */
public class ViewHistoryInteractor implements ViewHistoryInputBoundary {

    private final ViewHistoryUserDataAccessInterface userDataAccess;
    private final ViewHistoryOutputBoundary outputBoundary;

    // Date formatters for different display scenarios
    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("h:mma");
    private static final DateTimeFormatter FULL_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("EEEE, MMMM d");
    private static final DateTimeFormatter DAY_OF_WEEK_FORMATTER =
            DateTimeFormatter.ofPattern("EEEE");

    /**
     * Constructs the View History interactor.
     * Dependencies are injected to support Dependency Inversion Principle.
     *
     * @param userDataAccess provides access to user data
     * @param outputBoundary handles presentation of results
     */
    public ViewHistoryInteractor(ViewHistoryUserDataAccessInterface userDataAccess,
                                  ViewHistoryOutputBoundary outputBoundary) {
        this.userDataAccess = userDataAccess;
        this.outputBoundary = outputBoundary;
    }

    /**
     * Executes the view history use case.
     * Retrieves the user's visits, formats them for display, and presents them.
     *
     * @param inputData contains the username whose history should be retrieved
     */
    @Override
    public void execute(ViewHistoryInputData inputData) {
        String username = inputData.getUsername();

        // Retrieve user from data access
        User user = userDataAccess.get(username);

        if (user == null) {
            outputBoundary.prepareFailView("User not found: " + username);
            return;
        }

        // Get all visits for this user
        List<Visit> visits = user.getVisits();

        // Convert to DTOs with formatted dates, sorted by most recent first
        List<ViewHistoryOutputData.VisitDTO> visitDTOs = new ArrayList<>();

        // Sort visits by visitedAt in descending order (most recent first)
        visits.stream()
                .sorted(Comparator.comparing(Visit::getVisitedAt).reversed())
                .forEach(visit -> {
                    String formattedDate = formatRelativeDate(visit.getVisitedAt());

                    visitDTOs.add(new ViewHistoryOutputData.VisitDTO(
                            visit.getVisitId(),
                            visit.getLandmark().getLandmarkName(),
                            formattedDate
                    ));
                });

        // Prepare and present the output
        ViewHistoryOutputData outputData = new ViewHistoryOutputData(username, visitDTOs);
        outputBoundary.prepareSuccessView(outputData);
    }

    /**
     * Formats a timestamp as a relative date string.
     * Uses "Today", "Yesterday", or day of week for recent dates, otherwise full date.
     *
     * @param visitTime the visit timestamp
     * @return formatted relative date string (e.g., "Today, 5:00PM" or "Yesterday, 3:30PM")
     */
    private String formatRelativeDate(Instant visitTime) {
        LocalDate visitDate = visitTime.atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        LocalDate yesterday = today.minusDays(1);

        String timeStr = TIME_FORMATTER
                .withZone(ZoneId.systemDefault())
                .format(visitTime);

        // Check if visit was today
        if (visitDate.equals(today)) {
            return "Today, " + timeStr;
        }

        // Check if visit was yesterday
        if (visitDate.equals(yesterday)) {
            return "Yesterday, " + timeStr;
        }

        // For visits within the last week, show day of week
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(visitDate, today);
        if (daysBetween <= 7) {
            String dayOfWeek = DAY_OF_WEEK_FORMATTER
                    .withZone(ZoneId.systemDefault())
                    .format(visitTime);
            return dayOfWeek + ", " + timeStr;
        }

        // For older visits, show full date
        String fullDate = FULL_DATE_FORMATTER
                .withZone(ZoneId.systemDefault())
                .format(visitTime);
        return fullDate + ", " + timeStr;
    }
}
