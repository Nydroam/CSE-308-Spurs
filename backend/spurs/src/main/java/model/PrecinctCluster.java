package model;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import model.Election.ElectionType;
import model.Election.Party;
import model.Election.Race;

public class PrecinctCluster extends GeoEntity{
	
	@OneToMany(targetEntity=Precinct.class, mappedBy="district", cascade=CascadeType.ALL)
	private Set<Precinct> precincts;
	@ManyToMany(targetEntity=Edge.class)
    private Set<Edge> interiorEdges;
    private float cumulativeMMJoinability;
    private float cumulativeNonMMJoinability;
    
    public PrecinctCluster() {
    }

    @ManyToMany
    public Set<Edge> getInteriorEdges(){
        return interiorEdges;
    }
    
    public void setInteriorEdges(Set<Edge> interiorEdges) {
    	this.interiorEdges = interiorEdges;
    }
    
    public Set<Precinct> getPrecincts(){
        return precincts;
    }
    
    public void setPrecincts(Set<Precinct> precincts) {
    	this.precincts = precincts;
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

	@Transient
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
    
    @Transient
    public float getCompactnessScore(){
        float total = 0;
        for (Precinct precinct: precincts){
            total += precinct.getCompactnessScore();
        }
        return total/precincts.size();
    }
    
    public Geometry getGeometry(){
        return null;
    }
}
