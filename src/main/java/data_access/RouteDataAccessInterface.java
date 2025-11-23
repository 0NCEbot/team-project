package data_access;

import entity.RouteStep;
import java.util.List;

public interface RouteDataAccessInterface {

    RouteResponse getRoute(String start, String destination, String[] intermediates);

    class RouteResponse {
        private final List<RouteStep> steps;
        private final int totalDistanceMeters;
        private final int totalDurationSeconds;
        private final boolean successful;
        private final String errorMessage;
        private final boolean manualMode;

        public RouteResponse(List<RouteStep> steps, int dist, int duration, boolean success) {
            this(steps, dist, duration, success, null, false);
        }

        public RouteResponse(List<RouteStep> steps, int dist, int duration, boolean success, String errorMessage) {
            this(steps, dist, duration, success, errorMessage, false);
        }

        // Full constructor with manual mode flag
        public RouteResponse(List<RouteStep> steps, int dist, int duration, boolean success, String errorMessage, boolean manualMode) {
            this.steps = steps;
            this.totalDistanceMeters = dist;
            this.totalDurationSeconds = duration;
            this.successful = success;
            this.errorMessage = errorMessage;
            this.manualMode = manualMode;
        }

        public List<RouteStep> getSteps() { return steps; }
        public int getTotalDistanceMeters() { return totalDistanceMeters; }
        public int getTotalDurationSeconds() { return totalDurationSeconds; }
        public boolean isSuccessful() { return successful; }
        public String getErrorMessage() { return errorMessage; }
        public boolean isManualMode() { return manualMode; }
    }
}