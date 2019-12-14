package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Election.Race;

public class PrecinctClusterEdge {

	private PrecinctCluster endpoint1;
	private PrecinctCluster endpoint2;
	private float mmJoinability;
	private float nonMMJoinability;

	public PrecinctClusterEdge() {
	}

	public PrecinctClusterEdge(PrecinctCluster endpoint1, PrecinctCluster endpoint2) {
		this.endpoint1 = endpoint1;
		this.endpoint2 = endpoint2;
		this.nonMMJoinability = calculateNonMMJoinability();
	}

	public List<PrecinctCluster> getEndPoints() {
		List<PrecinctCluster> l = new ArrayList<PrecinctCluster>();
		l.add(endpoint1);
		l.add(endpoint2);
		return l;
	}

	public void setEndpoints(PrecinctCluster a, PrecinctCluster b) {
		setEndpoint1(a);
		setEndpoint2(b);
	}

	public PrecinctCluster getEndpoint1() {
		return endpoint1;
	}

	public void setEndpoint1(PrecinctCluster endpoint1) {
		this.endpoint1 = endpoint1;
	}

	public PrecinctCluster getEndpoint2() {
		return endpoint2;
	}

	public void setEndpoint2(PrecinctCluster endpoint2) {
		this.endpoint2 = endpoint2;
	}

	public PrecinctCluster getOtherEndpoint(PrecinctCluster precinctCluster) {
		if (precinctCluster.equals(endpoint1)) {
			return endpoint2;
		}
		return endpoint1;
	}

	public float getMMJoinability() {
		return mmJoinability;
	}

	public void setMMJoinability(float mmJoinability) {
		this.mmJoinability = mmJoinability;
	}

	public float getNonMMJoinability() {
		return nonMMJoinability;
	}

	public void setNonMMJoinability(float nonMMJoinability) {
		this.nonMMJoinability = nonMMJoinability;
	}

	public float calculateMMJoinability(List<Race> races, float rangeMin, float rangeMax) {
		float pc1Joinability = endpoint1.getCumulativeMMJoinability(races, rangeMin, rangeMax);
		float pc2Joinability = endpoint2.getCumulativeMMJoinability(races, rangeMin, rangeMax);

		this.mmJoinability = (pc1Joinability * endpoint1.getPrecincts().size()
				+ pc2Joinability * endpoint2.getPrecincts().size())
				/ (endpoint1.getPrecincts().size() + endpoint2.getPrecincts().size());
		return mmJoinability;
	}

	public float calculateNonMMJoinability() {
		
		
		
		this.nonMMJoinability = (calculateCompactnessScore() + calculateCountyScore() + calculateFairnessScore()
				+ calculatePopulationScore()) / 8;
		if(nonMMJoinability<0) {
//		System.out.println("Compact: " + calculateCompactnessScore());
//		System.out.println("County: " + calculateCountyScore());
//		System.out.println("Fairness: " + calculateFairnessScore());
//		System.out.println("Population: " + calculatePopulationScore());
		}
		return nonMMJoinability;
	}

	public PrecinctCluster generatePrecinctCluster(PrecinctCluster eater) {
    	//PrecinctCluster cluster = new PrecinctCluster();
		PrecinctCluster other = null;
    	if (eater.equals(endpoint1)) {
    		eater = endpoint1;
    		other = endpoint2;
    	}else {
    		eater = endpoint2;
    		other = endpoint1;
    	}
    	Set<Precinct> newPrecincts = new HashSet<Precinct>();
    	newPrecincts.addAll(eater.getPrecincts());
    	newPrecincts.addAll(other.getPrecincts());
    	eater.setPrecincts(newPrecincts);
    	
    	eater.setPopulation(eater.getPopulation() + other.getPopulation());
    	eater.setCumulativeMMJoinability(mmJoinability);
    	Map<Race, Long> newPopulationByRace = new HashMap<Race, Long>();
    	for (Race r: eater.getPopulationByRace().keySet()) {
    		newPopulationByRace.put(r, eater.getPopulation(r) + other.getPopulation(r));
    	}
    	eater.setPopulationByRace(newPopulationByRace);
    	eater.setRepVotes(eater.getRepVotes() + other.getRepVotes());
    	eater.setDemVotes(eater.getDemVotes() + other.getDemVotes());
    	Map<String, Integer> newCountyTally = combineCountyTally();
    	eater.setCountyTally(newCountyTally);
    	Set<PrecinctEdge> newInteriorEdges = new HashSet<PrecinctEdge>();
    	newInteriorEdges.addAll(eater.getInteriorEdges());
    	newInteriorEdges.addAll(other.getInteriorEdges());
    	eater.setInteriorEdges(newInteriorEdges);
    	/*
    	Set<PrecinctClusterEdge> newExteriorEdges = new HashSet<PrecinctClusterEdge>();
    	Set<PrecinctCluster> neighbors = new HashSet<PrecinctCluster>();
    	for (PrecinctClusterEdge edge: endpoint1.getExteriorEdges()) {
    		PrecinctCluster other = edge.getOtherEndpoint(endpoint1);
    		if (!neighbors.contains(other) && !other.equals(endpoint2)) {
    			newExteriorEdges.add(new PrecinctClusterEdge(cluster, other));
    			neighbors.add(other);
    		}
    	}
    	for (PrecinctClusterEdge edge: endpoint2.getExteriorEdges()) {
    		PrecinctCluster other = edge.getOtherEndpoint(endpoint2);
    		if (!neighbors.contains(other) && !other.equals(endpoint1)) {
    			newExteriorEdges.add(new PrecinctClusterEdge(cluster, other));
    			neighbors.add(other);
    		}
    	}
    	cluster.setExteriorEdges(newExteriorEdges);
    	*/
    	return eater;
    }

	public float calculateCompactnessScore() {
		float area = 0;
		float perimeter = 0;
		float sharedPerimeter = 0;

		Set<Precinct> precincts = new HashSet<Precinct>();
		precincts.addAll(endpoint1.getPrecincts());
		precincts.addAll(endpoint2.getPrecincts());
		for (Precinct precinct : precincts) {
			area += precinct.getArea();
			perimeter += precinct.getPerimeter();
		}
		Set<PrecinctEdge> interiorEdges = new HashSet<PrecinctEdge>();
		interiorEdges.addAll(endpoint1.getInteriorEdges());
		interiorEdges.addAll(endpoint2.getInteriorEdges());
		for (PrecinctEdge edge : interiorEdges) {
			sharedPerimeter += edge.getSharedPerimeter();
		}
		perimeter -= sharedPerimeter;
		float circlePerimeter = (float) (Math.sqrt((area / Math.PI)) * 2 * Math.PI);
		return circlePerimeter / perimeter;
	}

	public float calculateCountyScore() {
		int mode = 0;
		int total = 0;
		Map<String, Integer> newCountyTally = endpoint1.getCountyTally();
		for (Integer i : newCountyTally.values()) {
			if (i > mode) {
				mode = i;
			}
			total += i;
		}

		return (float) mode / total;
	}

	public float calculateFairnessScore() {

		float repVote1 = (float) endpoint1.getRepVotes() / (endpoint1.getDemVotes() + endpoint1.getRepVotes());
		float repVote2 = (float) endpoint2.getRepVotes() / (endpoint2.getDemVotes() + endpoint2.getRepVotes());
		return 1 - Math.abs(repVote1 - repVote2);
	}

	public float calculatePopulationScore() {
		float avgPop1 = endpoint1.getPopulation() / endpoint1.getPrecincts().size();
		float avgPop2 = endpoint2.getPopulation() / endpoint2.getPrecincts().size();

		return (float) (Math.abs(endpoint1.getPopulation() + endpoint2.getPopulation()) / Math.pow(endpoint1.getPopulation() + endpoint2.getPopulation(),2 ));
	}

	private Map<String, Integer> combineCountyTally() {
		Map<String, Integer> newCountyTally = new HashMap<String, Integer>();

		for (String county : endpoint1.getCountyTally().keySet()) {
			if (newCountyTally.containsKey(county)) {
				newCountyTally.put(county, newCountyTally.get(county) + endpoint1.getCountyTally().get(county));
			} else {
				newCountyTally.put(county, endpoint1.getCountyTally().get(county));
			}
		}
		for (String county : endpoint2.getCountyTally().keySet()) {
			if (newCountyTally.containsKey(county)) {
				newCountyTally.put(county, newCountyTally.get(county) + endpoint2.getCountyTally().get(county));
			} else {
				newCountyTally.put(county, endpoint2.getCountyTally().get(county));
			}
		}
		return newCountyTally;
	}

	public boolean equals(Object other) {
		return (other instanceof PrecinctClusterEdge) && (((PrecinctClusterEdge) other).getEndpoint1().equals(this.endpoint1) && ((PrecinctClusterEdge) other).getEndpoint2().equals(this.endpoint2))
				|| (((PrecinctClusterEdge) other).getEndpoint2().equals(this.endpoint1) && ((PrecinctClusterEdge) other).getEndpoint1().equals(this.endpoint2));
	}

	public int hashCode() {
		return (int)Math.floor((Math.pow(this.endpoint1.getId(), 2)) + Math.pow(this.endpoint2.getId(),2));
	}
}
