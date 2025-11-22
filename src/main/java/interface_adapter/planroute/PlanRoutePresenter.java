package interface_adapter.planroute;

import interface_adapter.ViewManagerModel;
import use_case.planroute.PlanRouteOutputBoundary;
import use_case.planroute.PlanRouteOutputData;

import java.util.ArrayList;
import java.util.List;

public class PlanRoutePresenter implements PlanRouteOutputBoundary {

    private final PlanRouteViewModel viewModel;
    private final ViewManagerModel viewManagerModel;

    public PlanRoutePresenter(PlanRouteViewModel viewModel,
                              ViewManagerModel viewManagerModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void presentRoute(PlanRouteOutputData outputData) {
        PlanRouteState state = new PlanRouteState();
        state.setStartLocation(outputData.getStartLocation());
        state.setDestination(outputData.getDestination());
        state.setManualMode(outputData.isManualMode());

        List<PlanRouteState.StepVM> stepVMs = new ArrayList<>();
        for (PlanRouteOutputData.RouteStepDTO dto : outputData.getSteps()) {
            PlanRouteState.StepVM vm = new PlanRouteState.StepVM();
            vm.instruction = dto.instruction;
            vm.distance = formatDistance(dto.distanceMeters);
            vm.duration = formatDuration(dto.durationSeconds);
            vm.landmarkNearby = dto.landmarkNearby;
            vm.completed = false;
            stepVMs.add(vm);
        }
        state.setSteps(stepVMs);

        state.setTotalDistance(formatDistance(outputData.getTotalDistanceMeters()));
        state.setTotalDuration(formatDuration(outputData.getTotalDurationSeconds()));

        viewModel.setState(state);
        viewModel.firePropertyChange();

        viewManagerModel.setState("plan a route");
        viewManagerModel.firePropertyChange();
    }

    @Override
    public void presentError(String errorMessage) {
        PlanRouteState state = viewModel.getState();
        state.setErrorMessage(errorMessage);
        viewModel.firePropertyChange();
    }

    private String formatDistance(int meters) {
        if (meters < 1000) {
            return meters + " m";
        }
        double km = meters / 1000.0;
        return String.format("%.2f km", km);
    }

    private String formatDuration(int seconds) {
        if (seconds < 60) {
            return seconds + " sec";
        }
        int minutes = seconds / 60;
        if (minutes < 60) {
            return minutes + " min";
        }
        int hours = minutes / 60;
        int mins = minutes % 60;
        return hours + "h " + mins + "m";
    }
}