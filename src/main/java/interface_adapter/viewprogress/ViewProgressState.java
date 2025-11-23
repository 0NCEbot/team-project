package interface_adapter.viewprogress;

/**
 * State object for the View Progress View.
 * Holds all the data needed to render the progress summary screen.
 * This class follows the State pattern and is part of the Interface Adapter layer,
 * converting use case output into a format suitable for the view.
 */
public class ViewProgressState {

    private String username = "";
    private int visitedCount = 0;
    private int totalLandmarks = 0;
    private int totalVisits = 0;
    private String completionPercent = "0.0%";
    private String lastVisitedAt = null;
    private boolean hasVisits = false;
    private String errorMessage = null;

    /**
     * Default constructor.
     */
    public ViewProgressState() {
    }

    /**
     * Copy constructor for creating a new state from an existing one.
     * Useful for immutability patterns in state management.
     *
     * @param copy the state to copy from
     */
    public ViewProgressState(ViewProgressState copy) {
        this.username = copy.username;
        this.visitedCount = copy.visitedCount;
        this.totalLandmarks = copy.totalLandmarks;
        this.totalVisits = copy.totalVisits;
        this.completionPercent = copy.completionPercent;
        this.lastVisitedAt = copy.lastVisitedAt;
        this.hasVisits = copy.hasVisits;
        this.errorMessage = copy.errorMessage;
    }

    // Getters and setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getVisitedCount() {
        return visitedCount;
    }

    public void setVisitedCount(int visitedCount) {
        this.visitedCount = visitedCount;
    }

    public int getTotalLandmarks() {
        return totalLandmarks;
    }

    public void setTotalLandmarks(int totalLandmarks) {
        this.totalLandmarks = totalLandmarks;
    }

    public int getTotalVisits() {
        return totalVisits;
    }

    public void setTotalVisits(int totalVisits) {
        this.totalVisits = totalVisits;
    }

    public String getCompletionPercent() {
        return completionPercent;
    }

    public void setCompletionPercent(String completionPercent) {
        this.completionPercent = completionPercent;
    }

    public String getLastVisitedAt() {
        return lastVisitedAt;
    }

    public void setLastVisitedAt(String lastVisitedAt) {
        this.lastVisitedAt = lastVisitedAt;
    }

    public boolean hasVisits() {
        return hasVisits;
    }

    public void setHasVisits(boolean hasVisits) {
        this.hasVisits = hasVisits;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
