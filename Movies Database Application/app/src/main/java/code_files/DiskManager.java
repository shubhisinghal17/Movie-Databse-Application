package code_files;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DiskManager {

    // Path where your review files are stored
    final static String REVIEW_FILES_PATH = ""; // is relative path

    final static String LIST_OF_TAILORED_LISTS_PATH = "GlobalListOFTailoredLists.json";

    // Check if ListOFTailoredLists exists on disk
    public static boolean ListOFTailoredListsExists() {
        File file = new File(LIST_OF_TAILORED_LISTS_PATH);
        return file.exists();
    }

    // Read ListOFTailoredLists from disk or create if it doesn't exist
    public static GlobalList readListOFTailoredListsFromDisk() throws IOException {
        if (ListOFTailoredListsExists()) {
            // If the file exists, read it
            ObjectMapper objectMapper = new ObjectMapper();
            File file = new File(LIST_OF_TAILORED_LISTS_PATH);
            return objectMapper.readValue(file, GlobalList.class);
        } else {
            // If the file doesn't exist, create a new ListOFTailoredLists object and write
            // to disk
            GlobalList listOfTailoredLists = new GlobalList();
            writeGlobalListOFTailoredListsToDisk(listOfTailoredLists);
            return listOfTailoredLists;
        }
    }

    // Write ListOFTailoredLists to disk
    public static void writeGlobalListOFTailoredListsToDisk(GlobalList listOFTailoredLists)
            throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(LIST_OF_TAILORED_LISTS_PATH);
        objectMapper.writeValue(file, listOFTailoredLists);
    }

    // Write User Object to JSON
    public static void writeUserToDisk(User u) throws StreamWriteException, DatabindException, IOException {
        String filePath = u.getUserName() + ".json";
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(filePath);
        objectMapper.writeValue(file, u);
    }

    // Read User Object from json if it exists
    public static User readUserFromDisk(String inputName) throws IOException {
        String filePath = inputName + ".json";
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(filePath);
        User user = objectMapper.readValue(file, User.class);
        return user;

    }

    // Write movie review for movie id to json
    public static void writeMovieToDisk(Movie movie) {
        String fileName = movie.getMovieID() + ".json";
        File movieFile = new File(fileName);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(movieFile, movie);
        } catch (IOException e) {
            System.err.println("Error saving new review: " + e.getMessage());
        }

    }

    // Read movie review for movie id to json
    public static Movie readMovieFromDisk(Movie movie) throws StreamReadException, DatabindException, IOException {
        String filePath = movie.getMovieID() + ".json";
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(filePath);
        Movie updatedMovie = objectMapper.readValue(file, Movie.class);
        return updatedMovie;
    }

    // Reading Movie object from file
    public static Movie getEntireMovieObjectFromDisk(int movieID) {
        String filePath = movieID + ".json";
        File file = new File(filePath);

        ObjectMapper objectMapper = new ObjectMapper();

        try {

            Movie m = objectMapper.readValue(file, Movie.class);
            return m;

        } catch (IOException e) {

            System.err.println("Error reading review file " + file + ": "
                    + e.getMessage());
            return null;
        }
    }

    // get list of reviews of a specific movie from Disk
    public static List<Review> getMovieReviewsForMovieFromDisk(int movieID) {
        Movie movie = getEntireMovieObjectFromDisk(movieID);
        return (movie != null) ? movie.getMovieReviews() : null;
    }

    // checking if the details of the specisif user have been created on the disk
    // using the user object
    public static boolean hasUserOnDisk(String inputName) {
        String filePath = inputName + ".json";
        File file = new File(filePath);
        return file.exists();

    }

    // checking if the particular movie has been recorded on the disk
    public static boolean hasMovieOnDisk(int movieID) {
        String filePath = movieID + ".json";
        File file = new File(filePath);
        return file.exists();

    }

}
