package code_files.ui;

import java.awt.*;
import java.io.IOException;
import java.util.List;

import code_files.DiskManager;
import code_files.GenresConstants;
import code_files.Movie;
import code_files.Watchlist;
import code_files.util.ImageUtils;
import code_files.util.JLabelWrappedUtils;
import code_files.SearchResult;
import code_files.User;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

public class WatchListPanel extends JPanel {
    private MovieLibraryApp app;
    private User currentUser;
    private JLabel usernameLabel;
    private JPanel moviePanelContainer; // Container to hold movie panels
    private JComboBox<String> genreFilter; // Genre filter dropdown

    public WatchListPanel(MovieLibraryApp app) {
        this.app = app;
        // Ensure currentUser is not null before accessing its library
        this.currentUser = app.getCurrentUser();
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "You must be logged in to view your watchlist.");
            app.switchToPanel("login"); // Redirect to login screen
            return; // Stop further processing
        }

        setLayout(new BorderLayout());

        // Top panel with Watchlist label, genre filter, and username + logout button
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());

        // Watchlist label on the left
        JLabel watchlistLabel = new JLabel("Watchlist", SwingConstants.LEFT);
        watchlistLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(watchlistLabel, BorderLayout.WEST);

        // Genre filter dropdown in the center
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

        // Scrollable panel to display movies in the watchlist
        moviePanelContainer = new JPanel();
        moviePanelContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        moviePanelContainer.setPreferredSize(new Dimension(1410, 10000)); 
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
        Watchlist watchlist = app.getCurrentUser().getWatchlist();

        if ("All Genres".equals(selectedGenre)) {
            return watchlist.getListOfMovies();
        }
        SearchResult filteredResult = watchlist.getFilteredWatchlistMovies(selectedGenre);
        return filteredResult.getMovies();
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

    // Logout and return to the login screen
    private void logout() {
        app.resetApplication();
    }

     // Refresh method for WatchListPanel
     public void refreshWatchList() {
        updateMovieDisplay(getFilteredMovies());
    }

    // MoviePanel class to represent each movie visually in the watchlist
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
            setPreferredSize(new Dimension(220, 270)); // Set the preferred size of the panel
            setAlignmentX(CENTER_ALIGNMENT); // Align the entire panel to the center
            setBorder(new LineBorder(Color.BLACK, 1)); // Border around the panel

            // Movie title
            JLabelWrappedUtils titleWrappedLabel = new JLabelWrappedUtils();
            JLabel titleLabel = titleWrappedLabel.createWrappedLabel(movie.getMovieTitle(), 30, 60);
            titleLabel.setAlignmentX(CENTER_ALIGNMENT);
            add(titleLabel);
            add(Box.createVerticalStrut(15));

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
            infoPanel.setAlignmentX(CENTER_ALIGNMENT); // Center-align the info panel
            JLabel ratingLabel = new JLabel("Rating: " + DiskManager.getEntireMovieObjectFromDisk(movie.getMovieID()).getRating() + "/10"); // Display movie rating
            JLabel votesLabel = new JLabel("Votes: " + DiskManager.getEntireMovieObjectFromDisk(movie.getMovieID()).getCountVotes()); // Display vote count
            ratingLabel.setAlignmentX(CENTER_ALIGNMENT);
            votesLabel.setAlignmentX(CENTER_ALIGNMENT);
            infoPanel.add(ratingLabel);
            infoPanel.add(votesLabel);
            add(infoPanel); // Add the info panel to the main panel

            // Buttons for delete and view details
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS)); // Horizontally arrange the buttons
            buttonPanel.setAlignmentX(CENTER_ALIGNMENT); // Center-align the button panel
            JButton deleteButton = new JButton("Delete");
            JButton viewDetailsButton = new JButton("Details");
            buttonPanel.add(deleteButton);
            buttonPanel.add(viewDetailsButton);
            add(buttonPanel);

            // Delete movie from watchlist button action
            deleteButton.addActionListener(e -> {
                Watchlist watchlist = currentUser.getWatchlist();
                watchlist.removeMovie(movie); // Remove the movie from the watchlist
                try {
                    currentUser.addMovieToWatchlist(movie);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                JOptionPane.showMessageDialog(this, "Movie removed from your watchlist!");
                refreshWatchList(); // Update movie list after deletion
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

            add(Box.createVerticalStrut(10)); // Add vertical spacing
        }
    }
}
