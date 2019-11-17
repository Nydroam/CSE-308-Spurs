import java.util.HashMap;

public class Election{

    //PRECINCTS
    public int precinctID;
    public int year;
    public ElectionID type;
    public Party winningParty;
    public HashMap<Party, Integer> votesByParty;

    //METHODS



    //ACCESSORS/MUTATORS
    //returns the total number of votes
    public Integer getVotes(){
        return 0;
    }

    public Integer getVotes(Party party){
        return votesByParty.get(party);
    }
}