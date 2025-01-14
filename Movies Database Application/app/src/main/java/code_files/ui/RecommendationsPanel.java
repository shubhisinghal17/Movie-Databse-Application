package code_files.ui;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import code_files.DiskManager;
import code_files.GenresConstants;
import code_files.Library;
import code_files.Movie;
import code_files.SearchResult;
import code_files.util.ImageUtils;
import code_files.util.JLabelWrappedUtils;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class RecommendationsPanel extends JPanel {
    private MovieLibraryApp app;
    private JLabel usernameLabel;
    private JPanel moviePanelContainer; // Container to hold movie panels
    private JComboBox<String> genreFilter; // Genre filter dropdown

    public RecommendationsPanel(MovieLibraryApp app) {
        this.app = app;
        setLayout(new BorderLayout());

        // Top panel with Library label, genre filter, and username + logout button
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());

        // Library label on the left
        JLabel recommendationsLabel = new JLabel("Recommendations", SwingConstants.LEFT);
        recommendationsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(recommendationsLabel, BorderLayout.WEST);

        // Genre filter dropdown on the right
        genreFilter = new JComboBox<>(GenresConstants.getAllGenresWithDefault());
        genreFilter.addActionListener(e -> updateMovieDisplay(getFilteredMovies()));
        topPanel.add(genreFilter, BorderLayout.CENTER);

        // Username and logout button on the right
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        usernameLabel = new JLabel("User: " + (app.getCurrentUser() != null ? app.getCurrentUser().getUserName() : "Guest"));
        rightPanel.add(usernameLabel);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());
        rightPanel.add(logoutButton);

        // "Back to Search" button
        JButton backToSearchButton = new JButton("Back to Search");
        backToSearchButton.addActionListener(e -> app.switchToPanel("search"));
        rightPanel.add(backToSearchButton);

        topPanel.add(rightPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Scrollable panel to display movies in the library
        moviePanelContainer = new JPanel();
        moviePanelContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10)); // Flow layout for movie panels
        moviePanelContainer.setPreferredSize(new Dimension(1250, 10000)); 
        JScrollPane scrollPane = new JScrollPane(moviePanelContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        // Initial display of movies
        updateMovieDisplay(getFilteredMovies());
    }

    // Get the filtered movies based on genre selection
    private List<Movie> getFilteredMovies() {
        String selectedGenre = (String) genreFilter.getSelectedItem();
        Library library = app.getCurrentUser().getLibrary(); // Get the current user's library

        try {
            // Get recommended movies from the library
            SearchResult recommendedMovies = library.getRecommendedMovies();

            if ("All Genres".equals(selectedGenre)) {
                // If "All Genres" is selected, return all recommended movies
                return recommendedMovies.getMovies();
            }

            // Filter recommended movies based on the selected genre
            SearchResult filteredResult = library.getFilteredRecommendedMovies(selectedGenre, recommendedMovies);
            return filteredResult.getMovies();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving recommendations: " + e.getMessage());
            return new ArrayList<>(); // Return an empty list in case of error
        }
    }

    // Update the movie display with a list of movies
    private void updateMovieDisplay(List<Movie> movies) {
        moviePanelContainer.removeAll(); // Clear current movie panels
        for (Movie movie : movies) {
            MoviePanel moviePanel = new MoviePanel(movie);
            moviePanel.setCurrentMovie(movie);
            moviePanelContainer.add(moviePanel);
        }
        moviePanelContainer.revalidate(); // Revalidate layout after adding new components
        moviePanelContainer.repaint(); // Repaint the container to update UI
    }

    // Refresh method for WatchListPanel
    public void refreshRecommendations() {
        updateMovieDisplay(getFilteredMovies());
    }

    // Logout and return to the login screen
    private void logout() {
        app.resetApplication();
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

        public MoviePanel(Movie movie) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // Vertical stack layout
            setPreferredSize(new Dimension(300, 310)); // Set the preferred size of the panel
            setAlignmentX(CENTER_ALIGNMENT);
            setBorder(new LineBorder(Color.BLACK, 1)); // Border around the panel

            // Movie title
            JLabelWrappedUtils titleWrappedLabel = new JLabelWrappedUtils();
            JLabel titleLabel = titleWrappedLabel.createWrappedLabel(movie.getMovieTitle(), 40, 60);
            titleLabel.setAlignmentX(CENTER_ALIGNMENT);
            add(titleLabel);
            add(Box.createVerticalStrut(10));

            // Movie poster
            JLabel posterLabel = new JLabel(); // Create a label to display the movie poster
            ImageUtils imageUtils = new ImageUtils();
            ImageIcon posterIcon = imageUtils.createScaledImageIcon(movie.getPosterPath(), 90, 120); // Scale the image
            posterLabel.setIcon(posterIcon); // Set the scaled image as the icon for the label
            posterLabel.setAlignmentX(CENTER_ALIGNMENT);
            add(posterLabel); // Add the image to the panel
            add(Box.createVerticalStrut(5));

            // Movie rating and votes
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS)); // Vertically arrange rating and vote count
            infoPanel.setAlignmentX(CENTER_ALIGNMENT);
            JLabel ratingLabel = new JLabel("Rating: " + DiskManager.getEntireMovieObjectFromDisk(movie.getMovieID()).getRating() + "/10"); // Display movie rating
            JLabel votesLabel = new JLabel("Votes: " + DiskManager.getEntireMovieObjectFromDisk(movie.getMovieID()).getCountVotes()); // Display vote count
            ratingLabel.setAlignmentX(CENTER_ALIGNMENT);
            votesLabel.setAlignmentX(CENTER_ALIGNMENT);
            infoPanel.add(ratingLabel);
            infoPanel.add(votesLabel);
            add(infoPanel); // Add the info panel to the main panel

            // Buttons for delete and view details
            JPanel buttonPanel = new JPanel();
            JButton addToLibraryButton = new JButton("Add to Library");
            JButton addToWatchListButton = new JButton("Add to Watchlist");
            JButton viewDetailsButton = new JButton("View Details");
            buttonPanel.add(addToLibraryButton);
            buttonPanel.add(addToWatchListButton);
            buttonPanel.add(viewDetailsButton);
            add(Box.createVerticalStrut(5));
            add(buttonPanel);
            add(Box.createVerticalStrut(10));

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
                Movie selectedMovie = getCurrentMovie();
                if (selectedMovie != null) {
                    app.setCurrentMovie(selectedMovie); // Set the current movie to the selected one
                    app.switchToPanel("details");
                } else {
                    JOptionPane.showMessageDialog(null, "Movie not found!");
                }
            });

         
        }
    }
}
