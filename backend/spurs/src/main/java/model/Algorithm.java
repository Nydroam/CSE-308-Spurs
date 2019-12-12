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
    	
    	Map<PrecinctCluster, PrecinctCluster> clusterMap = new HashMap<PrecinctCluster, PrecinctCluster>();
    	for (PrecinctCluster p: precinctClusters) {
    		clusterMap.put(p, p);
    	}
    	Queue<PrecinctCluster> clusterQueue = new LinkedList<PrecinctCluster>();
    	for (PrecinctCluster p: clusterMap.keySet()) {
    		clusterQueue.add(clusterMap.get(p));
    	}
    	while (precinctClusters.size() > state.getDistricts().size()) {
    		//System.out.println(precinctClusters.size());
    		if (clusterQueue.isEmpty()) {
    			precinctClusters = new HashSet<PrecinctCluster>();
    			precinctClusters.addAll(clusterMap.values());
    			clusterMap = new HashMap<PrecinctCluster, PrecinctCluster>();
    			for (PrecinctCluster p: precinctClusters) {
    	    		clusterMap.put(p, p);
    	    	}
    			clusterQueue = new LinkedList<PrecinctCluster>();
    			for (PrecinctCluster p: clusterMap.keySet()) {
    	    		clusterQueue.add(clusterMap.get(p));
    	    	}
    			//newClusters = new HashSet<PrecinctCluster>();
        		pickedClusters = new HashSet<PrecinctCluster>();
    		}
    		PrecinctCluster pc1 = clusterQueue.poll();
    		if (pc1 == null) {
    			System.out.println();
    		}
			if (pickedClusters.contains(pc1)) {
				continue;
			}
			PrecinctClusterEdge pickedEdge = null;
			float currMaxJoinability = 0;
			for (PrecinctClusterEdge pcEdge: pc1.getExteriorEdges()) {
				float joinability = (pcEdge.calculateMMJoinability(races, rangeMin, rangeMax) + pcEdge.calculateNonMMJoinability()) / 2;
				if (joinability >= currMaxJoinability && !pickedClusters.contains(pcEdge.getOtherEndpoint(pc1))) {
					pickedEdge = pcEdge;
					currMaxJoinability = joinability; 
				}
			}
			if (pickedEdge != null) {
				PrecinctCluster other = pickedEdge.getOtherEndpoint(pc1);
				pickedClusters.add(pc1);
				pickedClusters.add(other);
				PrecinctCluster newCluster = pickedEdge.generatePrecinctCluster(pc1);
				//newClusters.add(newCluster);
				
				Set<PrecinctClusterEdge> pc1ExteriorEdges = newCluster.getExteriorEdges();
				pc1ExteriorEdges.remove(pickedEdge);
				Set<PrecinctClusterEdge> otherExteriorEdges = other.getExteriorEdges();
				PrecinctClusterEdge oe = null;
				for (PrecinctClusterEdge e: otherExteriorEdges) {
					if (e.equals(pickedEdge)) {
						oe = e;
					}
				}
				otherExteriorEdges.remove(oe);
				for (PrecinctClusterEdge pce: otherExteriorEdges) {
					if (pce.getOtherEndpoint(other).equals(newCluster)) {
						continue;
					}
					if (clusterMap.get(pce.getOtherEndpoint(other)) == null) {
						continue;
					}
					Set<PrecinctClusterEdge> edges = clusterMap.get(pce.getOtherEndpoint(other)).getExteriorEdges();
					PrecinctClusterEdge tbr = null;
					for (PrecinctClusterEdge e: edges) {
						if (e.equals(pce)) {
							tbr = e;
						}
					}
					edges.remove(tbr);
					edges.add(new PrecinctClusterEdge(clusterMap.get(pce.getOtherEndpoint(other)), newCluster));
					clusterMap.get(clusterMap.get(pce.getOtherEndpoint(other))).setExteriorEdges(edges);
					PrecinctClusterEdge newEdge = new PrecinctClusterEdge(clusterMap.get(pce.getOtherEndpoint(other)), newCluster);
					for (PrecinctClusterEdge e: pc1ExteriorEdges) {
						if (e.equals(newEdge)) {
							break;
						}
					}
					pc1ExteriorEdges.add(new PrecinctClusterEdge(newCluster, clusterMap.get(pce.getOtherEndpoint(other))));
									
				}
				newCluster.setExteriorEdges(pc1ExteriorEdges);
				//clusterMap.remove(pc1);
				clusterMap.remove(other);
				System.out.println();
				//clusterMap.put(newCluster, newCluster);
			}
			if (newClusters.size() == state.getDistricts().size() && newClusters.size() > precinctClusters.size() / 2) {
				return newClusters;
			}
		
    				
    	}
    	return precinctClusters;
    }
}
