package model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lineitem.BlocLineItem;
import model.Election.ElectionType;
import model.Election.Race;

public class Algorithm {
	private State state;
    private Set<PrecinctCluster> precinctClusters;
    private Set<PrecinctCluster> mergedClusters;
    private boolean finishedPhase1;

    //test code
    
    
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
    	HashMap<PrecinctCluster,PrecinctClusterEdge> pickedClusters = new HashMap<PrecinctCluster,PrecinctClusterEdge>();
    	HashSet<PrecinctCluster> feederClusters = new HashSet<PrecinctCluster>();
    	Set<PrecinctCluster> unpickedClusters = new HashSet<PrecinctCluster>();
    	for (PrecinctCluster pc : mergedClusters) {
    		if (feederClusters.contains(pc)) {
    			continue;
    		}
    		float mmj = 0;
    		float mmnj = 0;
    		float joinability = 0;
    		float maxJoinability = -10000;
    		PrecinctClusterEdge maxEdge = null;
    		for(PrecinctClusterEdge edge : pc.getExteriorEdges()) {
    			mmj = edge.calculateMMJoinability(races, rangeMin, rangeMax);
    			mmnj = edge.calculateNonMMJoinability();
    			joinability = (mmj + mmnj)/2;
    			if (!pc.equals(edge.getOtherEndpoint(pc)) && joinability >= maxJoinability && !feederClusters.contains(edge.getOtherEndpoint(pc)) && !pickedClusters.containsKey(edge.getOtherEndpoint(pc))) {
    				maxJoinability = joinability;
    				maxEdge = edge;
    			}
    		}
    		if(maxEdge!=null) {
    		pickedClusters.put(pc,maxEdge);
			feederClusters.add(maxEdge.getOtherEndpoint(pc));
    		}
    	}

    	for (PrecinctCluster pc: mergedClusters) {
    		if(!pickedClusters.containsKey(pc) && !feederClusters.contains(pc))
    			unpickedClusters.add(pc);
    	}
    	
    	mergedClusters = new HashSet<PrecinctCluster>();
    	mergedClusters.addAll(unpickedClusters);
    	for (PrecinctCluster pc : pickedClusters.keySet()) {
    		
    		PrecinctClusterEdge pickedEdge = pickedClusters.get(pc);
    		
    		PrecinctCluster B = pickedEdge.getOtherEndpoint(pc);
    		PrecinctCluster A = pickedEdge.generatePrecinctCluster(pc);
    		    		
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
			//A - B
			//X - Y
			//Making all points from C to B now C to A
			for (PrecinctCluster c : other) {
				Set<PrecinctClusterEdge> newEdges = new HashSet<PrecinctClusterEdge>();
				for(PrecinctClusterEdge edge : c.getExteriorEdges()) {
					PrecinctClusterEdge next = new PrecinctClusterEdge(edge.getEndpoint1(),edge.getEndpoint2());
					if(edge.getOtherEndpoint(c).equals(B)) {
						next.setEndpoints(c, A);
						if (pickedClusters.containsKey(c) && pickedClusters.get(c).getEndPoints().contains(B)) {
							pickedClusters.put(c, next);

						}
					}
					newEdges.add(next);
				}
				c.setExteriorEdges(newEdges);
			}
			
			//Union A and B into A and remove B from clusters
			Set<PrecinctClusterEdge> union = new HashSet<PrecinctClusterEdge>();
			union.addAll(AExterior);
			union.addAll(BExterior);
			A.setExteriorEdges(union);
			
			mergedClusters.add(A);
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
    
    public void initializeMMPopulations(List<Race> races, float rangeMin, float rangeMax) {
    	for (PrecinctCluster pc: precinctClusters) {
    		long population = 0;
    		for (Precinct p: pc.getPrecincts()) {
    			for (Race r: races) {
    				population += p.getPopulation(r);
    			}
    		}
    		pc.setMmPopulation(population);
    	}
    }
}
