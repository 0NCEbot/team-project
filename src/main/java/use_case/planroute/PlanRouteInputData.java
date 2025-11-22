package use_case.planroute;

public class PlanRouteInputData {
    private final String username;
    private final String startLocation;
    private final String destination;
    private final String[] intermediateStops;

    public PlanRouteInputData(String username, String startLocation,
                              String destination, String[] intermediateStops) {
        this.username = username;
        this.startLocation = startLocation;
        this.destination = destination;
        this.intermediateStops = intermediateStops != null ? intermediateStops : new String[0];
    }

    public String getUsername() { return username; }
    public String getStartLocation() { return startLocation; }
    public String getDestination() { return destination; }
    public String[] getIntermediateStops() { return intermediateStops; }
}