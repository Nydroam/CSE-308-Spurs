package model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@Entity
public class PrecinctEdge {
	
	private PrecinctEdgeEndpoints endpts;
    private float mmJoinability;
    private float nonMMJoinability;

    public PrecinctEdge() {
    }
    
    public PrecinctEdge(Precinct a, Precinct b, float mmJoinability, float nonMMJoinability) {
    	endpts = new PrecinctEdgeEndpoints(a, b);
    	this.mmJoinability = mmJoinability;
    	this.nonMMJoinability = nonMMJoinability;
    }
    
    @Id
    public PrecinctEdgeEndpoints getEdgeEndpoints() {
    	return endpts;
    }
    
    public void setEdgeEndpoints(PrecinctEdgeEndpoints endpts) {
    	this.endpts = endpts;
    }
    
    public PrecinctCluster generateCluster(){
        return null;
    }
    
    @Transient
    public List<Precinct> getEndPoints(){
		List<Precinct> l = new ArrayList<Precinct>();
		l.add(endpts.getEndpoint1());
		l.add(endpts.getEndpoint2());
		return l;
	}

    public void setEndpoints(Precinct endpoint1, Precinct endpoint2) {
    	
    	endpts.setEndpoint1(endpoint1);
    	endpts.setEndpoint2(endpoint2);
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
