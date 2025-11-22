package data_access;

import entity.Location;
import entity.RouteStep;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsRouteDataAccessObject implements RouteDataAccessInterface {

    private static final String API_KEY = "AIzaSyCk9bPskLw7eUI-_Y9G6tW8eDAE-iXI8Ms";
    private static final String ROUTES_API_URL = "https://routes.googleapis.com/directions/v2:computeRoutes";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient httpClient = new OkHttpClient();
    private final LandmarkDataAccessInterface landmarkDAO;

    public MapsRouteDataAccessObject(LandmarkDataAccessInterface landmarkDAO) {
        this.landmarkDAO = landmarkDAO;
    }

    @Override
    public RouteResponse getRoute(String startLocationName, String destinationName, String[] intermediateStopNames) {
        try {
            // Step 1: Resolve landmark names to coordinates
            Location startLocation = resolveLandmarkToLocation(startLocationName);
            Location destinationLocation = resolveLandmarkToLocation(destinationName);

            if (startLocation == null) {
                System.out.println("[ROUTE DAO] Start location not found: " + startLocationName);
                return null;
            }
            if (destinationLocation == null) {
                System.out.println("[ROUTE DAO] Destination not found: " + destinationName);
                return null;
            }

            // Step 2: Build request body
            String requestBody = buildRouteRequest(startLocation, destinationLocation, intermediateStopNames);

            // Step 3: Call Google Routes API
            JSONObject responseJson = callRoutesAPI(requestBody);
            if (responseJson == null) {
                return null;
            }

            // Step 4: Parse response
            List<RouteStep> steps = extractSteps(responseJson);
            int totalDistance = responseJson
                    .getJSONArray("routes")
                    .getJSONObject(0)
                    .getInt("distanceMeters");

            String durationStr = responseJson
                    .getJSONArray("routes")
                    .getJSONObject(0)
                    .getString("duration");
            int totalDuration = parseDurationToSeconds(durationStr);

            return new RouteResponse(steps, totalDistance, totalDuration, true);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Resolve a landmark name to coordinates using fuzzy matching.
     * If exact match fails, tries case-insensitive and partial matching.
     */
    private Location resolveLandmarkToLocation(String landmarkName) {
        if (landmarkName == null || landmarkName.isBlank()) {
            return null;
        }

        // Try exact match (case-insensitive)
        for (var landmark : landmarkDAO.getLandmarks()) {
            if (landmark.getLandmarkName().equalsIgnoreCase(landmarkName.trim())) {
                return landmark.getLocation();
            }
        }

        // Try partial match (contains substring, case-insensitive)
        String lowerQuery = landmarkName.toLowerCase().trim();
        for (var landmark : landmarkDAO.getLandmarks()) {
            if (landmark.getLandmarkName().toLowerCase().contains(lowerQuery)) {
                System.out.println("[ROUTE DAO] Partial matched '" + landmarkName + "' to '" + landmark.getLandmarkName() + "'");
                return landmark.getLocation();
            }
        }

        return null;
    }

    /**
     * Build the JSON request body for Google Routes API.
     */
    private String buildRouteRequest(Location start, Location destination, String[] intermediates) {
        JSONObject request = new JSONObject();

        // Origin
        JSONObject origin = new JSONObject();
        origin.put("location", createLatLngJson(start));
        request.put("origin", origin);

        // Destination
        JSONObject dest = new JSONObject();
        dest.put("location", createLatLngJson(destination));
        request.put("destination", dest);

        // Intermediate waypoints (if any)
        if (intermediates != null && intermediates.length > 0) {
            JSONArray waypoints = new JSONArray();
            for (String intermediateName : intermediates) {
                Location intermediateLoc = resolveLandmarkToLocation(intermediateName);
                if (intermediateLoc != null) {
                    JSONObject waypoint = new JSONObject();
                    waypoint.put("location", createLatLngJson(intermediateLoc));
                    waypoints.put(waypoint);
                }
            }
            if (waypoints.length() > 0) {
                request.put("intermediates", waypoints);
            }
        }

        // Travel mode and field mask
        request.put("travelMode", "WALK");

        return request.toString();
    }

    /**
     * Helper to create {"latitude": x, "longitude": y} JSON structure
     */
    private JSONObject createLatLngJson(Location location) {
        JSONObject latLng = new JSONObject();
        latLng.put("latitude", location.getLatitude());
        latLng.put("longitude", location.getLongitude());
        return latLng;
    }

    /**
     * Call the Google Routes API
     */
    private JSONObject callRoutesAPI(String requestBody) throws IOException {
        RequestBody body = RequestBody.create(requestBody, JSON);

        Request request = new Request.Builder()
                .url(ROUTES_API_URL)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-Goog-Api-Key", API_KEY)
                .addHeader("X-Goog-FieldMask", "routes.duration,routes.distanceMeters,routes.legs.steps")
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
//            if (!response.isSuccessful() || response.body() == null) {
//                System.out.println("[ROUTE DAO] API call failed: " + response.code());
//                return null;
//            }

            String responseData = response.body().string();
            System.out.println("[ROUTE DAO] API Response received");
            return new JSONObject(responseData);
        }
    }

    /**
     * Extract individual route steps from the API response.
     * The response has structure: routes[0].legs[0].steps[0..n]
     */
    private List<RouteStep> extractSteps(JSONObject responseJson) {
        List<RouteStep> steps = new ArrayList<>();

        try {
            JSONArray routes = responseJson.optJSONArray("routes");
            if (routes == null || routes.length() == 0) {
                return steps;
            }

            JSONObject route = routes.getJSONObject(0);
            JSONArray legs = route.optJSONArray("legs");
            if (legs == null) {
                return steps;
            }

            int stepIndex = 0;

            // Each leg can have multiple steps
            for (int legIdx = 0; legIdx < legs.length(); legIdx++) {
                JSONObject leg = legs.getJSONObject(legIdx);
                JSONArray stepsArray = leg.optJSONArray("steps");

                if (stepsArray == null) {
                    continue;
                }

                // Process each step in the leg
                for (int stepIdx = 0; stepIdx < stepsArray.length(); stepIdx++) {
                    JSONObject stepJson = stepsArray.getJSONObject(stepIdx);

                    // Extract instruction (from navigationInstruction)
                    String instruction = extractInstruction(stepJson);

                    // Extract distance in meters
                    int distanceMeters = stepJson.optInt("distanceMeters", 0);

                    // Extract duration in seconds (from staticDuration, e.g. "45s")
                    int durationSeconds = extractDurationSeconds(stepJson);

                    RouteStep step = new RouteStep(stepIndex, instruction, distanceMeters, durationSeconds);
                    steps.add(step);
                    stepIndex++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return steps;
    }

    /**
     * Extract the human-readable instruction from a step.
     * Format: "Turn right onto St. George Street" or "Head northwest"
     */
    private String extractInstruction(JSONObject stepJson) {
        try {
            JSONObject navInstruction = stepJson.optJSONObject("navigationInstruction");
            if (navInstruction != null) {
                String instructions = navInstruction.optString("instructions", "");
                if (!instructions.isEmpty()) {
                    // Remove newlines for single-line display
                    return instructions.replace("\n", " ");
                }
            }
        } catch (Exception e) {
            // Fall back to distance text
        }

        // Fallback: use localized distance
        try {
            JSONObject localizedValues = stepJson.optJSONObject("localizedValues");
            if (localizedValues != null) {
                JSONObject distance = localizedValues.optJSONObject("distance");
                if (distance != null) {
                    String distText = distance.optString("text", "");
                    if (!distText.isEmpty()) {
                        return "Walk " + distText;
                    }
                }
            }
        } catch (Exception e) {
            // Ignore
        }

        return "Continue";
    }

    /**
     * Parse duration from staticDuration field (e.g. "45s" or "2 mins")
     * Returns seconds as an integer.
     */
    private int extractDurationSeconds(JSONObject stepJson) {
        try {
            // Try to get from staticDuration field first
            String staticDuration = stepJson.optString("staticDuration", "");
            if (!staticDuration.isEmpty()) {
                return parseDurationToSeconds(staticDuration);
            }

            // Fallback to localizedValues
            JSONObject localizedValues = stepJson.optJSONObject("localizedValues");
            if (localizedValues != null) {
                JSONObject duration = localizedValues.optJSONObject("staticDuration");
                if (duration != null) {
                    String text = duration.optString("text", "");
                    return parseDurationToSeconds(text);
                }
            }
        } catch (Exception e) {
            // Ignore
        }

        return 0;
    }

    /**
     * Parse duration strings like "45s", "1 min", "2 mins" into seconds.
     * Also handles ISO format like "427s".
     */
    private int parseDurationToSeconds(String durationStr) {
        if (durationStr == null || durationStr.isBlank()) {
            return 0;
        }

        durationStr = durationStr.trim().toLowerCase();

        try {
            // Handle "45s" or "427s" format
            if (durationStr.endsWith("s")) {
                String numStr = durationStr.substring(0, durationStr.length() - 1);
                return Integer.parseInt(numStr);
            }

            // Handle "1 min" or "2 mins" format
            if (durationStr.contains("min")) {
                String numStr = durationStr.split("\\s+")[0];
                int minutes = Integer.parseInt(numStr);
                return minutes * 60;
            }

            // Handle "1 hour" or "2 hours" format
            if (durationStr.contains("hour")) {
                String numStr = durationStr.split("\\s+")[0];
                int hours = Integer.parseInt(numStr);
                return hours * 3600;
            }

        } catch (Exception e) {
            System.out.println("[ROUTE DAO] Failed to parse duration: " + durationStr);
        }

        return 0;
    }
}