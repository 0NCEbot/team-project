package interface_adapter.viewprogress;

import interface_adapter.ViewModel;

/**
 * View Model for the View Progress screen.
 * Extends the base ViewModel to provide type-safe state management
 * for the progress view.
 * This class is part of the Interface Adapter layer and manages
 * the presentation state for the view.
 */
public class ViewProgressViewModel extends ViewModel<ViewProgressState> {

    /**
     * Constructs the View Progress view model.
     * Initializes with an empty state and registers the view name
     * for navigation purposes.
     */
    public ViewProgressViewModel() {
        super("my progress");
        setState(new ViewProgressState());
    }
}
