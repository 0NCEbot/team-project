package interface_adapter.viewhistory;

import interface_adapter.ViewModel;

/**
 * View Model for the View History screen.
 * Extends the base ViewModel to provide type-safe state management
 * for the visit history view.
 * This class is part of the Interface Adapter layer and manages
 * the presentation state for the view.
 */
public class ViewHistoryViewModel extends ViewModel<ViewHistoryState> {

    /**
     * Constructs the View History view model.
     * Initializes with an empty state and registers the view name
     * for navigation purposes.
     */
    public ViewHistoryViewModel() {
        super("view history");
        setState(new ViewHistoryState());
    }
}
