package use_case.planroute;

import data_access.LandmarkDataAccessInterface;
import data_access.RouteDataAccessInterface;
import entity.RouteStep;

import java.util.ArrayList;
import java.util.List;

public class PlanRouteInteractor implements PlanRouteInputBoundary {

    private final RouteDataAccessInterface routeDAO;
    private final LandmarkDataAccessInterface landmarkDAO;
    private final PlanRouteOutputBoundary presenter;

    public PlanRouteInteractor(RouteDataAccessInterface routeDAO,
                               LandmarkDataAccessInterface landmarkDAO,
                               PlanRouteOutputBoundary presenter) {
        this.routeDAO = routeDAO;
        this.landmarkDAO = landmarkDAO;
        this.presenter = presenter;
    }

    @Override
    public void planRoute(PlanRouteInputData inputData) {
        String start = inputData.getStartLocation();
        String destination = inputData.getDestination();
        String[] intermediates = inputData.getIntermediateStops();

        // VALIDATION: Check inputs
        if (start == null || start.isBlank()) {
            presenter.presentError("Start location cannot be empty.");
            return;
        }
        if (destination == null || destination.isBlank()) {
            presenter.presentError("Destination cannot be empty.");
            return;
        }

        // CALL DAO: Try to fetch route from Google Maps
        try {
            RouteDataAccessInterface.RouteResponse response = routeDAO.getRoute(start, destination, intermediates);

            if (response == null) {
                presenter.presentError("Failed to plan route. Please try again.");
                return;
            }

            // Check if the request was successful
            if (!response.isSuccessful()) {
                // DAO already validated landmarks and provided a user-friendly error message
                String errorMsg = response.getErrorMessage();
                if (errorMsg != null && !errorMsg.isEmpty()) {
                    presenter.presentError(errorMsg);
                } else {
                    presenter.presentError("Failed to plan route. Please try again.");
                }
                return;
            }

            // SUCCESS: Convert DAO response to output data
            List<PlanRouteOutputData.RouteStepDTO> steps = convertStepsToDTO(response.getSteps());

            PlanRouteOutputData output = new PlanRouteOutputData(
                    start,
                    destination,
                    steps,
                    response.getTotalDistanceMeters(),
                    response.getTotalDurationSeconds(),
                    null,
                    true,
                    response.isManualMode()
            );

            presenter.presentRoute(output);

        } catch (Exception e) {
            e.printStackTrace();
            presenter.presentError("An error occurred while planning the route. Please try again.");
        }
    }

    private List<PlanRouteOutputData.RouteStepDTO> convertStepsToDTO(List<RouteStep> steps) {
        List<PlanRouteOutputData.RouteStepDTO> dtos = new ArrayList<>();
        for (RouteStep step : steps) {
            dtos.add(new PlanRouteOutputData.RouteStepDTO(
                    step.getInstruction(),
                    step.getDistance(),
                    step.getDuration(),
                    step.getLandmarkName(),
                    step.isLandmark()
            ));
        }
        return dtos;
    }
}