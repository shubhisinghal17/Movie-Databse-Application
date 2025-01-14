package code_files;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Library {
    @Override
    public String toString() {
        return "Library [listOfMovies=" + listOfMovies + "]";
    }

    final static String REVIEW_FILES_PATH = "";

    private List<Movie> listOfMovies;

    public List<Movie> getListOfMovies() {
        return listOfMovies;
    }

    public Library(List<Movie> listOfMovies) {
        this.listOfMovies = listOfMovies;
    }

    // adding a Movie object to a user's Library
    public void addMovie(Movie m) {
        this.listOfMovies.add(m);
    }

    // user removes movie from library
    public void removeMovie(Movie movieToBeRemoved) {
        for (int i = 0; i < listOfMovies.size(); i++) {
            if (listOfMovies.get(i).getMovieID() == movieToBeRemoved.getMovieID()) {
                listOfMovies.remove(listOfMovies.get(i));
            }
        }
    }

    public Library() { // when no user or library
        this.listOfMovies = new ArrayList<>();
    }

    // user gets movie recommendations based on the movies they have added to their
    // library
    @JsonIgnore
    public SearchResult getRecommendedMovies() throws Exception {
        List<Movie> recommendedMoviesList = new ArrayList<>();
        for (Movie movie : this.listOfMovies) {
            String url = String.format("https://api.themoviedb.org/3/movie/%d/recommendations?language=en-US&page=1",
                    movie.getMovieID());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("accept", "application/json")
                    .header("Authorization",
                            "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJmNTM3YjhjNjIwY2Q3MjRmMjg0YWM1MjI4ODMxMTgyYiIsIm5iZiI6MTczMTA5NDQzMy4wMTQxOTM4LCJzdWIiOiI2NzJhNDIzMzQzM2M4MmVhMjY3ZTZmZTYiLCJzY29wZXMiOlsiYXBpX3JlYWQiXSwidmVyc2lvbiI6MX0._vZSVEsxP58E767uZWKxF0qzEcgHDO6ebi1vVFkyB2Q")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = null;

            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            String allRelatedMovieDetails = response.body();

            ObjectMapper mapper = new ObjectMapper();

            JsonNode rootNode = mapper.readTree(allRelatedMovieDetails);
            JsonNode results = rootNode.path("results");

            for (JsonNode moviedetails : results) {
                String originalLanguage = moviedetails.path("original_language").asText();
                if ("en".equals(originalLanguage)) {
                    // Extract the movie details
                    String originalTitle = moviedetails.path("original_title").asText();
                    int movieIDRecomm = Integer.parseInt(moviedetails.path("id").asText());
                    String overview = moviedetails.path("overview").asText();
                    String posterPath = "https://image.tmdb.org/t/p/w440_and_h660_bestv2"
                            + moviedetails.path("poster_path").asText();
                    Double avgVotes = Double.parseDouble(moviedetails.path("vote_average").asText());
                    int numberOfVotes = Integer.parseInt(moviedetails.path("vote_count").asText());
                    List<String> genres = new ArrayList<>();
                    JsonNode genreIds = moviedetails.path("genre_ids");
                    List<Review> listReviews = new ArrayList<>();
                    for (JsonNode genreIdNode : genreIds) {
                        int genreId = genreIdNode.asInt();

                        String genreName = GenresConstants.getGenremap().get(genreId);

                        // If the genre name exists, add it to the genres list
                        if (genreName != null) {
                            genres.add(genreName);
                        }
                    }

                    // Check if Disk has reviews for this movie
                    if (DiskManager.hasMovieOnDisk(movieIDRecomm)) {
                        // Get reviews from the file system

                        listReviews = DiskManager.getMovieReviewsForMovieFromDisk(movieIDRecomm);

                    }

                    // Runtime update
                    Movie m = new Movie(movieIDRecomm, originalTitle, overview, avgVotes, numberOfVotes,
                            posterPath, genres, listReviews);

                    // Disk Update
                    m.writeMovieToDisk();
                    recommendedMoviesList.add(m);
                }
            }

        }
        return new SearchResult(recommendedMoviesList);
    }

    public SearchResult getFilteredRecommendedMovies(String genre, SearchResult recommendedMovies) {
        List<Movie> filteredMovies = new ArrayList<>();
        for (Movie movie : recommendedMovies.getMovies()) {
            // Check if the movie contains the genre
            if (movie.getGenres().contains(genre)) {
                // If it matches the genre, add to the filtered list
                filteredMovies.add(movie);
            }
        }
        return new SearchResult(filteredMovies);
    }

    public SearchResult getFilteredLibraryMovies(String genre, Library library) {
        List<Movie> filteredMovies = new ArrayList<>();
        for (Movie movie : library.getListOfMovies()) {
            // Check if the movie contains the genre
            if (movie.getGenres().contains(genre)) {
                // If it matches the genre, add to the filtered list
                filteredMovies.add(movie);
            }
        }
        return new SearchResult(filteredMovies);
    }

    public static List<Review> getReviewsForMovie(int movieID) {

        List<Review> reviews = new ArrayList<>();
        File reviewDir = new File(REVIEW_FILES_PATH);

        // Scan all files in the review directory
        File[] files = reviewDir.listFiles(
                (dir, name) -> name.endsWith(".json") && name.startsWith(String.valueOf(movieID)));

        if (files != null) {
            ObjectMapper objectMapper = new ObjectMapper();

            // Iterate through the files and read the reviews
            for (File file : files) {
                try {
                    // Read the review from the JSON file
                    Review review = objectMapper.readValue(file, Review.class);

                    // Add the review to the list
                    reviews.add(review);

                } catch (IOException e) {
                    System.err.println("Error reading review file " + file.getName() + ": "
                            + e.getMessage());
                }
            }
        }

        return reviews;

    }
}
