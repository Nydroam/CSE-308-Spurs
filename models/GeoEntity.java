import java.util.List;

enum Party {
    DEMOCRAT, REPUBLICAN, OTHER;
}

enum Race {
    ASIAN, BLACK, WHITE, NHPI, AMIN, HISP;
}

enum ElectionID {
    PRESIDENTIAL2016, SENATE2014, SENATE2016, SENATE2018, GUBERNATORIAL2014, GUBERNATORIAL2018;
}


interface GeoEntity {
    public List<Edge> getNeighbors();
    public long getPopulation();
    public long getPopulation(Race r);
    public long getNumVoters(ElectionID election);
    public long getNumVoters(ElectionID election, Party p);
    public float getCompactnessScore();
    public Geometry getGeometry();
}