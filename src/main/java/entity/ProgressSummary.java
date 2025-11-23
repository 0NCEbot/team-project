package entity;

import java.time.Instant;

/**
 * Entity representing a summary of a user's exploration progress.
 * This entity encapsulates the core business data about how much of the
 * campus a user has explored.
 *
 * This class is part of the Entity layer and contains enterprise business rules.
 * It is framework-agnostic and has no dependencies on outer layers.
 */
public class ProgressSummary {

    private final int visitedCount;
    private final int totalLandmarks;
    private final double completionPercent;
    private final Instant lastVisitedAt;

    /**
     * Constructs a progress summary with all fields.
     *
     * @param visitedCount the number of landmarks the user has visited
     * @param totalLandmarks the total number of landmarks available to visit
     * @param lastVisitedAt the timestamp of the most recent visit, or null if no visits
     */
    public ProgressSummary(int visitedCount, int totalLandmarks, Instant lastVisitedAt) {
        if (visitedCount < 0) {
            throw new IllegalArgumentException("Visited count cannot be negative");
        }
        if (totalLandmarks <= 0) {
            throw new IllegalArgumentException("Total landmarks must be positive");
        }
        if (visitedCount > totalLandmarks) {
            throw new IllegalArgumentException("Visited count cannot exceed total landmarks");
        }

        this.visitedCount = visitedCount;
        this.totalLandmarks = totalLandmarks;
        this.completionPercent = totalLandmarks > 0
            ? (visitedCount * 100.0 / totalLandmarks)
            : 0.0;
        this.lastVisitedAt = lastVisitedAt;
    }

    /**
     * Gets the number of landmarks visited.
     *
     * @return the visited count
     */
    public int getVisitedCount() {
        return visitedCount;
    }

    /**
     * Gets the total number of landmarks available.
     *
     * @return the total landmarks count
     */
    public int getTotalLandmarks() {
        return totalLandmarks;
    }

    /**
     * Gets the completion percentage (0-100).
     *
     * @return the completion percentage
     */
    public double getCompletionPercent() {
        return completionPercent;
    }

    /**
     * Gets the timestamp of the most recent visit.
     *
     * @return the last visited timestamp, or null if no visits yet
     */
    public Instant getLastVisitedAt() {
        return lastVisitedAt;
    }

    /**
     * Checks if the user has visited any landmarks.
     *
     * @return true if at least one landmark has been visited, false otherwise
     */
    public boolean hasVisits() {
        return visitedCount > 0;
    }
}
