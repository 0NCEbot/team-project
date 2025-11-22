package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.selectedplace.SelectedPlaceController;
import interface_adapter.selectedplace.SelectedPlaceState;
import interface_adapter.selectedplace.SelectedPlaceViewModel;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.charset.StandardCharsets;

public class SelectedPlaceView extends JPanel implements PropertyChangeListener {

    private final String viewName = "selected place";
    private final SelectedPlaceViewModel viewModel;
    private final ViewManagerModel viewManagerModel;

    private SelectedPlaceController controller;

    private JLabel usernameLabel;
    private JLabel landmarkNameLabel;
    private JTextArea descriptionArea;
    private JLabel addressLabel;
    private JLabel hoursLabel;
    private JLabel imageLabel;

    private JButton checkInButton;

    // ====== GOOGLE PLACES CONFIG ======
    private static final String PLACES_API_KEY = "AIzaSyCk9bPskLw7eUI-_Y9G6tW8eDAE-iXI8Ms";
    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient httpClient = new OkHttpClient();

    // keep a reference to the current worker so we can cancel if needed
    private SwingWorker<ImageIcon, Void> currentPhotoWorker;

    public SelectedPlaceView(SelectedPlaceViewModel viewModel,
                             ViewManagerModel viewManagerModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;

        this.viewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // === TOP BAR ===
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        userPanel.setOpaque(false);
        usernameLabel = new JLabel("Username1!");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameLabel.setForeground(new Color(0, 102, 204));
        userPanel.add(usernameLabel);

        JLabel logoutLabel = new JLabel("Logout");
        logoutLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        logoutLabel.setForeground(new Color(0, 102, 204));
        logoutLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        topBar.add(userPanel, BorderLayout.WEST);
        topBar.add(logoutLabel, BorderLayout.EAST);

        // === BOTTOM BAR ===
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomBar.setBackground(Color.WHITE);
        bottomBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton backButton = new JButton("back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.setForeground(new Color(0, 102, 204));
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> {
            viewManagerModel.setState("browse landmarks");
            viewManagerModel.firePropertyChange();
        });

        bottomBar.add(backButton);

        // === CENTER: left buttons + right content ===
        JPanel centerPanel = new JPanel(new GridLayout(1, 2));
        centerPanel.setBackground(Color.WHITE);

        // LEFT SIDE — centered buttons
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        checkInButton = createBigButton("Check In");
        JButton notesButton = createBigButton("Notes");

        checkInButton.addActionListener(e -> {
            if (!checkInButton.isEnabled()) {
                return;
            }
            if (controller != null) {
                controller.checkIn();
            }
            checkInButton.setBackground(new Color(0, 180, 0));
            checkInButton.setForeground(Color.WHITE);
            checkInButton.setBorder(BorderFactory.createLineBorder(new Color(0, 140, 0), 2));
            checkInButton.setEnabled(false);
        });

        notesButton.addActionListener(e -> {
            if (controller != null) controller.notes();
        });
        checkInButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        notesButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(checkInButton);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        leftPanel.add(notesButton);
        leftPanel.add(Box.createVerticalGlue());

        // RIGHT SIDE — landmark info section
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        // add right margin so it doesn't touch scrollbar
        rightPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 40));

        imageLabel = new JLabel();
        // preferred size so the scroll pane/BoxLayout respects it
        imageLabel.setPreferredSize(new Dimension(350, 220));
        imageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        // start with placeholder
        setPlaceholderImage();

        landmarkNameLabel = new JLabel("Landmark Name");
        landmarkNameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        landmarkNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        descriptionArea = new JTextArea();
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionArea.setOpaque(false);
        descriptionArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        addressLabel = new JLabel();
        addressLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        addressLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        hoursLabel = new JLabel();
        hoursLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        hoursLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        rightPanel.add(imageLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(landmarkNameLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(descriptionArea);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(addressLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(hoursLabel);

        centerPanel.add(leftPanel);
        JScrollPane scroll = new JScrollPane(
                rightPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        scroll.getVerticalScrollBar().setUnitIncrement(16); // smoother scrolling
        // optional outer padding instead of default border
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));

        centerPanel.add(scroll);

        add(topBar, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomBar, BorderLayout.SOUTH);
    }

    public void setSelectedPlaceController(SelectedPlaceController controller) {
        this.controller = controller;
    }

    private JButton createBigButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 18));
        btn.setPreferredSize(new Dimension(200, 70));
        btn.setMaximumSize(new Dimension(200, 70));
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(0, 102, 204));
        btn.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 2));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!"state".equals(evt.getPropertyName())) return;

        SelectedPlaceState state = (SelectedPlaceState) evt.getNewValue();
        usernameLabel.setText(state.getUsername());
        landmarkNameLabel.setText(state.getLandmarkName());
        descriptionArea.setText(state.getDescription());
        addressLabel.setText(state.getAddress());

        // ---- format hours on multiple lines ----
        String openHours = state.getOpenHours();
        if (openHours == null || openHours.isBlank() || "No hours available".equals(openHours)) {
            hoursLabel.setText("Hours: No hours available");
        } else {
            // Convert "\n" to "<br>" and wrap in HTML so JLabel shows line breaks
            String htmlHours =
                    "<html>Hours:<br>" + openHours.replace("\n", "<br>") + "</html>";
            hoursLabel.setText(htmlHours);
        }
        // ----------------------------------------

        // reset Check In button appearance
        if (checkInButton != null) {
            checkInButton.setEnabled(true);
            checkInButton.setBackground(Color.WHITE);
            checkInButton.setForeground(new Color(0, 102, 204));
            checkInButton.setBorder(
                    BorderFactory.createLineBorder(new Color(0, 102, 204), 2)
            );
        }

        // load photo for this landmark name
        loadPhotoForLandmarkAsync(state.getLandmarkName());
    }


    public String getViewName() {
        return viewName;
    }

    // ================== PHOTO LOADING ==================

    private void setPlaceholderImage() {
        ImageIcon img = new ImageIcon("src/main/resources/placeholder_landmark.jpg");
        Image scaled = img.getImage().getScaledInstance(350, 220, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(scaled));
    }

    private void loadPhotoForLandmarkAsync(String landmarkName) {
        if (landmarkName == null || landmarkName.isBlank()) {
            setPlaceholderImage();
            return;
        }

        // cancel previous worker if still running
        if (currentPhotoWorker != null && !currentPhotoWorker.isDone()) {
            currentPhotoWorker.cancel(true);
        }

        currentPhotoWorker = new SwingWorker<>() {
            @Override
            protected ImageIcon doInBackground() throws Exception {
                try {
                    String photoName = findPhotoNameForPlace(landmarkName);
                    if (photoName == null) {
                        return null;
                    }
                    return fetchPhotoIcon(photoName);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void done() {
                if (isCancelled()) return;
                try {
                    ImageIcon icon = get();
                    if (icon != null) {
                        imageLabel.setIcon(icon);
                    } else {
                        setPlaceholderImage();
                    }
                    revalidate();
                    repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                    setPlaceholderImage();
                }
            }
        };

        currentPhotoWorker.execute();
    }

    /**
     * Uses Places Text Search (New) to find a place and return first photo name.
     */
    private String findPhotoNameForPlace(String textQuery) throws Exception {
        // POST https://places.googleapis.com/v1/places:searchText
        String url = "https://places.googleapis.com/v1/places:searchText";

        JSONObject bodyJson = new JSONObject();
        bodyJson.put("textQuery", textQuery);
        bodyJson.put("maxResultCount", 1);

        RequestBody body = RequestBody.create(bodyJson.toString(), JSON);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-Goog-Api-Key", PLACES_API_KEY)
                .addHeader("X-Goog-FieldMask", "places.photos") // only need photos
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                return null;
            }
            String resp = response.body().string();
            JSONObject json = new JSONObject(resp);
            JSONArray places = json.optJSONArray("places");
            if (places == null || places.length() == 0) {
                return null;
            }
            JSONObject place = places.getJSONObject(0);
            JSONArray photos = place.optJSONArray("photos");
            if (photos == null || photos.length() == 0) {
                return null;
            }
            JSONObject photo = photos.getJSONObject(0);
            return photo.optString("name", null); // e.g. "places/PLACE_ID/photos/PHOTO_RESOURCE"
        }
    }

    /**
     * Uses Place Photos (New) to download the actual image for a given photo name.
     */
    private ImageIcon fetchPhotoIcon(String photoName) throws Exception {
        // GET https://places.googleapis.com/v1/NAME/media?maxHeightPx=300&maxWidthPx=600&key=API_KEY
        String url = "https://places.googleapis.com/v1/"
                + photoName
                + "/media?maxHeightPx=300&maxWidthPx=600&key="
                + PLACES_API_KEY;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                return null;
            }
            byte[] bytes = response.body().bytes();
            ImageIcon icon = new ImageIcon(bytes);
            Image scaled = icon.getImage().getScaledInstance(350, 220, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        }
    }
}