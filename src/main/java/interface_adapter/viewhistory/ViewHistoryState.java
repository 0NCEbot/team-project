package interface_adapter.viewhistory;

import java.util.ArrayList;
import java.util.List;

/**
 * State object for the View History View.
 * Holds all the data needed to render the visit history screen.
 * This class follows the State pattern and is part of the Interface Adapter layer,
 * converting use case output into a format suitable for the view.
 */
public class ViewHistoryState {

    /**
     * View Model representation of a single visit.
     * Contains only the data needed for display in the UI.
     */
    public static class VisitVM {
        public String visitId;
        public String landmarkName;
        public String visitedAt;

        /**
         * Default constructor for creating empty visit view models.
         */
        public VisitVM() {
        }

        /**
         * Constructs a visit view model with all fields.
         *
         * @param visitId unique identifier for the visit
         * @param landmarkName name of the visited landmark
         * @param visitedAt formatted timestamp of the visit
         */
        public VisitVM(String visitId, String landmarkName, String visitedAt) {
            this.visitId = visitId;
            this.landmarkName = landmarkName;
            this.visitedAt = visitedAt;
        }
    }

    private String username = "";
    private List<VisitVM> visits = new ArrayList<>();
    private String errorMessage = null;
    private String successMessage = null;

    /**
     * Default constructor.
     */
    public ViewHistoryState() {
    }

    /**
     * Copy constructor for creating a new state from an existing one.
     * Useful for immutability patterns in state management.
     *
     * @param copy the state to copy from
     */
    public ViewHistoryState(ViewHistoryState copy) {
        this.username = copy.username;
        this.errorMessage = copy.errorMessage;
        this.successMessage = copy.successMessage;

        this.visits = new ArrayList<>();
        if (copy.visits != null) {
            // Deep copy the visits list
            for (VisitVM vm : copy.visits) {
                VisitVM newVm = new VisitVM();
                newVm.visitId = vm.visitId;
                newVm.landmarkName = vm.landmarkName;
                newVm.visitedAt = vm.visitedAt;
                this.visits.add(newVm);
            }
        }
    }

    // Getters and setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<VisitVM> getVisits() {
        return visits;
    }

    public void setVisits(List<VisitVM> visits) {
        this.visits = visits;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }
}
