import java.util.Set;

enum StateID {
    CALIFORNIA, RHODEISLAND, PENNSYLVANIA;
}

public class State extends PrecinctCluster {

    // PROPERTIES

    public int geoID;
    public StateID stateID;
    public Geometry geometry;
    public Set<District> districts;
    public Set<Precinct> PRECINCTS;

    // METHODS

    // ACCESSORS/MUTATORS
}