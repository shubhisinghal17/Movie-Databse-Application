package code_files.ui;

import java.awt.*;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.LineBorder;

import java.util.List;

import code_files.DiskManager;
import code_files.GenresConstants;
import code_files.Library;
import code_files.Movie;
import code_files.SearchResult;
import code_files.User;
import code_files.util.ImageUtils;
import code_files.util.JLabelWrappedUtils;

public class LibraryPanel extends JPanel {
    private MovieLibraryApp app;
    private User currentUser;
    private JLabel usernameLabel;
    private JPanel moviePanelContainer;
    private JComboBox<String> genreFilter;

    public LibraryPanel(MovieLibraryApp app) {
        this.app = app;
        // Ensure currentUser is not null before accessing its library
        this.currentUser = app.getCurrentUser();
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "You must be logged in to view your library.");
            app.switchToPanel("login"); // Redirect to login screen
            return;
        }

        setLayout(new BorderLayout());

        // Top panel with Library label, genre filter, and username + logout button
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());

        // Library label on the left
        JLabel libraryLabel = new JLabel("Library", SwingConstants.LEFT);
        libraryLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(libraryLabel, BorderLayout.WEST);

        // Genre filter dropdown on the right
        genreFilter = new JComboBox<>(GenresConstants.getAllGenresWithDefault());
        genreFilter.addActionListener(e -> updateMovieDisplay(getFilteredMovies()));
        topPanel.add(genreFilter, BorderLayout.CENTER);

        // Username and logout button on the right
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        usernameLabel = new JLabel("User: " + (currentUser != null ? currentUser.getUserName() : "Guest"));
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

        // Scrollable panel to display movies in the library with a flowing layout
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
        Library library = currentUser.getLibrary();
        if ("All Genres".equals(selectedGenre)) {
            return library.getListOfMovies();
        }
        SearchResult filteredResult = library.getFilteredLibraryMovies(selectedGenre, library);
        return filteredResult.getMovies();
    }

    // Update the movie display with a list of movies
    private void updateMovieDisplay(List<Movie> movies) {
        moviePanelContainer.removeAll();
        for (Movie movie : movies) {
            MoviePanel moviePanel = new MoviePanel(movie);
            moviePanel.setCurrentMovie(movie);
            moviePanelContainer.add(moviePanel);
        }
        moviePanelContainer.revalidate();
        moviePanelContainer.repaint();
    }

    // Refresh method for LibraryPanel
    public void refreshLibrary() {
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
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setPreferredSize(new Dimension(220, 270));
            setBorder(new LineBorder(Color.BLACK, 1));

            // Movie title
            JLabelWrappedUtils titleWrappedLabel = new JLabelWrappedUtils();
            JLabel titleLabel = titleWrappedLabel.createWrappedLabel(movie.getMovieTitle(), 30, 60);
            titleLabel.setAlignmentX(CENTER_ALIGNMENT);
            add(titleLabel);
            add(Box.createVerticalStrut(15));

            // Movie poster
            JLabel posterLabel = new JLabel();
            ImageUtils imageUtils = new ImageUtils();
            ImageIcon posterIcon = imageUtils.createScaledImageIcon(movie.getPosterPath(), 90, 120);
            posterLabel.setIcon(posterIcon); // Set the scaled image as the icon for the label
            posterLabel.setAlignmentX(CENTER_ALIGNMENT);
            add(posterLabel); // Add the image to the panel
            add(Box.createVerticalStrut(5));

            // Movie rating and votes
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setAlignmentX(CENTER_ALIGNMENT);
            JLabel ratingLabel = new JLabel("Rating: " + DiskManager.getEntireMovieObjectFromDisk(movie.getMovieID()).getRating() + "/10"); // Display movie rating
            JLabel votesLabel = new JLabel("Votes: " + DiskManager.getEntireMovieObjectFromDisk(movie.getMovieID()).getCountVotes()); // Display vote count
            ratingLabel.setAlignmentX(CENTER_ALIGNMENT);
            votesLabel.setAlignmentX(CENTER_ALIGNMENT);
            infoPanel.add(ratingLabel);
            infoPanel.add(votesLabel);
            add(infoPanel);

            // Buttons for delete and view details
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
            buttonPanel.setAlignmentX(CENTER_ALIGNMENT);
            JButton deleteButton = new JButton("Delete"); // Button to delete a movie
            JButton viewDetailsButton = new JButton("Details"); // Button to view movie details
            buttonPanel.add(deleteButton);
            buttonPanel.add(viewDetailsButton);
            add(buttonPanel);

            // Delete movie from library button action
            deleteButton.addActionListener(e -> {
                Library library = currentUser.getLibrary();
                library.removeMovie(movie);
                try {
                    currentUser.removeMovieFromLibrary(movie);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                JOptionPane.showMessageDialog(this, "Movie removed from your library!");
                refreshLibrary();
            });

            // View movie details button action
            viewDetailsButton.addActionListener(e -> {
                Movie selectedMovie = getCurrentMovie();
                if (selectedMovie != null) {
                    app.setCurrentMovie(selectedMovie);
                    app.switchToPanel("details");
                } else {
                    JOptionPane.showMessageDialog(null, "Movie not found!");
                }
            });

            buttonPanel.add(deleteButton);
            buttonPanel.add(viewDetailsButton);
            add(Box.createVerticalStrut(10));
            add(buttonPanel);
        }
    }
}
