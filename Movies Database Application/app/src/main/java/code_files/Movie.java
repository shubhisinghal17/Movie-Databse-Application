package code_files;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Movie { // Movie class containing movieID, name, summary, rating, votes, rating. poster,
                     // genres, reviews(the the users we create)

    private int movieID;
    private String movieTitle;
    private String movieSummary;
    private Double rating;
    private int countVotes;
    private String posterPath;
    private List<String> genres;
    private List<Review> movieReviews;

    public Movie() { // empty constructor to ensure that jackson marshalls the object details
                     // correctly

    }

    public Movie(int movieID, String movieTitle, String movieSummary, Double avgVotes, int numberOfVotes,
            String posterPath, List<String> genres, List<Review> movieReviews) {
        this.movieID = movieID;
        this.movieTitle = movieTitle;
        this.movieSummary = movieSummary;
        this.rating = avgVotes;
        this.countVotes = numberOfVotes;
        this.posterPath = posterPath;
        this.genres = genres;
        this.movieReviews = movieReviews;

    }

    public void writeMovieToDisk() { // the movie getting processed is being written to the disk inform of JSON
                                     // document using jackson
        DiskManager.writeMovieToDisk(this);

    }

    public List<Review> getMovieReviews() { // getter for movie reviews
        return movieReviews;
    }

    public void setMovieReviews(List<Review> movieReviews) { // setter for movie reviews
        this.movieReviews = movieReviews;
    }

    public void addOrUpdateReview(Review r) throws IOException { // adding or updating new reviews by users to ensure
                                                                 // that no duplicity happens
        Movie m = DiskManager.getEntireMovieObjectFromDisk(this.movieID);

        // Update runtime object
        if (hasUserReview(r, m)) {
            Review existingReview = findReviewForUser(r, m);

            // Subtract the old rating before updating
            double oldRating = existingReview.getUserRating();
            double totalRating = m.getRating() * m.getCountVotes();
            m.setRating((totalRating - oldRating + r.getUserRating()) / m.getCountVotes()); // Update rating

            existingReview.setUserRating(r.getUserRating());
            existingReview.setUserReview(r.getUserReview());

        } else {
            m.movieReviews.add(r);
            m.addRating(r);
        }

        // Update disk
        DiskManager.writeMovieToDisk(m);

        // Read user from disk and then change in runtime
        User usr = DiskManager.readUserFromDisk(r.getUserName());
        for (int i = 0; i < usr.getLibrary().getListOfMovies().size(); i++) {
            Movie movie = usr.getLibrary().getListOfMovies().get(i);
            if (movie.getMovieID() == m.getMovieID()) {
                usr.getLibrary().getListOfMovies().set(i, m);
            }
        }

        DiskManager.writeUserToDisk(usr);
    }

    public Review findReviewForUser(Review r, Movie m) { // find speciic review by a user for a specific movie
        for (Review existingReview : m.getMovieReviews()) {

            if (existingReview.getUserName().equals(r.getUserName())) {
                return existingReview;
            }
        }
        return null;
    }

    public boolean hasUserReview(Review r, Movie m) { // checking if the user has written review for the movie and
                                                      // whether that review exists in our records
        for (Review existingReview : m.getMovieReviews()) {
            if (existingReview.getUserName().equals(r.getUserName())) {
                return true;
            }
        }
        return false;
    }

    public int getMovieID() { // getter for movieID
        return movieID;
    }

    public void setMovieID(int movieID) { // setter for movieID
        this.movieID = movieID;
    }

    public String getMovieTitle() { // getter for movie title
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) { // setter for movie title
        this.movieTitle = movieTitle;
    }

    public String getMovieSummary() { // getter for movie summary
        return movieSummary;
    }

    public void setMovieSummary(String movieSummary) { // setter for movie summary
        this.movieSummary = movieSummary;
    }

    public Double getRating() {

        // Convert the double value to BigDecimal for precise calculation
        BigDecimal bd = BigDecimal.valueOf(rating);

        // Set the scale to 2 decimal places and round using HALF_UP rounding mode
        bd = bd.setScale(2, RoundingMode.HALF_UP);

        // Convert the BigDecimal back to a double and return
        return bd.doubleValue();
    }

    // Method to update the rating based on reviews
    public void addRating(Review review) {
        double totalRating = this.getRating() * this.getCountVotes();
        int newCountVotes = this.getCountVotes() + 1;
        this.setCountVotes(newCountVotes);
        double newRating = (totalRating + review.getUserRating()) / newCountVotes;
        this.setRating(newRating);
    }

    public void setRating(Double rating) { // setter for rating
        this.rating = rating;
    }

    public int getCountVotes() { // getter for vote count
        return countVotes;
    }

    public void setCountVotes(int countVotes) { // setter for votes
        this.countVotes = countVotes;
    }

    public String getPosterPath() { // getter for poster image
        return posterPath;
    }

    public void setPosterPath(String posterPath) { // setter for poster image
        this.posterPath = posterPath;
    }

    public List<String> getGenres() { // getter for the genres(list)
        return genres;
    }

    public void setGenres(List<String> genres) { // genre setter
        this.genres = genres;
    }

    @Override
    public String toString() {
        return "Movie [movieID=" + movieID + "]";
    }

}
