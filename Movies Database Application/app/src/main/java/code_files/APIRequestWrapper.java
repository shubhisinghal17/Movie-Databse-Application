package code_files;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class APIRequestWrapper {

        final static String API_URL = "https://api.themoviedb.org/3/search/movie?query=%s&include_adult=false&language=en-US&page=1"; // its
                                                                                                                                      // a
                                                                                                                                      // static
                                                                                                                                      // variable
                                                                                                                                      // because
                                                                                                                                      // it
                                                                                                                                      // always
                                                                                                                                      // stays
                                                                                                                                      // the
                                                                                                                                      // same

        // Method to fetch related movies and their reviews
        public static SearchResult relatedMovies(SearchQuery sq) {
                String typedMovie = sq.getTypedName();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(String.format(API_URL, typedMovie)))
                                .header("accept", "application/json")
                                .header("Authorization",
                                                "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJmNTM3YjhjNjIwY2Q3MjRmMjg0YWM1MjI4ODMxMTgyYiIsIm5iZiI6MTczMDgyNDE4My43MDM2MDQ3LCJzdWIiOiI2NzJhNDIzMzQzM2M4MmVhMjY3ZTZmZTYiLCJzY29wZXMiOlsiYXBpX3JlYWQiXSwidmVyc2lvbiI6MX0.TfGi8kE5cYYr14LZ-MXwX4OjUm0UQwdOCAP-qeK_q3U")
                                .method("GET", HttpRequest.BodyPublishers.noBody())
                                .build();

                HttpResponse<String> response = null;
                try {
                        response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                        String allRelatedMovieDetails = response.body();

                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode rootNode = mapper.readTree(allRelatedMovieDetails);
                        JsonNode results = rootNode.path("results");
                        List<Movie> relatedMovies = new ArrayList<>();

                        // Iterate over each movie in the search results
                        for (JsonNode movie : results) {
                                String originalLanguage = movie.path("original_language").asText();
                                if ("en".equals(originalLanguage)) {
                                        // Extract movie details
                                        String originalTitle = movie.path("original_title").asText();
                                        int movieID = Integer.parseInt(movie.path("id").asText());
                                        String overview = movie.path("overview").asText();
                                        String posterPath = "https://image.tmdb.org/t/p/w440_and_h660_bestv2"
                                                        + movie.path("poster_path").asText();
                                        Double avgVotes = Double.parseDouble(movie.path("vote_average").asText());
                                        int numberOfVotes = Integer.parseInt(movie.path("vote_count").asText());

                                        List<String> genres = new ArrayList<>();
                                        JsonNode genreIds = movie.path("genre_ids");
                                        for (JsonNode genreIdNode : genreIds) {
                                                int genreId = genreIdNode.asInt();
                                                String genreName = GenresConstants.getGenremap().get(genreId);
                                                if (genreName != null) {
                                                        genres.add(genreName);
                                                }
                                        }

                                        // Create Movie object
                                        List<Review> listReviews = new ArrayList<>();

                                        // Check if Disk has reviews for this movie
                                        if (DiskManager.hasMovieOnDisk(movieID)) {
                                                // Get reviews from the file system
                                                avgVotes = DiskManager.getEntireMovieObjectFromDisk(movieID)
                                                                .getRating();
                                                numberOfVotes = DiskManager.getEntireMovieObjectFromDisk(movieID)
                                                                .getCountVotes();
                                                listReviews = DiskManager.getMovieReviewsForMovieFromDisk(movieID);
                                        }

                                        // Runtime update
                                        Movie m = new Movie(movieID, originalTitle, overview, avgVotes, numberOfVotes,
                                                        posterPath, genres, listReviews);

                                        // Disk Update
                                        m.writeMovieToDisk();

                                        relatedMovies.add(m);
                                }
                        }

                        SearchResult sr = new SearchResult(relatedMovies);
                        return sr;

                } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                }

                return null;
        }

}
