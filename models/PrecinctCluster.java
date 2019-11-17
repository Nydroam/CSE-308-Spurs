import java.util.List;
import java.util.Set;

public class PrecinctCluster implements GeoEntity{

    public List<Precinct> precincts;
    public Set<Edge> interiorEdges;
    public List<Edge> outgoingEdges;
    public float cumulativeMMJoinability;
    public float cumulativeNonMMJoinability;

    // METHODS

    //ACCESSORS/MUTATORS

    public List<Edge> getEdges(){
        return outgoingEdges;
    }
    public List<Precinct> getPrecincts(){
        return precincts;
    }

    public List<Edge> getNeighbors(){
        return outgoingEdges;
    }
    public long getPopulation(){
        long population = 0;
        for (int i=0; i<precincts.size(); i++){
            population += precincts.get(i).getPopulation();
        }
        return population;
    }
    public long getPopulation(Race r){
        long population = 0;
        for (int i=0; i<precincts.size(); i++){
            population += precincts.get(i).getPopulation(r);
        }
        return population;
    }
    public long getNumVoters(ElectionID election){
        long votes = 0;
        for (int i=0; i<precincts.size(); i++){
            votes += precincts.get(i).getNumVoters(election);
        }
        return votes;
    }
    public long getNumVoters(ElectionID election, Party p){
        long votes = 0;
        for (int i=0; i<precincts.size(); i++){
            votes += precincts.get(i).getNumVoters(election, p);
        }
        return votes;
    }
    public float getCompactnessScore(){
        float total = 0;
        for (int i=0; i<precincts.size(); i++){
            total += precincts.get(i).getCompactnessScore();
        }
        return total/precincts.size();
    }
    public Geometry getGeometry(){
        return null;
    }
}
