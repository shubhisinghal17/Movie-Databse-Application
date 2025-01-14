package code_files;

import java.util.List;

public class SearchResult {

    List<Movie> movies;

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public SearchResult(List<Movie> movies) {
        this.movies = movies;
    }

    @Override
    public String toString() {
        return "SearchResult [movies=" + movies + "]";
    }

}
