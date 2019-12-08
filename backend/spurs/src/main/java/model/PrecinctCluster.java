package model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import model.Election.ElectionType;
import model.Election.Party;
import model.Election.Race;

public class PrecinctCluster {

	private Set<Precinct> precincts;
    private Set<PrecinctEdge> interiorEdges;
    private Set<PrecinctClusterEdge> exteriorEdges;
    private float cumulativeMMJoinability;
    private float cumulativeNonMMJoinability;
    private Coordinate averageCoordinate;
    private HashMap<String, Integer> countyTally;
    
    public PrecinctCluster() {
    	precincts = new HashSet<Precinct>();
    	interiorEdges = new HashSet<PrecinctEdge>();
    	exteriorEdges = new HashSet<PrecinctClusterEdge>();
    }

    public PrecinctCluster(Precinct precinct) {
    	this();
    	precincts.add(precinct);
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
    
    public float getCumulativeMMJoinability() {
		return cumulativeMMJoinability;
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

    public long getPopulation(){
        long population = 0;
        for (Precinct precinct: precincts) {
            population += precinct.getPopulation();
        }
        return population;
    }
    
    public long getPopulation(Race r){
        long population = 0;
        for (Precinct precinct: precincts) {
            population += precinct.getPopulation(r);
        }
        return population;
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
        float total = 0;
        for (Precinct precinct: precincts){
            total += precinct.getCompactnessScore();
        }
        return total/precincts.size();
    }
}
