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

            if (response == null || !response.isSuccessful()) {
                handleManualMode(start, destination, intermediates);
                return;
            }

            // PROCESS: Convert DAO response to output data
            List<PlanRouteOutputData.RouteStepDTO> steps =
                    convertStepsToDTO(response.getSteps());

            PlanRouteOutputData output = new PlanRouteOutputData(
                    start,
                    destination,
                    steps,
                    response.getTotalDistanceMeters(),
                    response.getTotalDurationSeconds(),
                    null,
                    true,
                    false
            );

            presenter.presentRoute(output);

        } catch (Exception e) {
            e.printStackTrace();
            handleManualMode(start, destination, intermediates);
        }
    }

    private void handleManualMode(String start, String destination, String[] intermediates) {
        List<PlanRouteOutputData.RouteStepDTO> manualSteps = new ArrayList<>();

        manualSteps.add(new PlanRouteOutputData.RouteStepDTO(
                "Start at: " + start,
                0, 0, null
        ));

        for (String intermediate : intermediates) {
            manualSteps.add(new PlanRouteOutputData.RouteStepDTO(
                    "Visit: " + intermediate,
                    0, 0, null
            ));
        }

        manualSteps.add(new PlanRouteOutputData.RouteStepDTO(
                "End at: " + destination,
                0, 0, null
        ));

        PlanRouteOutputData output = new PlanRouteOutputData(
                start,
                destination,
                manualSteps,
                0, 0,
                "API unavailable. Using self-guided mode.",
                true,
                true
        );

        presenter.presentRoute(output);
    }

    private List<PlanRouteOutputData.RouteStepDTO> convertStepsToDTO(List<RouteStep> steps) {
        List<PlanRouteOutputData.RouteStepDTO> dtos = new ArrayList<>();
        for (RouteStep step : steps) {
            dtos.add(new PlanRouteOutputData.RouteStepDTO(
                    step.getInstruction(),
                    step.getDistance(),
                    step.getDuration(),
                    null
            ));
        }
        return dtos;
    }
}