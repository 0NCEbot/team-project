package use_case.homescreen;

public class HomescreenInteractor implements HomescreenInputBoundary {
    private final HomescreenOutputBoundary presenter;

    public HomescreenInteractor(HomescreenOutputBoundary presenter) {
        this.presenter = presenter;
    }

    @Override
    public void execute(HomescreenInputData inputData) {
        String action = inputData.getAction();

        //just logs the action - no navigation yet
        System.out.println("Action received: " + action);

        // Map actions to target views
        String targetView = "";
        switch (action) {
            case "browse landmarks":
                targetView = "browse landmarks";
                break;
            case "plan a route":
                targetView = "plan a route";
                break;
            case "my progress":
                targetView = "my progress";
                break;
            default:
                presenter.prepareFailView("Unknown action: " + action);
                return;
        }

        HomescreenOutputData outputData = new HomescreenOutputData(targetView, true);
        presenter.prepareSuccessView(outputData);
    }
}