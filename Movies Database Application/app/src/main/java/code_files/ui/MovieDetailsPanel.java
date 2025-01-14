package code_files.ui;

import java.awt.*;
import javax.swing.*;
import code_files.Movie;
import code_files.Review;
import code_files.util.ImageUtils;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;

import code_files.TailoredList;
import code_files.DiskManager;
import code_files.GlobalList;

public class MovieDetailsPanel extends JPanel {

    private MovieLibraryApp app;
    private Movie movie;
    private JLabel usernameLabel;
    private JTextArea reviewTextArea;
    private JComboBox<Integer> ratingComboBox;
    private JPanel reviewDisplayPanel; // Panel for all reviews
    private JLabel movieTitleLabel;
    private JLabel movieIdLabel;
    private JLabel genresLabel;
    private JLabel averageRatingLabel;
    private JLabel voteCountLabel;
    private JTextArea movieSummaryArea;
    private JLabel posterLabel;
    private JPanel movieDetailsReviewsPanel;

    public MovieDetailsPanel(MovieLibraryApp app, Movie movie) {
        this.app = app;
        this.movie = movie;
        this.movieDetailsReviewsPanel = new JPanel(new BorderLayout());
        setLayout(new BorderLayout());
        buildUI();
        refreshDetails();
    }

    // Build the UI layout
    private void buildUI() {
        removeAll(); // Clear existing layout for rebuilding the UI
        // Build each section
        add(buildTopPanel(), BorderLayout.NORTH);
        this.movieDetailsReviewsPanel.add(buildMovieDetailsPanel(), BorderLayout.CENTER);
        this.movieDetailsReviewsPanel.add(buildReviewPanel(), BorderLayout.SOUTH);
        add(this.movieDetailsReviewsPanel, BorderLayout.CENTER);
        revalidate();
        repaint();

    }

    // Update the movie details dynamically without rebuilding the entire UI
    public void updateMovieDetails(Movie movie) {
        this.movie = movie;
        // Update movie details
        posterLabel.setIcon(new ImageUtils().createScaledImageIcon(movie.getPosterPath(), 150, 150));
        movieTitleLabel.setText("Title: " + movie.getMovieTitle());
        movieIdLabel.setText("ID: " + movie.getMovieID());
        genresLabel.setText("Genres: " + String.join(", ", movie.getGenres()));
        averageRatingLabel.setText("Average Rating: " + DiskManager.getEntireMovieObjectFromDisk(movie.getMovieID()).getRating());
        voteCountLabel.setText("Votes: " + DiskManager.getEntireMovieObjectFromDisk(movie.getMovieID()).getCountVotes());
        movieSummaryArea.setText("Overview: " + movie.getMovieSummary());

        // Update the reviews section
        updateReviews();
    }

    // Refresh method for MovieDetailsPanel
    public void refreshDetails() {
        updateMovieDetails(movie);
        movieDetailsReviewsPanel.revalidate();
        movieDetailsReviewsPanel.repaint();
    }

    // Top section: Header with movie details and user information
    private JPanel buildTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setPreferredSize(new Dimension(800, 60));

        // Movie Detail Label
        JLabel movieDetailLabel = new JLabel("Movie Detail", SwingConstants.LEFT);
        movieDetailLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(movieDetailLabel, BorderLayout.WEST);

        // Right section: Username and logout button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        usernameLabel = new JLabel("User: " + getUsername());
        rightPanel.add(usernameLabel);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());
        rightPanel.add(logoutButton);

        // "Back to Search" button
        JButton backToSearchButton = new JButton("Back to Search");
        backToSearchButton.addActionListener(e -> app.switchToPanel("search"));
        rightPanel.add(backToSearchButton);

        topPanel.add(rightPanel, BorderLayout.EAST);

        return topPanel;
    }

    // Middle section: Movie details and poster
    private JPanel buildMovieDetailsPanel() {
        JPanel movieDetailsPanel = new JPanel(new BorderLayout());
        movieDetailsPanel.setPreferredSize(new Dimension(800, 400));

        // Create a Box layout container for the left side
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));

        // Add padding (horizontal strut)
        leftPanel.add(Box.createHorizontalStrut(15));

        // Create poster label with scaled image
        posterLabel = new JLabel();
        posterLabel.setAlignmentX(CENTER_ALIGNMENT);

        // Add poster label to left panel
        leftPanel.add(posterLabel);

        // Add the left panel to movieDetailPanel
        movieDetailsPanel.add(leftPanel, BorderLayout.WEST);

        // Right: Movie information
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setPreferredSize(new Dimension(500, 400));
        infoPanel.add(Box.createHorizontalStrut(30));

        // Add movie labels to infoPanel
        movieTitleLabel = createLabel("");
        movieIdLabel = createLabel("");
        genresLabel = createLabel("");
        averageRatingLabel = createLabel("");
        voteCountLabel = createLabel("");

        // Use JTextArea for the movie summary
        movieSummaryArea = new JTextArea("Overview: " + movie.getMovieSummary());
        movieSummaryArea.setLineWrap(true); // Enable line wrapping
        movieSummaryArea.setWrapStyleWord(true); // Wrap at word boundaries
        movieSummaryArea.setEditable(false); // Make it non-editable
        movieSummaryArea.setFocusable(false); // Prevent focus for better label-like behavior
        movieSummaryArea.setOpaque(false); // Make it look like a label
        movieSummaryArea.setFont(new Font("Arial", Font.PLAIN, 14)); // Set font
        movieSummaryArea.setBorder(null); // Remove border
        movieSummaryArea.setAlignmentX(Component.LEFT_ALIGNMENT); // Ensure left alignment

        JButton addToListButton = new JButton("Add to List");
        addToListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    showAddToListDialog();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        // Add components to infoPanel
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(movieTitleLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(movieIdLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(genresLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(averageRatingLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(voteCountLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(movieSummaryArea);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(addToListButton);
        // Add infoPanel to the center
        movieDetailsPanel.add(infoPanel, BorderLayout.CENTER);

        return movieDetailsPanel;
    }

    private void showAddToListDialog() throws IOException {
        GlobalList globalList = DiskManager.readListOFTailoredListsFromDisk();
    
        // Get the list of tailored movie lists from the global list
        List<TailoredList> lists = globalList.getGlobalListOFTailoredLists();
    
        // If no lists are available, inform the user and exit
        if (lists.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No lists available. Please create a list first.", "No Lists",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        // Extract the list titles to show as options in the dialog box
        String[] listTitles = lists.stream().map(TailoredList::getListTitle).toArray(String[]::new);
    
        // Show a dialog for the user to choose a list
        String selectedList = (String) JOptionPane.showInputDialog(
                this,
                "Choose a list to add this movie:",
                "Add to List",
                JOptionPane.QUESTION_MESSAGE,
                null,
                listTitles,
                listTitles[0]); // Default to the first list title
    
        // If the user selects a list
        if (selectedList != null) {
            // Find the tailored list that matches the selected list title
            TailoredList targetList = lists.stream()
                    .filter(list -> list.getListTitle().equals(selectedList))
                    .findFirst()
                    .orElse(null);
    
            // If the target list is found, attempt to add the movie
            if (targetList != null) {
                // Call addMovie to add the movie to the list and check if it was already present
                boolean noExists = targetList.updateTailoredList(movie,targetList);
   
                // Display a message based on whether the movie was added or already exists
                if (!noExists) {
                    JOptionPane.showMessageDialog(this, "Movie already exists in the list.", "Info",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // targetList.updateGlobalList(globalList,targetList);
                    DiskManager.writeGlobalListOFTailoredListsToDisk(globalList);
                    JOptionPane.showMessageDialog(this, "Movie added to " + selectedList, "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                // If the target list is not found, show an error message
                JOptionPane.showMessageDialog(this, "Selected list not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    

    // Bottom section: Separate review input panel and review display panel
    private JPanel buildReviewPanel() {
        JPanel reviewPanel = new JPanel(new BorderLayout());

        // Review Input Section
        JPanel reviewInputSection = new JPanel(new BorderLayout());
        reviewInputSection.setBorder(BorderFactory.createTitledBorder("Submit Your Review"));

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.add(new JLabel("Your Review:"));
        reviewTextArea = new JTextArea(4, 30);
        reviewTextArea.setLineWrap(true);
        reviewTextArea.setWrapStyleWord(true);
        inputPanel.add(new JScrollPane(reviewTextArea));
        inputPanel.add(new JLabel("Rating:"));
        ratingComboBox = new JComboBox<>();
        for (int i = 1; i <= 10; i++) {
            ratingComboBox.addItem(i);
        }
        inputPanel.add(ratingComboBox);

        reviewInputSection.add(inputPanel, BorderLayout.CENTER);

        // Submit Button
        JButton submitButton = new JButton("Submit Review");
        submitButton.addActionListener(e -> submitReview());
        reviewInputSection.add(submitButton, BorderLayout.SOUTH);

        reviewPanel.add(reviewInputSection, BorderLayout.NORTH);

        // Review Display Section
        JPanel reviewDisplaySection = new JPanel(new BorderLayout());
        reviewDisplaySection.setBorder(BorderFactory.createTitledBorder("User Reviews"));
        reviewDisplayPanel = new JPanel();
        reviewDisplayPanel.setLayout(new BoxLayout(reviewDisplayPanel, BoxLayout.Y_AXIS));
        JScrollPane reviewScrollPane = new JScrollPane(reviewDisplayPanel);
        reviewDisplaySection.add(reviewScrollPane, BorderLayout.CENTER);
        reviewPanel.add(reviewDisplaySection, BorderLayout.CENTER);

        return reviewPanel;
    }

    public void updateReviews() {
        reviewDisplayPanel.removeAll();
        for (Review review : DiskManager.getMovieReviewsForMovieFromDisk(movie.getMovieID())) {
            JLabel reviewLabel = new JLabel(formatReviewText(review));
            reviewDisplayPanel.add(reviewLabel);
        }
        reviewDisplayPanel.revalidate();
        reviewDisplayPanel.repaint();

    }

    // Submit review logic
    private void submitReview() {
        String userReview = reviewTextArea.getText().trim();
        int userRating = (Integer) ratingComboBox.getSelectedItem();

        if (userReview.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Review cannot be empty!");
            return;
        }
        try {
            // Add or update the review in the movie object
            movie.addOrUpdateReview(new Review(movie.getMovieID(), userReview, getUsername(), userRating));
            refreshDetails();
            JOptionPane.showMessageDialog(this, "Review submitted successfully!");
            reviewTextArea.setText("");
            ratingComboBox.setSelectedIndex(0);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to submit review!");
            e.printStackTrace();
        }
    }

    private void logout() {
        // Clear current user before switching panels
        app.resetApplication();
    }

    // Create label methods
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    // Get username methods
    private String getUsername() {
        return app.getCurrentUser() != null ? app.getCurrentUser().getUserName() : "Guest";
    }

    // format review method
    private String formatReviewText(Review review) {
        return String.format("<html><b>User:</b> %s <b>Rating:</b> %d<br>%s</html>",
                review.getUserName(), review.getUserRating(), review.getUserReview());
    }
}
