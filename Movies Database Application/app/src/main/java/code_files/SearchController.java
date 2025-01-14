package code_files;

public class SearchController {

    // when the SearchQuery (movie is typed in search bar) is made, the SearchQuery
    // class is called and the result is passes to APIRequestWrapper class which
    // uses the object created to make an API request and at the same time look at
    // what movie information is stored on the Disk. The return is SearchResult type
    // as it contains the list of Movie objects
    public static SearchResult getResult(SearchQuery sq) {

        SearchResult sr = APIRequestWrapper.relatedMovies(sq);
        return sr;
    }

}
