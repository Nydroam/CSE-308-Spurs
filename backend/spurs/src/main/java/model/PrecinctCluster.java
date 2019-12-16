package model;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.annotations.Expose;

import model.Election.ElectionType;
import model.Election.Party;
import model.Election.Race;

public class PrecinctCluster implements Comparable<PrecinctCluster>{

	@Expose
	private long id;
	@Expose
	private Set<Precinct> precincts;
    private Set<PrecinctEdge> interiorEdges;
    @Expose
    private Set<PrecinctClusterEdge> exteriorEdges;
    @Expose
    private Map<Race, Long> populationByRace;
    private float cumulativeMMJoinability;
    private float cumulativeNonMMJoinability;
    private Map<String, Integer> countyTally;
    @Expose
    private long population;
    @Expose
    private long mmPopulation;
    private int repVotes;
    private int demVotes;
    private float area;
    private float perimeter;
    
    public float sharedPerimeter;
    
    public PrecinctCluster() {
    	precincts = new HashSet<Precinct>();
    	interiorEdges = new HashSet<PrecinctEdge>();
    	exteriorEdges = new HashSet<PrecinctClusterEdge>();
    	populationByRace = new HashMap<Race, Long>();
    	countyTally = new HashMap<String, Integer>();
    }

    public PrecinctCluster(Precinct precinct) {
    	this();
    	this.id = precinct.getId();
    	precincts.add(precinct);
    	setPopulation(precinct.getPopulation());
    	for (Demographic d: precinct.getDemographics()) {
    		populationByRace.put(d.getRace(), d.getPopulation());
    	}
    	setRepVotes(precinct.getVotingData(ElectionType.PRES16).getVotesByPartyAsMap().get(Party.REPUBLICAN));
    	setDemVotes(precinct.getVotingData(ElectionType.PRES16).getVotesByPartyAsMap().get(Party.DEMOCRAT));
    	countyTally.put(precinct.getCounty(), 1);
    }
    
    public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
    
    public Set<Precinct> getPrecincts(){
        return precincts;
    }
    
	public void setPrecincts(Set<Precinct> precincts) {
    	this.precincts = precincts;
    }
    
    public Set<PrecinctEdge> getInteriorEdges(){
        return interiorEdges;
    }
    
    public void setInteriorEdges(Set<PrecinctEdge> interiorEdges) {
    	this.interiorEdges = interiorEdges;
    }
    
    public Set<PrecinctClusterEdge> getExteriorEdges() {
		return exteriorEdges;
	}

	public void setExteriorEdges(Set<PrecinctClusterEdge> exteriorEdges) {
		this.exteriorEdges = exteriorEdges;
	}
    
    public float getCumulativeMMJoinability(List<Race> races, float rangeMin, float rangeMax) {
    	float cumMMJoin = 0;
		for (Precinct p: precincts) {
			cumMMJoin += p.getMMScore(races, rangeMin, rangeMax);
		}
		return cumMMJoin / precincts.size();
	}

	public void setCumulativeMMJoinability(float cumulativeMMJoinability) {
		this.cumulativeMMJoinability = cumulativeMMJoinability;
	}

	public float getCumulativeNonMMJoinability() {
		return cumulativeNonMMJoinability;
	}

	public void setCumulativeNonMMJoinability(float cumulativeNonMMJoinability) {
		this.cumulativeNonMMJoinability = cumulativeNonMMJoinability;
	}
    
    public long getPopulation(Race r){
        return populationByRace.get(r);
    }
    
    public long getNumVoters(ElectionType election){
        long votes = 0;
        for (Precinct precinct: precincts) {
            votes += precinct.getNumVoters(election);
        }
        return votes;
    }
    
    public long getNumVoters(ElectionType election, Party p){
        long votes = 0;
        for (Precinct precinct: precincts){
            votes += precinct.getNumVoters(election, p);
        }
        return votes;
    }
    
    public float getCompactnessScore(){
        float area = 0;
        float perimeter = 0;
        float sharedPerimeter = 0;
        for (Precinct precinct: precincts){
            area += precinct.getArea();
            perimeter += precinct.getPerimeter();
        }
        for (PrecinctEdge edge: interiorEdges) {
        	sharedPerimeter += edge.getSharedPerimeter();
        }
        perimeter -= sharedPerimeter;
        float circlePerimeter = (float)(Math.sqrt((area/Math.PI)) * 2 * Math.PI);
        return circlePerimeter / perimeter;
    }
    
    public Set<Precinct> getBorderPrecincts(){
    	Set<Precinct> borderPrecincts = new HashSet<Precinct>();
    	for (Precinct precinct: precincts) {
    		for (PrecinctEdge adjacentEdge: precinct.getAdjacentEdges()) {
    			Precinct other = adjacentEdge.getOtherEndpoint(precinct);
    			if (!precincts.contains(other)) {
    				borderPrecincts.add(other);
    			}
    		}
    	}
    	return borderPrecincts;
    }
    
    public long getPopulation() {
    	return population;
    }
    
    public void setPopulation(long population) {
		this.population = population;
	}
    
    public Map<Race, Long> getPopulationByRace(){
    	return populationByRace;
    }
    
    public void setPopulationByRace(Map<Race, Long> populationByRace) {
    	this.populationByRace = populationByRace;
    }

	public long getMmPopulation() {
		return mmPopulation;
	}

	public void setMmPopulation(long mmPopulation) {
		this.mmPopulation = mmPopulation;
	}

	public int getRepVotes() {
		return repVotes;
	}

	public void setRepVotes(int repVotes) {
		this.repVotes = repVotes;
	}

	public int getDemVotes() {
		return demVotes;
	}

	public void setDemVotes(int demVotes) {
		this.demVotes = demVotes;
	}

	public float getArea() {
		return area;
	}

	public void setArea(float area) {
		this.area = area;
	}

	public float getPerimeter() {
		return perimeter;
	}

	public void setPerimeter(float perimeter) {
		this.perimeter = perimeter;
	}

	public Map<String, Integer> getCountyTally(){
		return countyTally;
	}
	
	public void setCountyTally(Map<String, Integer> countyTally) {
		this.countyTally = countyTally;
	}
	
	public boolean equals(Object precinctCluster) {
    	return ((PrecinctCluster) precinctCluster).getId() == id;
    }
    
    public int hashCode() {
       	return (int)id;
    }
    
    public int compareTo(PrecinctCluster other) {
    	if (this.getExteriorEdges().size() > 0 && other.getExteriorEdges().size() > 0)
    		return Collections.max(this.getExteriorEdges()).compareTo(Collections.max(other.getExteriorEdges()));
    	else if (this.getExteriorEdges().size() == 0) {
    		return -1;
    	}else if (other.getExteriorEdges().size() == 0){
    		return 1;
    	}else {
    		return 0;
    	}
    }
}
