package code_files;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;


public class User {
    @Override
    public String toString() {
        return "User [userName=" + userName + ", library=" + library + "]";
    }

    private String userName;
    private String password;
    private Library library;
    private List<Review> userReviews = new ArrayList<>();
    private Watchlist watchlist;

    public User() {
        // Default constructor needed for Jackson to work
    }

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }

    public Watchlist getWatchlist() {
        return watchlist;
    }

    public void setWatchlist(Watchlist watchlist) {
        this.watchlist = watchlist;
    }

    public User(String username, String password) { // when no library and no watchlist
        this.userName = username;
        this.password = password;
        this.library = new Library();
        this.watchlist = new Watchlist();
    }

    public User(String username, String password, Library library, Watchlist watchlist) { // when library and watchlist
                                                                                          // already has movies
        this.userName = username;
        this.password = password;
        this.library = library;
        this.watchlist = watchlist;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Review> getUserReviews() {
        return userReviews;
    }

    public void addReview(Review review) {
        userReviews.add(review);
    }

    public boolean addMovieToLibrary(Movie movieToBeAdded) throws StreamWriteException, DatabindException, IOException {
        // check if movie already in library
        User currentRecordedUserInfo = DiskManager.readUserFromDisk(this.getUserName());
        Library currentUserLibrary = currentRecordedUserInfo.getLibrary();
        for (Movie m : currentUserLibrary.getListOfMovies()) {
            if (m.getMovieID() == (movieToBeAdded.getMovieID())) {
                return false;
            }

        }
        // Update Runtime Object
        this.library.addMovie(movieToBeAdded);
        // Update Disk
        DiskManager.writeUserToDisk(this);
        return true;
    }

    public void removeMovieFromLibrary(Movie movieToBeRemoved) throws IOException {
        User currentRecordedUserInfo = DiskManager.readUserFromDisk(this.getUserName());
        Library currentUserLibrary = currentRecordedUserInfo.getLibrary();
        boolean movieExists = false;
        for (Movie m : currentUserLibrary.getListOfMovies()) {
            if (m.getMovieID() == (movieToBeRemoved.getMovieID())) {
                movieExists = true;
                break;
            }

        }
        if (movieExists) {
            // Update Runtime Object
            this.library.removeMovie(movieToBeRemoved);
        }

        // Update Disk
        DiskManager.writeUserToDisk(this);

    }

    public boolean addMovieToWatchlist(Movie movieToBeAdded) throws IOException {
        // check if movie already in watchlist
        User currentRecordedUserInfo = DiskManager.readUserFromDisk(this.getUserName());
        Watchlist currentUserwatchlist = currentRecordedUserInfo.getWatchlist();
        for (Movie m : currentUserwatchlist.getListOfMovies()) {
            if (m.getMovieID() == (movieToBeAdded.getMovieID())) {
                return false;
            }

        }
        this.watchlist.addMovie(movieToBeAdded);
        // Update Disk
        DiskManager.writeUserToDisk(this);
        return true;

    }

    public void removeMovieFromWatchlist(Movie movieToBeRemoved) throws IOException {
        User currentRecordedUserInfo = DiskManager.readUserFromDisk(this.getUserName());
        Watchlist currentUserwatchlist = currentRecordedUserInfo.getWatchlist();
        boolean movieExists = false;
        for (Movie m : currentUserwatchlist.getListOfMovies()) {
            if (m.getMovieID() == (movieToBeRemoved.getMovieID())) {
                // System.out.print(m);
                movieExists = true;
                break;
            }

        }
        if (movieExists) {
            // Update Runtime Object
            this.watchlist.removeMovie(movieToBeRemoved);
        }

        // Update Disk
        DiskManager.writeUserToDisk(this);

    }

    public void updateUserReviewsToDisk(User user) throws IOException {
        for (Movie movie : user.getLibrary().getListOfMovies()) {
            // Retrieve the movie from the disk
            Movie movieFromDisk = DiskManager.getEntireMovieObjectFromDisk(movie.getMovieID());

            // Update the reviews of the movie from disk with those in the user's library
            for (Review review : movie.getMovieReviews()) {
                movieFromDisk.addOrUpdateReview(review);
            }

            // Save the updated movie back to disk
            DiskManager.writeMovieToDisk(movieFromDisk);
        }
    }

    public static User doLogin(String inputName, String password) throws IOException {

        if (DiskManager.hasUserOnDisk(inputName)) {
            User user = DiskManager.readUserFromDisk(inputName);
            for (Movie toBeUpdatedMovie : user.getLibrary().getListOfMovies()) {
                Movie movieFromDisk = DiskManager.readMovieFromDisk(toBeUpdatedMovie);
                toBeUpdatedMovie.setMovieReviews(movieFromDisk.getMovieReviews());
            }
            // Write user to disk
            DiskManager.writeUserToDisk(user);

            return user;
        } else {

            Library emptyLibrary = new Library();
            Watchlist watchlist = new Watchlist();
            User newUser = new User(inputName, password, emptyLibrary, watchlist); // writing user to memory
            DiskManager.writeUserToDisk(newUser); // writing user to disk
            return newUser;
        }

    }
}
