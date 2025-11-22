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

        public RouteResponse(List<RouteStep> steps, int dist, int duration, boolean success) {
            this.steps = steps;
            this.totalDistanceMeters = dist;
            this.totalDurationSeconds = duration;
            this.successful = success;
        }

        public List<RouteStep> getSteps() { return steps; }
        public int getTotalDistanceMeters() { return totalDistanceMeters; }
        public int getTotalDurationSeconds() { return totalDurationSeconds; }
        public boolean isSuccessful() { return successful; }
    }
}