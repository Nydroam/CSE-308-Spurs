package model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import lineitem.BlocLineItem;
import model.Election.ElectionType;
import model.Election.Race;

public class Algorithm {
	private State state;
    private Set<PrecinctCluster> precinctClusters;
    private Set<PrecinctCluster> mergedClusters;
    private boolean finishedPhase1;

    public Algorithm(State state) {
    	this.state = state;
    	precinctClusters = state.initializePrecinctClusters();
    	mergedClusters = null;
    	finishedPhase1 = false;
    }
    
    public List<BlocLineItem> runPhase0(ElectionType electionType, float voteThresh, float raceThresh) {
    	return state.isVotingAsBloc(electionType, voteThresh, raceThresh);
    }
    
    public Set<PrecinctCluster> runPhase1Step(List<Race> races, float rangeMin, float rangeMax, int distNum){
    	if(mergedClusters == null) {
    		mergedClusters = new HashSet<PrecinctCluster>();
        	mergedClusters.addAll(precinctClusters);
    	}
    		
    	System.out.println("SIZE: " + mergedClusters.size());
		float maxJoinability = -10000;
		PrecinctClusterEdge pickedEdge = null;
		PrecinctCluster A = null;
		for (PrecinctCluster pc : mergedClusters) {//loop through all precinct clusters for highest joinability
			for (PrecinctClusterEdge edge : pc.getExteriorEdges()) {
				float mmj = edge.calculateMMJoinability(races, rangeMin, rangeMax);
				float mmnj = edge.calculateNonMMJoinability();
				float joinability = (mmj + mmnj)/2;
				if(joinability<0) {
					System.out.println("JOIN: "+ joinability + " " + mmj + " + " + mmnj);
				}
				if (joinability >= maxJoinability) {
					pickedEdge = edge;
					A = pc;
					maxJoinability = joinability;
				}
			}
		}
		if(pickedEdge == null) {
			finishedPhase1 = true;
			return mergedClusters;
		}else {
			PrecinctCluster B = pickedEdge.getOtherEndpoint(A);
			A = pickedEdge.generatePrecinctCluster(A);
			
			Set<PrecinctClusterEdge> AExterior = A.getExteriorEdges();
			Set<PrecinctClusterEdge> BExterior = B.getExteriorEdges();
			
			//Remove intersection of A and B
			AExterior.remove(pickedEdge);
			BExterior.remove(pickedEdge);
			
			//Making all points from B to C now A to C
			Set<PrecinctCluster> other = new HashSet<PrecinctCluster>();
			for (PrecinctClusterEdge e:BExterior) {
				other.add(e.getOtherEndpoint(B));
				e.setEndpoints(A, e.getOtherEndpoint(B));
			}
			//Making all points from C to B now C to A
			for (PrecinctCluster c : other) {
				Set<PrecinctClusterEdge> newEdges = new HashSet<PrecinctClusterEdge>();
				for(PrecinctClusterEdge edge : c.getExteriorEdges()) {
					PrecinctClusterEdge next = new PrecinctClusterEdge(edge.getEndpoint1(),edge.getEndpoint2());
					if(edge.getOtherEndpoint(c).equals(B)) {
						next.setEndpoints(c, A);
					}
					newEdges.add(next);
				}
				c.setExteriorEdges(newEdges);
			}
			
			//Union A and B into A and remove B from clusters
			AExterior.addAll(BExterior);
			mergedClusters.remove(B);
		}
		if(mergedClusters.size() <= distNum)
			finishedPhase1 = true;
		return mergedClusters;
    }
    
    public boolean finishedPhase1() {
    	return finishedPhase1;
    }
    public void resetClusters() {
    	mergedClusters = null;
    	finishedPhase1 = false;
    }
    public Set<PrecinctCluster> runPhase1(List<Race> races, float rangeMin, float rangeMax, int distNum){
    	mergedClusters = new HashSet<PrecinctCluster>();
    	mergedClusters.addAll(precinctClusters);
    	while(mergedClusters.size() > distNum) {
    		int size = mergedClusters.size();
    		runPhase1Step(races, rangeMin, rangeMax, distNum);
    		if (size == mergedClusters.size())
    			break;
    	}
    	return mergedClusters;
    }
}
