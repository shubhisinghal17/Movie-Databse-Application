package code_files;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//class of a list which is a list of globally visible movie lists. Eg: IMDB's Top 100 movies of all time. Every user can create their own list in the Global List and can add their movies 
public class GlobalList {
    private List<TailoredList> listOfTailoredLists;

    public GlobalList(List<TailoredList> listOfTailoredLists) {
        this.listOfTailoredLists = listOfTailoredLists;
    }

    // for when there mo list exists in Global List
    public GlobalList() {
        this.listOfTailoredLists = new ArrayList<>();
    }

    public List<TailoredList> getGlobalListOFTailoredLists() {
        return listOfTailoredLists;
    }

    public void setListOfTailoredLists(List<TailoredList> listOfTailoredLists) {
        this.listOfTailoredLists = listOfTailoredLists;
    }

    // user accesses this function to add a list to global list.Eg: I could add a
    // taylored list like "Chuckle Worthy Movies"
    public void addTailoredList(TailoredList tailoredListToBeAdded, GlobalList currentGlobalList)
            throws IOException {
        boolean sameTitleExists = false;
        for (TailoredList tailoredList : currentGlobalList.getGlobalListOFTailoredLists()) {
            if (tailoredListToBeAdded.getListTitle().equals(tailoredList.getListTitle())) {
                sameTitleExists = true;
            }
        }
        if (!sameTitleExists) {
            listOfTailoredLists.add(tailoredListToBeAdded);
            DiskManager.writeGlobalListOFTailoredListsToDisk(currentGlobalList);
        }

    }

}
