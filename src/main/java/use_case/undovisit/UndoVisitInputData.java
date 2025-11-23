package use_case.undovisit;

/**
 * Input Data Transfer Object for the Undo Visit Use Case.
 * Encapsulates the data needed to remove a visit from history.
 */
public class UndoVisitInputData {

    private final String username;
    private final String visitId;

    /**
     * Constructs input data for undoing a visit.
     *
     * @param username the username whose visit should be removed
     * @param visitId the unique identifier of the visit to remove
     */
    public UndoVisitInputData(String username, String visitId) {
        this.username = username;
        this.visitId = visitId;
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
     * Gets the visit ID to remove.
     *
     * @return the visit ID
     */
    public String getVisitId() {
        return visitId;
    }
}
