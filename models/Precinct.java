import java.util.List;
import java.util.HashMap;

public class Precinct implements GeoEntity{

    // PROPERTIES

    public int geoID;
    public HashMap<ElectionID, Election> elections;
    public District district;
    public State state;
    public Geometry geometry;
    public List<Edge> neighbors;
    public float compactnessScore;

    public int population;
    public HashMap<Race, Integer> populationByRace;

    // CONSTRUCTORS

    public Precinct(int geoID){
        this.geoID = geoID;
    }

    // METHODS

    public boolean isBloc(ElectionID electype, List<Race> minorities, float voteThresh, float raceThresh){
        Election election = elections.get(electype);
        for (int i=0; i < minorities.size(); i++){
        }
        return false;
    }

    public Race getBloc(){
        return null;
    }


    // ACCESSORS / MUTATORS

    public Election getVotingData(ElectionID type){
        return null;
    }

    public List<Edge> getNeighbors(){
        return neighbors;
    }
    public long getPopulation(){
        return population;
    }
    public long getPopulation(Race r){
        return populationByRace.get(r);
    }
    public long getNumVoters(ElectionID election){
        return elections.get(election).getVotes();
    }
    public long getNumVoters(ElectionID election, Party p){
        return elections.get(election).getVotes(p);
    }
    public float getCompactnessScore(){
        return compactnessScore;
    }
    public Geometry getGeometry(){
        return geometry;
    }
}