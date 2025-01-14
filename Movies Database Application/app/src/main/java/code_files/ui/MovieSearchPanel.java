
package code_files.ui;

import java.awt.*;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.LineBorder;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import code_files.APIRequestWrapper;
import code_files.DiskManager;
import code_files.GenresConstants;
import code_files.Movie;
import code_files.SearchQuery;
import code_files.SearchResult;
import code_files.util.ImageUtils;
import code_files.util.JLabelWrappedUtils;
import java.util.ArrayList;
import java.util.List;

public class MovieSearchPanel extends JPanel {
    // private JLabel usernameLabel;
    private JLabel usernameLabel;
    private MovieLibraryApp app;
    @SuppressWarnings("rawtypes")
    private JComboBox genreFilter;
    private JPanel moviePanelContainer; // Container to hold movie panels
    private List<Movie> allMovies = new ArrayList<>(); // To store the search results

    public MovieSearchPanel(MovieLibraryApp app) {
        this.app = app;
        usernameLabel = new JLabel(app.getCurrentUser().getUserName());
        setLayout(new BorderLayout());

        // Genre filter dropdown on the right
        genreFilter = new JComboBox<>(GenresConstants.getAllGenresWithDefault());
        genreFilter.addActionListener(e -> updateMovieDisplay(getFilteredMovies()));

        // Search panel for searching movies
        JTextField searchField = new JTextField();
        JButton searchButton = new JButton("Search");
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.add(genreFilter, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        searchPanel.revalidate();
        searchPanel.repaint();
        add(searchPanel, BorderLayout.NORTH);

        // Scrollable panel to display movies
        moviePanelContainer = new JPanel();
        moviePanelContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        // and vertically
        moviePanelContainer.setPreferredSize(new Dimension(1570, 10000));
        JScrollPane scrollPane = new JScrollPane(moviePanelContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        // Right panel for username and other buttons
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        updateUsernameDisplay(app.getCurrentUser() != null ? app.getCurrentUser().getUserName() : "Guest");
        rightPanel.add(usernameLabel);

        // Buttons panel for "Watchlist", "Library", and "Recommendations"
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));

        // Watchlist button
        JButton watchlistButton = new JButton("Watchlist");
        watchlistButton.addActionListener(e -> app.switchToPanel("watchlist"));
        buttonsPanel.add(watchlistButton);

        // Library button
        JButton libraryButton = new JButton("Library");
        libraryButton.addActionListener(e -> app.switchToPanel("library"));
        buttonsPanel.add(libraryButton);

        // Recommendations button
        JButton recommendationsButton = new JButton("Recommendations");
        recommendationsButton.addActionListener(e -> app.switchToPanel("recommendations"));
        buttonsPanel.add(recommendationsButton);

        // GlobalList button
        JButton globalListButton = new JButton("GlobalList");
        globalListButton.addActionListener(e -> app.switchToPanel("globalList"));
        buttonsPanel.add(globalListButton);

        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());
        rightPanel.add(logoutButton);

        rightPanel.add(buttonsPanel); // Add the buttons panel to the right panel
        add(rightPanel, BorderLayout.EAST);

        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim();
            if (query.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Search query cannot be empty.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                // Perform the search API call
                SearchResult result = APIRequestWrapper.relatedMovies(new SearchQuery(query));
                allMovies = result != null ? result.getMovies() : List.of();
                // Update movie display with the filtered movies based on the selected genre
                updateMovieDisplay(getFilteredMovies());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "404 Not Found", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
    }

    // Logout and return to the login screen
    private void logout() {
        app.resetApplication();
    }

    // Update the movie display with a list of movies
    private void updateMovieDisplay(List<Movie> movies) {
        moviePanelContainer.removeAll(); // Clear current movie panels
        for (Movie movie : movies) {
            try {
                MoviePanel moviePanel = new MoviePanel(movie); // Create a panel for each movie
                moviePanel.setCurrentMovie(movie);
                moviePanelContainer.add(moviePanel);
            } catch (StreamWriteException e) {
                // Handle the exception, e.g., log or show an error message
                System.err.println("Error creating panel for movie: " + movie.getMovieTitle());
                e.printStackTrace();
            }
        }
        moviePanelContainer.revalidate(); // Revalidate layout after adding new components
        moviePanelContainer.repaint(); // Repaint the container to update UI
    }

    // Refresh method for LibraryPanel
    public void refreshMovieSearch() {
        updateMovieDisplay(getFilteredMovies());
    }

    private List<Movie> getFilteredMovies() {
        String selectedGenre = (String) genreFilter.getSelectedItem();
        if ("All Genres".equals(selectedGenre)) {
            return allMovies; // Return all movies if "All Genres" is selected
        }
        // Filter the movies based on the selected genre
        List<Movie> filteredMovies = new ArrayList<>();
        for (Movie movie : allMovies) {
            if (movie.getGenres().contains(selectedGenre)) {
                // If it matches the genre, add to the filtered list
                filteredMovies.add(movie);
            }
        }
        return filteredMovies;
    }

    public void updateUsernameDisplay(String username) {
        usernameLabel.setText(username != null ? "User: " + username : "Guest");
    }

    // MoviePanel class to represent each movie visually
    private class MoviePanel extends JPanel {
        private Movie currentMovie;

        public void setCurrentMovie(Movie currentMovie) {
            this.currentMovie = currentMovie;
        }

        public Movie getCurrentMovie() {
            return currentMovie;
        }

        public MoviePanel(Movie movie) throws StreamWriteException {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setPreferredSize(new Dimension(300, 290));
            setBorder(new LineBorder(Color.BLACK, 1)); // Add a black border

            // Movie title
            JLabelWrappedUtils titleWrappedLabel = new JLabelWrappedUtils();
            JLabel titleLabel = titleWrappedLabel.createWrappedLabel(movie.getMovieTitle(), 40, 40);
            titleLabel.setAlignmentX(CENTER_ALIGNMENT);
            add(titleLabel);
            add(Box.createVerticalStrut(5));

            // Movie poster
            JLabel posterLabel = new JLabel();
            ImageUtils imageUtils = new ImageUtils();
            ImageIcon posterIcon = imageUtils.createScaledImageIcon(movie.getPosterPath(), 90, 120);
            posterLabel.setIcon(posterIcon);
            posterLabel.setAlignmentX(CENTER_ALIGNMENT);
            add(posterLabel);
            add(Box.createVerticalStrut(5));

            // Movie rating and votes
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
             JLabel ratingLabel = new JLabel("Rating: " + DiskManager.getEntireMovieObjectFromDisk(movie.getMovieID()).getRating() + "/10"); // Display movie rating
            JLabel votesLabel = new JLabel("Votes: " + DiskManager.getEntireMovieObjectFromDisk(movie.getMovieID()).getCountVotes()); // Display vote count
            infoPanel.add(ratingLabel);
            infoPanel.add(votesLabel);
            infoPanel.setAlignmentX(CENTER_ALIGNMENT);
            add(infoPanel);

            // Buttons for add to library, watchlist and view details
            JPanel buttonPanel = new JPanel();
            JButton addToLibraryButton = new JButton("Add to Library");
            JButton addToWatchListButton = new JButton("Add to Watchlist");
            JButton viewDetailsButton = new JButton("View Details");
            buttonPanel.add(addToLibraryButton);
            buttonPanel.add(addToWatchListButton);
            buttonPanel.add(viewDetailsButton);
            add(Box.createVerticalStrut(5));
            add(buttonPanel);

            // Add movie to library button action
            addToLibraryButton.addActionListener(e -> {
                try {
                    boolean added = app.getCurrentUser().addMovieToLibrary(movie);
                    if (added) {
                        JOptionPane.showMessageDialog(this, "Movie added to your library!");
                    } else {
                        JOptionPane.showMessageDialog(this, "This movie is already in your library.");
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this,
                            "An error occurred while adding the movie to the library: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });

            // Add movie to watchlist button action
            addToWatchListButton.addActionListener(e -> {
                try {
                    boolean added = app.getCurrentUser().addMovieToWatchlist(movie);
                    if (added) {
                        JOptionPane.showMessageDialog(this, "Movie added to your watchlist!");
                    } else {
                        JOptionPane.showMessageDialog(this, "This movie is already in your watchlist.");
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this,
                            "An error occurred while adding the movie to the watchlist: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });

            // View movie details button action
            viewDetailsButton.addActionListener(e -> {
                // Get the current movie associated with this panel
                Movie selectedMovie = getCurrentMovie(); // This will return the movie associated with this panel

                if (selectedMovie != null) {
                    // Set the selected movie as the current movie in the app
                    app.setCurrentMovie(selectedMovie); // Set the current movie to the selected one
                    // Switch to the movie details panel
                    app.switchToPanel("details");
                } else {
                    // If no movie is found, display an error message
                    JOptionPane.showMessageDialog(null, "Movie not found!");
                }
            });
        }
    }
}