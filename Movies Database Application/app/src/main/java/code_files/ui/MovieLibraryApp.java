package code_files.ui;

import javax.swing.*;
import code_files.Movie;
import code_files.User;
import java.awt.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MovieLibraryApp extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private User currentUser;
    private Movie currentMovie;
    private Map<String, JPanel> panelMap; // Store all panels in a map for quick access

    public MovieLibraryApp() {
        setTitle("Movie Library App");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize CardLayout and main panel
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        panelMap = new HashMap<>(); // Initialize the panel map

        // Add the login panel
        LoginPanel loginPanel = new LoginPanel(this);
        addPanel("login", loginPanel);

        // Set the layout of the JFrame and add the main panel
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        // Show the login panel initially
        cardLayout.show(mainPanel, "login");
    }

    // Method to add a panel to the map and CardLayout
    private void addPanel(String name, JPanel panel) {
        panelMap.put(name, panel); // Store the panel in the map
        mainPanel.add(panel, name); // Add the panel to CardLayout
    }

    // Method to check if a panel has already been added
    private boolean isPanelAdded(String panelName) {
        return panelMap.containsKey(panelName);
    }

    // Switch to a specific panel by name
    public void switchToPanel(String panelName) {
        if (!isPanelAdded(panelName)) {
            // Add the panel dynamically if it does not already exist
            switch (panelName) {
                case "search":
                    addPanel("search", new MovieSearchPanel(this));
                    break;
                case "library":
                    addPanel("library", new LibraryPanel(this));
                    break;
                case "recommendations":
                    addPanel("recommendations", new RecommendationsPanel(this));
                    break;
                case "watchlist":
                    addPanel("watchlist", new WatchListPanel(this));
                    break;
                case "details":
                    addPanel("details", new MovieDetailsPanel(this, currentMovie));
                    break;
                case "globalList":
                    addPanel("globalList", new GlobalListPanel(this));
                    break;
                default:
                    throw new IllegalArgumentException("Unknown panel: " + panelName);
            }
        } else {
            // Update the panel dynamically if it does already exist
            switch (panelName) {
                case "search":
                    MovieSearchPanel movieSearchPanel = (MovieSearchPanel) getPanel("search");
                    movieSearchPanel.refreshMovieSearch();
                    break;
                case "library":
                    LibraryPanel libraryPanel = (LibraryPanel) getPanel("library");
                    libraryPanel.refreshLibrary();
                    break;
                case "recommendations":
                    RecommendationsPanel recommendationsPanel = (RecommendationsPanel) getPanel("recommendations");
                    recommendationsPanel.refreshRecommendations();
                    break;
                case "watchlist":
                    WatchListPanel watchListPanel = (WatchListPanel) getPanel("watchlist");
                    watchListPanel.refreshWatchList();
                    break;
                case "details":
                    MovieDetailsPanel movieDetailsPanel = (MovieDetailsPanel) getPanel("details");
                    movieDetailsPanel.refreshDetails();
                    break;
                case "globalList":
                    GlobalListPanel globalListPanel = (GlobalListPanel) getPanel("globalList");
                    globalListPanel.refreshGlobal();
                    break;
                default:
                    throw new IllegalArgumentException("Unknown panel: " + panelName);
            }

        }
        // Show the requested panel
        cardLayout.show(mainPanel, panelName);
    }

    public void resetApplication() {
        // Reset user state and movie information when logging out or switching users
        setCurrentUser(null); // Clear current user
        setCurrentMovie(null); // Clear current movie

        // Remove all panels from the main panel to reset the UI
        mainPanel.removeAll();
        mainPanel.revalidate();
        mainPanel.repaint();

        // Clear the panel map to remove all previously added panels
        panelMap.clear(); // This will remove all entries in the map

        // Add the login panel again after reset
        LoginPanel loginPanel = new LoginPanel(this);
        addPanel("login", loginPanel);

        // Switch to the login panel
        cardLayout.show(mainPanel, "login");
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        // After user login, reset the current movie to ensure no old movie data is shown
        setCurrentMovie(null);
    }

    public void setCurrentMovie(Movie currentMovie) {
        this.currentMovie = currentMovie;
        // When a movie is selected or updated, dynamically update the movie details panel
        if (currentMovie != null) {
            MovieDetailsPanel detailsPanel = (MovieDetailsPanel) getPanel("details");
            if (detailsPanel != null) {
                detailsPanel.updateMovieDetails(currentMovie); // Update the movie details display
            }
        }
    }

    public JPanel getPanel(String panelName) {
        return panelMap.get(panelName);
    }

    // Get the current user
    public User getCurrentUser() {
        return currentUser;
    }

    public Movie getCurrentMovie() {
        return currentMovie;
    }

    // Main method to start the application
    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);
        SwingUtilities.invokeLater(() -> {
            MovieLibraryApp app = new MovieLibraryApp();
            app.setVisible(true);
        });
    }
}
