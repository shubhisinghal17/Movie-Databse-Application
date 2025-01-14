// package code_files;

// public class Main {
//     public static void main(String[] args) throws Exception {

//         User user = new User().doLogin("lijuan", "lijuan");
//         SearchQuery searchquery = new SearchQuery("inception"); // search bar
//         SearchResult searchresult = SearchController.getResult(searchquery);

//         Movie movieWithReview = searchresult.getMovies().get(1);
//         String reviewTextField = "review 1 by lijuan";
//         Integer ratingTextField = 8;
//         Review reviewToBeAdded = new Review(movieWithReview.getMovieID(), reviewTextField, user.getUserName(),
//                 ratingTextField);
//         movieWithReview.addOrUpdateReview(reviewToBeAdded);

//         Movie movieWithoutReview = searchresult.getMovies().get(2);
//         user.addMovieToLibrary(movieWithReview);
//         user.removeMovieFromLibrary(movieWithReview);

//         SearchResult recommMovies = user.getLibrary().getRecommendedMovies(); // get movie recommendations
//         // System.out.println(recommMovies);
//         SearchResult filtedMovies = user.getLibrary().getFilteredRecommendedMovies("Comedy", recommMovies);
//         // System.out.println(filtedMovies);

//         Movie movie1 = searchresult.getMovies().get(2);

//         Movie movie2 = searchresult.getMovies().get(3);
//         user.addMovieToWatchlist(movie1);
//         user.removeMovieFromWatchlist(movieWithReview);

//         // called when user wants to create a new list in global list
//         TailoredList newTailoredList = new TailoredList("List2");

//         // Wne user clicks on button called GlobalLists
//         GlobalList globalListVisibleToEveryOne = DiskManager.readListOFTailoredListsFromDisk();
//         globalListVisibleToEveryOne.addTailoredList(newTailoredList, globalListVisibleToEveryOne);

//         GlobalList currentGlobalList = DiskManager.readListOFTailoredListsFromDisk();
//         TailoredList chosenList = currentGlobalList.getGlobalListOFTailoredLists().get(1);
//         chosenList.addMovie(movie1, currentGlobalList);

//     }
// }