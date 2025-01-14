package code_files;

import java.util.ArrayList;
import java.util.List;

public class Watchlist extends MovieList {

    public Watchlist() {
        super();

    }
    public SearchResult getFilteredWatchlistMovies(String genre) {
        List<Movie> filteredMovies = new ArrayList<>();
        for (Movie movie : this.getListOfMovies()) { // Use 'this' to refer to the current Watchlist object
            // Check if the movie contains the genre
            if (movie.getGenres().contains(genre)) {
                // If it matches the genre, add to the filtered list
                filteredMovies.add(movie);
            }
        }
        return new SearchResult(filteredMovies);
    }

}
