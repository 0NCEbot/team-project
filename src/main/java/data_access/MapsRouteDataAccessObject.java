package data_access;

import entity.Location;
import entity.RouteStep;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            // Step 1: Resolve landmark names to coordinates AND track canonical names
            Map<String, String> nameResolutions = new HashMap<>();

            Location startLocation = resolveLandmarkToLocation(startLocationName, nameResolutions);
            Location destinationLocation = resolveLandmarkToLocation(destinationName, nameResolutions);

            // Check for invalid landmarks BEFORE proceeding
            if (startLocation == null) {
                System.out.println("[ROUTE DAO] Start location not found: " + startLocationName);
                return new RouteResponse(
                        new ArrayList<>(), 0, 0, false,
                        "Start location '" + startLocationName + "' not found. Please enter a valid landmark name."
                );
            }
            if (destinationLocation == null) {
                System.out.println("[ROUTE DAO] Destination not found: " + destinationName);
                return new RouteResponse(
                        new ArrayList<>(), 0, 0, false,
                        "Destination '" + destinationName + "' not found. Please enter a valid landmark name."
                );
            }

            // Get canonical names (after fuzzy matching)
            String resolvedStartName = nameResolutions.get(startLocationName);
            String resolvedDestName = nameResolutions.get(destinationName);

            // Step 2: Build a list to track all waypoints with names
            List<String> waypointNames = new ArrayList<>();
            List<Location> waypointLocations = new ArrayList<>();

            // Add intermediates
            if (intermediateStopNames != null) {
                for (String intermediateName : intermediateStopNames) {
                    Location loc = resolveLandmarkToLocation(intermediateName, nameResolutions);
                    if (loc != null) {
                        String resolvedName = nameResolutions.get(intermediateName);
                        waypointNames.add(resolvedName);
                        waypointLocations.add(loc);
                    } else {
                        // Invalid intermediate stop
                        System.out.println("[ROUTE DAO] Intermediate stop not found: " + intermediateName);
                        return new RouteResponse(
                                new ArrayList<>(), 0, 0, false,
                                "Intermediate stop '" + intermediateName + "' not found. Please enter a valid landmark name."
                        );
                    }
                }
            }

            // Step 3: Build request body
            String requestBody = buildRouteRequest(startLocation, destinationLocation,
                    waypointLocations.toArray(new Location[0]));

            // Step 4: Call Google Routes API
            JSONObject responseJson = callRoutesAPI(requestBody);
            if (responseJson == null) {
                // API failed - use manual mode with resolved names
                return handleManualMode(resolvedStartName, resolvedDestName, waypointNames);
            }

            // Step 5: Parse response with RESOLVED landmark names
            List<RouteStep> steps = extractStepsWithLandmarks(
                    responseJson,
                    resolvedStartName,
                    resolvedDestName,
                    waypointNames
            );

            int totalDistance = responseJson
                    .getJSONArray("routes")
                    .getJSONObject(0)
                    .getInt("distanceMeters");

            String durationStr = responseJson
                    .getJSONArray("routes")
                    .getJSONObject(0)
                    .getString("duration");
            int totalDuration = parseDurationToSeconds(durationStr);

            return new RouteResponse(steps, totalDistance, totalDuration, true, null, false);

        } catch (Exception e) {
            e.printStackTrace();
            return new RouteResponse(
                    new ArrayList<>(), 0, 0, false,
                    "An error occurred while planning the route. Please try again."
            );
        }
    }

    /**
     * Resolve a landmark name to coordinates using fuzzy matching.
     * Stores the canonical name in nameResolutions map for later use.
     */
    private Location resolveLandmarkToLocation(String landmarkName, Map<String, String> nameResolutions) {
        if (landmarkName == null || landmarkName.isBlank()) {
            return null;
        }

        // Try exact match (case-insensitive)
        for (var landmark : landmarkDAO.getLandmarks()) {
            if (landmark.getLandmarkName().equalsIgnoreCase(landmarkName.trim())) {
                nameResolutions.put(landmarkName, landmark.getLandmarkName());
                return landmark.getLocation();
            }
        }

        // Try partial match (contains substring, case-insensitive)
        String lowerQuery = landmarkName.toLowerCase().trim();
        for (var landmark : landmarkDAO.getLandmarks()) {
            if (landmark.getLandmarkName().toLowerCase().contains(lowerQuery)) {
                System.out.println("[ROUTE DAO] Partial matched '" + landmarkName + "' to '" + landmark.getLandmarkName() + "'");
                nameResolutions.put(landmarkName, landmark.getLandmarkName());
                return landmark.getLocation();
            }
        }

        return null;
    }

    /**
     * Build the JSON request body for Google Routes API.
     */
    private String buildRouteRequest(Location start, Location destination, Location[] intermediates) {
        JSONObject request = new JSONObject();

        // Origin
        JSONObject origin = new JSONObject();
        JSONObject originLocation = new JSONObject();
        originLocation.put("latLng", createLatLngJson(start));
        origin.put("location", originLocation);
        request.put("origin", origin);

        // Destination
        JSONObject dest = new JSONObject();
        JSONObject destLocation = new JSONObject();
        destLocation.put("latLng", createLatLngJson(destination));
        dest.put("location", destLocation);
        request.put("destination", dest);

        // Intermediate waypoints (if any)
        if (intermediates != null && intermediates.length > 0) {
            JSONArray waypoints = new JSONArray();
            for (Location intermediateLoc : intermediates) {
                if (intermediateLoc != null) {
                    JSONObject waypoint = new JSONObject();
                    JSONObject waypointLocation = new JSONObject();
                    waypointLocation.put("latLng", createLatLngJson(intermediateLoc));
                    waypoint.put("location", waypointLocation);
                    waypoints.put(waypoint);
                }
            }
            if (waypoints.length() > 0) {
                request.put("intermediates", waypoints);
            }
        }

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
            if (!response.isSuccessful() || response.body() == null) {
                System.out.println("[ROUTE DAO] API call failed: " + response.code());
                return null;
            }

            String responseData = response.body().string();
            System.out.println("[ROUTE DAO] API Response received");
            return new JSONObject(responseData);
        }
    }

    /**
     * Handle manual mode with resolved landmark names.
     */
    private RouteResponse handleManualMode(String startName, String destName, List<String> intermediateNames) {
        List<RouteStep> manualSteps = new ArrayList<>();

        int stepIndex = 0;

        // Start landmark
        manualSteps.add(new RouteStep(stepIndex++, "📍 " + startName, 0, 0));

        // Intermediate landmarks
        for (String intermediateName : intermediateNames) {
            manualSteps.add(new RouteStep(stepIndex++, "Visit: " + intermediateName, 0, 0));
            manualSteps.add(new RouteStep(stepIndex++, "📍 " + intermediateName, 0, 0));
        }

        // End landmark
        manualSteps.add(new RouteStep(stepIndex++, "Navigate to: " + destName, 0, 0));
        manualSteps.add(new RouteStep(stepIndex, "📍 " + destName, 0, 0));

        return new RouteResponse(
                manualSteps, 0, 0, true,
                "API unavailable. Using self-guided mode with resolved landmark names."
        );
    }

    /**
     * Extract individual route steps from the API response WITH landmark markers.
     */
    private List<RouteStep> extractStepsWithLandmarks(JSONObject responseJson,
                                                      String startName,
                                                      String destName,
                                                      List<String> intermediateNames) {
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

            // Add START landmark step
            steps.add(new RouteStep(stepIndex++, "📍 " + startName, 0, 0));

            // Process each leg
            for (int legIdx = 0; legIdx < legs.length(); legIdx++) {
                JSONObject leg = legs.getJSONObject(legIdx);
                JSONArray stepsArray = leg.optJSONArray("steps");

                if (stepsArray != null) {
                    for (int i = 0; i < stepsArray.length(); i++) {
                        JSONObject stepJson = stepsArray.getJSONObject(i);
                        String instruction = extractInstruction(stepJson);
                        int distanceMeters = stepJson.optInt("distanceMeters", 0);
                        int durationSeconds = extractDurationSeconds(stepJson);

                        steps.add(new RouteStep(stepIndex++, instruction, distanceMeters, durationSeconds));
                    }
                }

                // Add intermediate landmark after each leg (except last)
                if (legIdx < intermediateNames.size()) {
                    steps.add(new RouteStep(stepIndex++, "📍 " + intermediateNames.get(legIdx), 0, 0));
                }
            }

            // Add END landmark step
            steps.add(new RouteStep(stepIndex++, "📍 " + destName, 0, 0));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return steps;
    }

    /**
     * Extract the human-readable instruction from a step.
     */
    private String extractInstruction(JSONObject stepJson) {
        try {
            JSONObject navInstruction = stepJson.optJSONObject("navigationInstruction");
            if (navInstruction != null) {
                String instructions = navInstruction.optString("instructions", "");
                if (!instructions.isEmpty()) {
                    return instructions.replace("\n", " - ");
                }
            }
        } catch (Exception e) {
            // Fall back to distance text
        }

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
     * Parse duration from staticDuration field.
     */
    private int extractDurationSeconds(JSONObject stepJson) {
        try {
            String staticDuration = stepJson.optString("staticDuration", "");
            if (!staticDuration.isEmpty()) {
                return parseDurationToSeconds(staticDuration);
            }

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
     * Parse duration strings into seconds.
     */
    private int parseDurationToSeconds(String durationStr) {
        if (durationStr == null || durationStr.isBlank()) {
            return 0;
        }

        durationStr = durationStr.trim().toLowerCase();

        try {
            if (durationStr.endsWith("s")) {
                String numStr = durationStr.substring(0, durationStr.length() - 1);
                return Integer.parseInt(numStr);
            }

            if (durationStr.contains("min")) {
                String numStr = durationStr.split("\\s+")[0];
                int minutes = Integer.parseInt(numStr);
                return minutes * 60;
            }

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