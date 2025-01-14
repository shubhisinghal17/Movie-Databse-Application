package code_files;

public class TailoredList extends MovieList {
    private String listTitle;

    public TailoredList() {
    }

    public TailoredList(String listTitle) {
        super();
        this.listTitle = listTitle;

    }


    public String getListTitle() {
        return listTitle;
    }

    public void setListTitle(String listTitle) {
        this.listTitle = listTitle;
    }


    public boolean updateTailoredList(Movie movieToBeAdded, TailoredList tailoredlist) {
        // Check if the movie is already in the tailored list
        return tailoredlist.addMovie(movieToBeAdded);

    }

    public void updateGlobalList(GlobalList currentGlobalList, TailoredList tailoredlist) {
        boolean listUpdated = false;
        
        // Iterate through the global list of tailored lists
        for (int i = 0; i < currentGlobalList.getGlobalListOFTailoredLists().size(); i++) {
            TailoredList list = currentGlobalList.getGlobalListOFTailoredLists().get(i);
            
            // If a matching list is found, update it
            if (list.getListTitle().equals(tailoredlist.getListTitle())) {
                currentGlobalList.getGlobalListOFTailoredLists().set(i, tailoredlist); // Replace the old list
                listUpdated = true; // Indicate that the list was updated
                break;
            }
        }
    
        // If no matching list was found, add the tailored list to the global list
        if (!listUpdated) {
            currentGlobalList.getGlobalListOFTailoredLists().add(tailoredlist);
        }
    }
    
    
    
    
}
