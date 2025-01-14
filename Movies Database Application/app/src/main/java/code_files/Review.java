package code_files;

public class Review {
    private int movieID;
    private String userReview;
    private String userName;
    private int userRating;

    public Review(int movieID, String userReview, String userName, int userRating) {
        this.movieID = movieID;
        this.userReview = userReview;
        this.userName = userName;
        this.userRating = userRating;
    }

    public Review() {
    }

    public int getUserRating() {
        return userRating;
    }

    public void setUserRating(int userRating) {
        this.userRating = userRating;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserReview() {
        return userReview;
    }

    public void setUserReview(String userReview) {
        this.userReview = userReview;
    }

    public int getMovieID() {
        return movieID;
    }

    public void setMovieID(int movieID) {
        this.movieID = movieID;
    }

    @Override
    public String toString() {
        return "Review [movieID=" + movieID + ", userReview=" + userReview + ", userName=" + userName + ", userRating="
                + userRating + "]";
    }

}
