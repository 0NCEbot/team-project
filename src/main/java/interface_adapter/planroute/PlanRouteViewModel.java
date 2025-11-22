package interface_adapter.planroute;

import interface_adapter.ViewModel;

public class PlanRouteViewModel extends ViewModel<PlanRouteState> {
    public PlanRouteViewModel() {
        super("plan a route");
        setState(new PlanRouteState());
    }
}