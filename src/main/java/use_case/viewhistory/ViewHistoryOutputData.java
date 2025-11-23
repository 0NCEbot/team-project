package use_case.viewhistory;

import java.util.List;

/**
 * Output Data Transfer Object for the View History Use Case.
 * Contains the formatted visit history data to be presented to the user.
 * Uses a nested DTO pattern to maintain clean separation between
 * entity models and presentation data.
 */
public class ViewHistoryOutputData {

    /**
     * Data Transfer Object representing a single visit for display.
     * Contains only the data needed for presentation, not full entity objects.
     */
    public static class VisitDTO {
        public final String visitId;
        public final String landmarkName;
        public final String visitedAt;

        /**
         * Constructs a visit DTO for presentation.
         *
         * @param visitId unique identifier for the visit
         * @param landmarkName name of the visited landmark
         * @param visitedAt formatted timestamp of the visit
         */
        public VisitDTO(String visitId, String landmarkName, String visitedAt) {
            this.visitId = visitId;
            this.landmarkName = landmarkName;
            this.visitedAt = visitedAt;
        }
    }

    private final String username;
    private final List<VisitDTO> visits;

    /**
     * Constructs output data containing visit history.
     *
     * @param username the username whose visits are being displayed
     * @param visits list of visit DTOs in display format
     */
    public ViewHistoryOutputData(String username, List<VisitDTO> visits) {
        this.username = username;
        this.visits = visits;
    }

    /**
     * Gets the username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the list of visits.
     *
     * @return unmodifiable list of visit DTOs
     */
    public List<VisitDTO> getVisits() {
        return visits;
    }
}
