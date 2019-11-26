package model;

import java.util.List;
import java.util.Set;

import model.Election.ElectionType;
import model.Election.Race;

public class Algorithm {
	private State state;
    private Set<PrecinctCluster> precinctClusters;

    public Algorithm(State state) {
    	this.state = state;
    	precinctClusters = state.initializePrecinctClusters();
    }
    
    public List<Demographic> runPhase0(ElectionType electionType, float voteThresh, float raceThresh) {
    	return state.isVotingAsBloc(electionType, voteThresh, raceThresh);
    }
    
    public Set<PrecinctCluster> runPhase1(float rangeMin, float rangeMax, List<Race> races){
    	while (precinctClusters.size() > state.getDistricts().size()) {
    		for (PrecinctCluster pc1: precinctClusters) {
    			PrecinctCluster bestPc2;
    			float currMaxJoinability = 0;
    			for (PrecinctClusterEdge pcEdge: pc1.getExteriorEdges()) {
    				List<PrecinctCluster> endpoints = pcEdge.getEndPoints();
    				PrecinctCluster pc2 = pcEdge.getOtherEndpoint(pc1);
    			}
    		}
    	}
    	return null;
    }
}
