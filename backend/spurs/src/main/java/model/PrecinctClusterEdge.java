package model;

import java.util.ArrayList;
import java.util.List;

public class PrecinctClusterEdge {
	
	private PrecinctCluster endpoint1;
	private PrecinctCluster endpoint2;
	private float mmJoinability;
    private float nonMMJoinability;

    public PrecinctClusterEdge() {
    }
    
    public PrecinctClusterEdge(PrecinctCluster endpoint1, PrecinctCluster endpoint2, float mmJoinability, float nonMMJoinability) {
    	this.endpoint1 = endpoint1;
    	this.endpoint2 = endpoint2;
    	this.mmJoinability = mmJoinability;
    	this.nonMMJoinability = nonMMJoinability;
    }
    
    public List<PrecinctCluster> getEndPoints(){
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

	public float getMMJoinability(){
        return mmJoinability;
    }
    
    public void setMMJoinability(float mmJoinability) {
    	this.mmJoinability = mmJoinability;
    }

    public float getNonMMJoinability(){
        return nonMMJoinability;
    }
    
    public void setNonMMJoinability(float nonMMJoinability) {
    	this.nonMMJoinability = nonMMJoinability;
    }
}
