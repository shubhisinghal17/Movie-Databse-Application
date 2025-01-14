package code_files.ui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.util.List;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import code_files.TailoredList;
import code_files.util.ImageUtils;
import code_files.util.JLabelWrappedUtils;
import code_files.DiskManager;
import code_files.GlobalList;
import code_files.Movie;

public class GlobalListPanel extends JPanel {
    private final JPanel listContainer; // Panel to display the list of tailored lists
    private MovieLibraryApp app;
    private GlobalList globalList;

    public GlobalListPanel(MovieLibraryApp app) {
        this.app = app;
        this.setLayout(new BorderLayout()); // Set the layout of the panel

        // Create and add the top panel with user info and buttons
        JPanel topPanel = createTopPanel();
        this.add(topPanel, BorderLayout.NORTH);

        // Create the list container for tailored lists
        this.listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(listContainer);

        // Add "Add New List" button at the bottom
        JButton addListButton = new JButton("Add New List");
        addListButton.addActionListener(this::addNewList);

        this.add(scrollPane, BorderLayout.CENTER);
        this.add(addListButton, BorderLayout.SOUTH);

        // Load existing lists from disk
        loadExistingLists();
    }

    // Create a top panel with user information and action buttons
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel usernameLabel = new JLabel("User: " + app.getCurrentUser().getUserName());
        JButton logoutButton = new JButton("Logout");
        JButton returnToSearchButton = new JButton("Return to Search");

        // Action listeners for buttons
        logoutButton.addActionListener(e -> logout());
        returnToSearchButton.addActionListener(e -> app.switchToPanel("search"));

        // Add components to the top panel
        topPanel.add(usernameLabel);
        topPanel.add(returnToSearchButton);
        topPanel.add(logoutButton);
        return topPanel;
    }

    // Method to load existing lists
    private void loadExistingLists() {
        try {
            globalList = DiskManager.readListOFTailoredListsFromDisk();
            for (TailoredList tailoredList : globalList.getGlobalListOFTailoredLists()) {
                addListToUI(tailoredList); // Add each list to the UI
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading tailored lists: " + e.getMessage());
        }
    }

    // Method to handle the creation of a new list
    private void addNewList(ActionEvent e) {
        String listName = JOptionPane.showInputDialog(this, "Enter List Name:");
        if (listName != null && !listName.trim().isEmpty()) {
            try {
                TailoredList newList = new TailoredList(listName);
                globalList.addTailoredList(newList, globalList);
                DiskManager.writeGlobalListOFTailoredListsToDisk(globalList);
                addListToUI(newList);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving list: " + ex.getMessage());
            }
        }
    }

    // Method to add the newly created list to the UI
    private void addListToUI(TailoredList list) {
        // Create a panel for the tailored list
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBorder(BorderFactory.createTitledBorder(list.getListTitle()));

        // Create a new movie container for this list
        JPanel movieListContainer = new JPanel();
        movieListContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

        // Add movie container to the list panel
        listPanel.add(movieListContainer, BorderLayout.CENTER);
        listContainer.add(listPanel);

        // Update the movie list for this tailored list
        updateMovieList(movieListContainer, list.getListOfMovies());
        listContainer.revalidate();
        listContainer.repaint();
    }

    // Update the movie list container with movies
    public void updateMovieList(JPanel movieListContainer, List<Movie> movies) {
        movieListContainer.removeAll(); // Clear current movie panels
        for (Movie movie : movies) {
            MoviePanel moviePanel = new MoviePanel(movie); // Create a new movie panel
            moviePanel.setCurrentMovie(movie);
            movieListContainer.add(moviePanel); // Add the panel to the container
            movieListContainer.add(Box.createVerticalStrut(5)); // Add vertical space
        }
        movieListContainer.revalidate();
        movieListContainer.repaint();
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
                Movie selectedMovie = getCurrentMovie();
                if (selectedMovie != null) {
                    app.setCurrentMovie(selectedMovie);
                    app.switchToPanel("details");
                } else {
                    JOptionPane.showMessageDialog(null, "Movie not found!");
                }
            });
        }
    }

    public void refreshGlobal() {
        listContainer.removeAll();
        loadExistingLists();
        listContainer.revalidate();
        listContainer.repaint();
    }

    // Logout and return to the login screen
    private void logout() {
        app.resetApplication();
    }
}
