package use_case.undovisit;

import java.util.List;

/**
 * Output Data Transfer Object for the Undo Visit Use Case.
 * Contains the updated visit history and success message after removing a visit.
 */
public class UndoVisitOutputData {

    /**
     * Data Transfer Object representing a visit for display.
     * Reuses the pattern established in ViewHistoryOutputData.
     */
    public static class VisitDTO {
        public final String visitId;
        public final String landmarkName;
        public final String visitedAt;

        /**
         * Constructs a visit DTO.
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
    private final String successMessage;

    /**
     * Constructs output data after successfully undoing a visit.
     *
     * @param username the username whose visit was removed
     * @param visits updated list of remaining visits
     * @param successMessage confirmation message to display
     */
    public UndoVisitOutputData(String username, List<VisitDTO> visits, String successMessage) {
        this.username = username;
        this.visits = visits;
        this.successMessage = successMessage;
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
     * Gets the updated list of visits.
     *
     * @return list of visit DTOs
     */
    public List<VisitDTO> getVisits() {
        return visits;
    }

    /**
     * Gets the success message.
     *
     * @return the success message
     */
    public String getSuccessMessage() {
        return successMessage;
    }
}
