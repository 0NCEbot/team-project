package use_case.viewprogress;

/**
 * Output Data Transfer Object for the View Progress Use Case.
 * Contains the formatted progress data to be presented to the user.
 * This class maintains clean separation between entity models and presentation data.
 */
public class ViewProgressOutputData {

    private final String username;
    private final int visitedCount;
    private final int totalLandmarks;
    private final int totalVisits;
    private final String completionPercent;
    private final String lastVisitedAt;
    private final boolean hasVisits;

    /**
     * Constructs output data containing progress summary.
     *
     * @param username the username whose progress is being displayed
     * @param visitedCount number of unique landmarks visited
     * @param totalLandmarks total number of landmarks available
     * @param totalVisits total number of visit entries (can include multiple visits to same landmark)
     * @param completionPercent formatted completion percentage (e.g., "50.0%")
     * @param lastVisitedAt formatted timestamp of last visit, or null if no visits
     * @param hasVisits whether the user has any visits
     */
    public ViewProgressOutputData(String username, int visitedCount, int totalLandmarks,
                                   int totalVisits, String completionPercent, String lastVisitedAt,
                                   boolean hasVisits) {
        this.username = username;
        this.visitedCount = visitedCount;
        this.totalLandmarks = totalLandmarks;
        this.totalVisits = totalVisits;
        this.completionPercent = completionPercent;
        this.lastVisitedAt = lastVisitedAt;
        this.hasVisits = hasVisits;
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
     * Gets the number of unique landmarks visited.
     *
     * @return the visited count
     */
    public int getVisitedCount() {
        return visitedCount;
    }

    /**
     * Gets the total number of landmarks.
     *
     * @return the total landmarks
     */
    public int getTotalLandmarks() {
        return totalLandmarks;
    }

    /**
     * Gets the total number of visits (including multiple visits to same landmark).
     *
     * @return the total visits
     */
    public int getTotalVisits() {
        return totalVisits;
    }

    /**
     * Gets the formatted completion percentage.
     *
     * @return the completion percentage string
     */
    public String getCompletionPercent() {
        return completionPercent;
    }

    /**
     * Gets the formatted last visited timestamp.
     *
     * @return the last visited timestamp, or null if no visits
     */
    public String getLastVisitedAt() {
        return lastVisitedAt;
    }

    /**
     * Checks if the user has any visits.
     *
     * @return true if user has visits, false otherwise
     */
    public boolean hasVisits() {
        return hasVisits;
    }
}
