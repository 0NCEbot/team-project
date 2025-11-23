package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.viewhistory.ViewHistoryController;
import interface_adapter.viewprogress.ViewProgressController;
import interface_adapter.viewprogress.ViewProgressState;
import interface_adapter.viewprogress.ViewProgressViewModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * View for displaying user progress summary.
 * This class is part of the Frameworks & Drivers layer and handles
 * all UI rendering and user interactions for the progress screen.
 *
 * The view follows the Observer pattern by listening to ViewModel changes
 * and updating the UI accordingly. It maintains separation from business
 * logic by delegating all actions to controllers.
 */
public class ViewProgressView extends JPanel implements PropertyChangeListener {

    private final String viewName = "my progress";
    private final ViewProgressViewModel viewModel;
    private final ViewManagerModel viewManagerModel;

    private ViewProgressController controller;
    private ViewHistoryController viewHistoryController;

    // UI Components
    private JLabel usernameLabel;
    private JLabel titleLabel;
    private JLabel statsLabel;
    private JLabel totalVisitsLabel;
    private JLabel completionLabel;
    private JLabel lastVisitLabel;
    private JLabel emptyStateLabel;
    private JProgressBar progressBar;
    private JButton viewHistoryButton;

    /**
     * Constructs the View Progress view.
     * Sets up all UI components and registers as a property change listener.
     *
     * @param viewModel the view model containing state for this view
     * @param viewManagerModel the view manager for navigation
     */
    public ViewProgressView(ViewProgressViewModel viewModel,
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
     * Creates the center panel containing progress statistics.
     *
     * @return the center panel
     */
    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        // Title
        titleLabel = new JLabel("My Progress");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Empty state message (shown when no visits)
        emptyStateLabel = new JLabel("You haven't visited any landmarks yet! Start exploring!");
        emptyStateLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        emptyStateLabel.setForeground(Color.GRAY);
        emptyStateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        emptyStateLabel.setVisible(false);

        // Statistics panel
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        statsPanel.setMaximumSize(new Dimension(600, 250));

        // Landmarks visited count
        statsLabel = new JLabel("Total Landmarks Visited: 0/31");
        statsLabel.setFont(new Font("Arial", Font.BOLD, 20));
        statsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Total visits count
        totalVisitsLabel = new JLabel("Total Number of Visits: 0");
        totalVisitsLabel.setFont(new Font("Arial", Font.BOLD, 20));
        totalVisitsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Completion percentage
        completionLabel = new JLabel("Completion: 0.0%");
        completionLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        completionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("Arial", Font.PLAIN, 14));
        progressBar.setForeground(new Color(0, 150, 0));
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        progressBar.setMaximumSize(new Dimension(550, 30));
        progressBar.setPreferredSize(new Dimension(550, 30));

        // Last visited label
        lastVisitLabel = new JLabel("");
        lastVisitLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        lastVisitLabel.setForeground(Color.GRAY);
        lastVisitLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        statsPanel.add(statsLabel);
        statsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        statsPanel.add(totalVisitsLabel);
        statsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        statsPanel.add(completionLabel);
        statsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        statsPanel.add(progressBar);
        statsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        statsPanel.add(lastVisitLabel);

        // View History button
        viewHistoryButton = new JButton("View History");
        viewHistoryButton.setFont(new Font("Arial", Font.BOLD, 18));
        viewHistoryButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        viewHistoryButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        viewHistoryButton.setPreferredSize(new Dimension(300, 50));
        viewHistoryButton.setMaximumSize(new Dimension(300, 50));
        viewHistoryButton.setBackground(new Color(65, 105, 225));  // Royal blue - more prominent
        viewHistoryButton.setForeground(Color.WHITE);
        viewHistoryButton.setFocusPainted(false);
        viewHistoryButton.setBorderPainted(false);
        viewHistoryButton.setOpaque(true);
        viewHistoryButton.addActionListener(e -> navigateToViewHistory());

        // Add components to center panel
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(emptyStateLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(statsPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        centerPanel.add(viewHistoryButton);
        centerPanel.add(Box.createVerticalGlue());  // Push content up

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
     * Navigates to the View History screen.
     * Uses the ViewHistoryController to load history data for the current user.
     */
    private void navigateToViewHistory() {
        if (viewHistoryController != null) {
            String username = viewModel.getState().getUsername();
            viewHistoryController.execute(username);
        }
    }

    /**
     * Navigates back to the homescreen.
     */
    private void navigateToHomescreen() {
        viewManagerModel.setState("homescreen");
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
     * @param controller the view progress controller
     */
    public void setController(ViewProgressController controller) {
        this.controller = controller;
    }

    /**
     * Sets the view history controller for navigation.
     * This is needed to navigate from progress view to history view.
     *
     * @param viewHistoryController the view history controller
     */
    public void setViewHistoryController(ViewHistoryController viewHistoryController) {
        this.viewHistoryController = viewHistoryController;
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

        ViewProgressState state = (ViewProgressState) evt.getNewValue();

        // Update username
        usernameLabel.setText(state.getUsername());

        // Update statistics
        if (state.hasVisits()) {
            // Show statistics
            emptyStateLabel.setVisible(false);

            statsLabel.setText(String.format("Total Landmarks Visited: %d/%d",
                    state.getVisitedCount(), state.getTotalLandmarks()));

            totalVisitsLabel.setText(String.format("Total Number of Visits: %d",
                    state.getTotalVisits()));

            completionLabel.setText("Completion: " + state.getCompletionPercent());

            // Update progress bar
            double completion = Double.parseDouble(
                    state.getCompletionPercent()
                        .replace("%", "")
                        .replace(",", ".")
                );
            progressBar.setValue((int) completion);

            // Update last visit label
            if (state.getLastVisitedAt() != null) {
                lastVisitLabel.setText("Last visit: " + state.getLastVisitedAt());
            } else {
                lastVisitLabel.setText("");
            }
        } else {
            // Show empty state
            emptyStateLabel.setVisible(true);
            statsLabel.setText("Total Landmarks Visited: 0/" + state.getTotalLandmarks());
            totalVisitsLabel.setText("Total Number of Visits: 0");
            completionLabel.setText("Completion: 0.0%");
            progressBar.setValue(0);
            lastVisitLabel.setText("");
        }

        // Show error if any
        if (state.getErrorMessage() != null) {
            JOptionPane.showMessageDialog(this,
                    state.getErrorMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        revalidate();
        repaint();
    }
}
