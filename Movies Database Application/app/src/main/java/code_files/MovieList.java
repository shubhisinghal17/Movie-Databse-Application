package code_files;

import java.util.ArrayList;
import java.util.List;

public abstract class MovieList {
    private final List<Movie> listOfMovies;

    public MovieList() {
        this.listOfMovies = new ArrayList<>();
    }

    public MovieList(List<Movie> listOfMovies) {
        this.listOfMovies = listOfMovies;
    }

    public List<Movie> getListOfMovies() {
        return listOfMovies;
    }

    // adding movie to the Movie List
    public boolean addMovie(Movie movieToBeAdded) {
        if (listOfMovies.size() == 0) {
            listOfMovies.add(movieToBeAdded);
            return true;
        }
        int i = 0;
        while (i < listOfMovies.size()) {
            if (listOfMovies.get(i).getMovieID() == movieToBeAdded.getMovieID()) {
                return false;
            }
            i++;
        }
        listOfMovies.add(movieToBeAdded);
        return true;
    }

    // remove movie from the movie list
    public void removeMovie(Movie movieToBeRemoved) {
        for (int i = 0; i < listOfMovies.size(); i++) {
            if (listOfMovies.get(i).getMovieID() == movieToBeRemoved.getMovieID()) {
                listOfMovies.remove(listOfMovies.get(i));
            }
        }
    }

}
