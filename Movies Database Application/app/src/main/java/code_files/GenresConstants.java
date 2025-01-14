package code_files;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

//class that create a map of genres to their genre codes which have been taken from the API
public class GenresConstants {

    private static final Map<Integer, String> genreMap = new HashMap<>();
    static {
        genreMap.put(28, "Action");
        genreMap.put(12, "Adventure");
        genreMap.put(16, "Animation");
        genreMap.put(35, "Comedy");
        genreMap.put(80, "Crime");
        genreMap.put(99, "Documentary");
        genreMap.put(18, "Drama");
        genreMap.put(10751, "Family");
        genreMap.put(14, "Fantasy");
        genreMap.put(36, "History");
        genreMap.put(27, "Horror");
        genreMap.put(10402, "Music");
        genreMap.put(9648, "Mystery");
        genreMap.put(10749, "Romance");
        genreMap.put(878, "Science Fiction");
        genreMap.put(10770, "TV Movie");
        genreMap.put(53, "Thriller");
        genreMap.put(10752, "War");
        genreMap.put(37, "Western");
    }

    public static Map<Integer, String> getGenremap() {
        return genreMap;
    }

    /**
     * Returns a String array containing "All Genres" followed by all genre values.
     * 
     * @return String[] containing all genres.
     */
    public static String[] getAllGenresWithDefault() {
        return Stream.concat(
                Stream.of("All Genres"),
                genreMap.values().stream()).toArray(String[]::new);
    }

}
