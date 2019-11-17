public class Edge{

    // PROPERTIES

    public GeoEntity a, b;
    private float mmJoinability;
    private float nonMMJoinability;

    // METHODS

    public PrecinctCluster generateCluster(){
        return null;
    }

    // ACCESSORS AND MUTATORS

    public GeoEntity[] getClusters(){
        GeoEntity[] endpts = {a,b};
        return endpts;
    }

    public float getMMJoinability(){
        return mmJoinability;
    }

    public float getNonMMJoinability(){
        return nonMMJoinability;
    }

}