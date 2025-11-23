package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.viewhistory.ViewHistoryController;
import interface_adapter.viewhistory.ViewHistoryState;
import interface_adapter.viewhistory.ViewHistoryViewModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * View for displaying visit history.
 * This class is part of the Frameworks & Drivers layer and handles
 * all UI rendering and user interactions for the visit history screen.
 *
 * The view follows the Observer pattern by listening to ViewModel changes
 * and updating the UI accordingly. It maintains separation from business
 * logic by delegating all actions to the controller.
 */
public class ViewHistoryView extends JPanel implements PropertyChangeListener {

    private final String viewName = "view history";
    private final ViewHistoryViewModel viewModel;
    private final ViewManagerModel viewManagerModel;

    private ViewHistoryController controller;

    // UI Components
    private JLabel usernameLabel;
    private JLabel titleLabel;
    private DefaultListModel<String> visitListModel;
    private JList<String> visitList;
    private JScrollPane scrollPane;
    private JButton undoButton;
    private JLabel messageLabel;
    private JLabel emptyStateLabel;
    private JPanel emptyStatePanel;

    /**
     * Constructs the View History view.
     * Sets up all UI components and registers as a property change listener.
     *
     * @param viewModel the view model containing state for this view
     * @param viewManagerModel the view manager for navigation
     */
    public ViewHistoryView(ViewHistoryViewModel viewModel,
                           ViewManagerModel viewManagerModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;

        this.viewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Build UI components
        add(createTopBar(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createBottomBar(), BorderLayout.SOUTH);
    }

    /**
     * Creates the top bar with username and logout link.
     *
     * @return the top bar panel
     */
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Username display
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        userPanel.setOpaque(false);
        usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameLabel.setForeground(new Color(0, 102, 204));
        userPanel.add(usernameLabel);

        // Logout link
        JLabel logoutLabel = new JLabel("Logout");
        logoutLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        logoutLabel.setForeground(new Color(0, 102, 204));
        logoutLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                navigateToLogin();
            }
        });

        topBar.add(userPanel, BorderLayout.WEST);
        topBar.add(logoutLabel, BorderLayout.EAST);

        return topBar;
    }

    /**
     * Creates the center panel containing the visit history list.
     *
     * @return the center panel
     */
    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        // Title
        titleLabel = new JLabel("Visit History");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Empty state message panel (centered)
        emptyStatePanel = new JPanel();
        emptyStatePanel.setLayout(new BoxLayout(emptyStatePanel, BoxLayout.Y_AXIS));
        emptyStatePanel.setBackground(Color.WHITE);
        emptyStatePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        emptyStateLabel = new JLabel("<html><div style='text-align: center;'>"
                + "You haven't visited any landmarks!<br/>"
                + "Visit a landmark and check in for the first time!"
                + "</div></html>");
        emptyStateLabel.setFont(new Font("Arial", Font.BOLD, 24));
        emptyStateLabel.setForeground(new Color(100, 100, 100));
        emptyStateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyStateLabel.setHorizontalAlignment(SwingConstants.CENTER);

        emptyStatePanel.add(Box.createVerticalGlue());
        emptyStatePanel.add(emptyStateLabel);
        emptyStatePanel.add(Box.createVerticalGlue());
        emptyStatePanel.setVisible(false);

        // Visit list
        visitListModel = new DefaultListModel<>();
        visitList = new JList<>(visitListModel);
        visitList.setFont(new Font("Arial", Font.PLAIN, 14));
        visitList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        visitList.setFixedCellHeight(40);

        scrollPane = new JScrollPane(visitList);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        scrollPane.setMaximumSize(new Dimension(800, 500));

        // Undo button
        undoButton = new JButton("Undo Visit");
        undoButton.setFont(new Font("Arial", Font.PLAIN, 16));
        undoButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        undoButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        undoButton.setPreferredSize(new Dimension(150, 40));
        undoButton.setMaximumSize(new Dimension(150, 40));
        undoButton.addActionListener(e -> handleUndoVisit());

        // Message label for feedback
        messageLabel = new JLabel("");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add components to center panel
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        centerPanel.add(emptyStatePanel);
        centerPanel.add(scrollPane);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(undoButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(messageLabel);

        return centerPanel;
    }

    /**
     * Creates the bottom bar with back button.
     *
     * @return the bottom bar panel
     */
    private JPanel createBottomBar() {
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomBar.setBackground(Color.WHITE);
        bottomBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton backButton = new JButton("back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.setForeground(new Color(0, 102, 204));
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> navigateToHomescreen());

        bottomBar.add(backButton);

        return bottomBar;
    }

    /**
     * Handles the undo visit action.
     * Prompts user for confirmation before removing the selected visit.
     */
    private void handleUndoVisit() {
        int selectedIndex = visitList.getSelectedIndex();

        if (selectedIndex == -1) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Please select a visit to remove.");
            return;
        }

        // Get the selected visit
        ViewHistoryState state = viewModel.getState();
        if (selectedIndex >= state.getVisits().size()) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Invalid selection.");
            return;
        }

        ViewHistoryState.VisitVM selectedVisit = state.getVisits().get(selectedIndex);

        // Confirm with user
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to remove this visit?",
                "Confirm Undo Visit",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION && controller != null) {
            controller.undoVisit(state.getUsername(), selectedVisit.visitId);
        }
    }

    /**
     * Navigates back to the my progress screen.
     */
    private void navigateToHomescreen() {
        viewManagerModel.setState("my progress");
        viewManagerModel.firePropertyChange();
    }

    /**
     * Navigates to the login screen.
     */
    private void navigateToLogin() {
        viewManagerModel.setState("log in");
        viewManagerModel.firePropertyChange();
    }

    /**
     * Sets the controller for this view.
     *
     * @param controller the view history controller
     */
    public void setController(ViewHistoryController controller) {
        this.controller = controller;
    }

    /**
     * Gets the view name for navigation purposes.
     *
     * @return the view name
     */
    public String getViewName() {
        return viewName;
    }

    /**
     * Responds to property change events from the view model.
     * Updates the UI to reflect the current state.
     *
     * @param evt the property change event
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!"state".equals(evt.getPropertyName())) {
            return;
        }

        ViewHistoryState state = (ViewHistoryState) evt.getNewValue();

        // Update username
        usernameLabel.setText(state.getUsername());

        // Update visit list
        visitListModel.clear();
        if (state.getVisits() != null && !state.getVisits().isEmpty()) {
            for (ViewHistoryState.VisitVM visit : state.getVisits()) {
                // Format: "LandmarkName - Date Time"
                String displayText = visit.landmarkName + " - " + visit.visitedAt;
                visitListModel.addElement(displayText);
            }
            // Show visit list and undo button
            emptyStatePanel.setVisible(false);
            scrollPane.setVisible(true);
            undoButton.setVisible(true);
        } else {
            // Show empty state, hide visit list and undo button
            emptyStatePanel.setVisible(true);
            scrollPane.setVisible(false);
            undoButton.setVisible(false);
        }

        // Update messages
        if (state.getErrorMessage() != null) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText(state.getErrorMessage());
        } else if (state.getSuccessMessage() != null) {
            messageLabel.setForeground(new Color(0, 128, 0));
            messageLabel.setText(state.getSuccessMessage());
        } else {
            messageLabel.setText("");
        }

        revalidate();
        repaint();
    }
}
