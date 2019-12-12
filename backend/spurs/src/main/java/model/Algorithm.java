package model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lineitem.BlocLineItem;
import model.Election.ElectionType;
import model.Election.Race;

public class Algorithm {
	private State state;
    private Set<PrecinctCluster> precinctClusters;

    public Algorithm(State state) {
    	this.state = state;
    	precinctClusters = state.initializePrecinctClusters();
    }
    
    public List<BlocLineItem> runPhase0(ElectionType electionType, float voteThresh, float raceThresh) {
    	return state.isVotingAsBloc(electionType, voteThresh, raceThresh);
    }
    
    public Set<PrecinctCluster> runPhase1(List<Race> races, float rangeMin, float rangeMax){
    	Set<PrecinctCluster> newClusters = new HashSet<PrecinctCluster>();
    	Set<PrecinctCluster> pickedClusters = new HashSet<PrecinctCluster>();
    	int size = precinctClusters.size();
    	
    	while (size > state.getDistricts().size()) {
    		int numP = precinctClusters.stream().mapToInt(p -> p.getPrecincts().size()).sum();
    		System.out.println(numP);
    		int count = 0;
    		for (PrecinctCluster pc1: precinctClusters) {
    			if (pickedClusters.contains(pc1)) {
    				continue;
    			}
    			PrecinctClusterEdge pickedEdge = null;
    			float currMaxJoinability = 0;
    			for (PrecinctClusterEdge pcEdge: pc1.getExteriorEdges()) {
    				float joinability = (pcEdge.calculateMMJoinability(races, rangeMin, rangeMax) + pcEdge.calculateNonMMJoinability()) / 2;
    				if (joinability > currMaxJoinability && !pickedClusters.contains(pcEdge.getOtherEndpoint(pc1))) {
    					pickedEdge = pcEdge;
    					currMaxJoinability = joinability; 
    				}
    			}
    			if (pickedEdge == null) {
    				count++;
    			}
    			if (pickedEdge != null) {
    				pickedClusters.add(pc1);
    				pickedClusters.add(pickedEdge.getOtherEndpoint(pc1));
    				newClusters.add(pickedEdge.generatePrecinctCluster());
    			}
    			if (newClusters.size() == state.getDistricts().size() && newClusters.size() > precinctClusters.size() / 2) {
    				return newClusters;
    			}
    		}
    		Set<PrecinctCluster> leftovers = new HashSet<PrecinctCluster>();
    		for (PrecinctCluster pc: precinctClusters) {
    			if (!pickedClusters.contains(pc)) {
    				leftovers.add(pc);
    			}
    		}
    		precinctClusters = new HashSet<PrecinctCluster>();
    		precinctClusters.addAll(newClusters);
    		precinctClusters.addAll(leftovers);
    		size = precinctClusters.size();
    		newClusters = new HashSet<PrecinctCluster>();
    		pickedClusters = new HashSet<PrecinctCluster>();
    	}
    	return precinctClusters;
    }
}
