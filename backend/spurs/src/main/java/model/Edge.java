package model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.MappedSuperclass;

@Entity
public class Edge {
	
	@Id
	private EdgeEndpoints endpts;
    private float mmJoinability;
    private float nonMMJoinability;

    public Edge() {
    }
    
    public Edge(Precinct a, Precinct b, float mmJoinability, float nonMMJoinability) {
    	endpts = new EdgeEndpoints(a, b);
    	this.mmJoinability = mmJoinability;
    	this.nonMMJoinability = nonMMJoinability;
    }
    
    public PrecinctCluster generateCluster(){
        return null;
    }
    
    public List<Precinct> getEndPoints(){
		List<Precinct> l = new ArrayList<Precinct>();
		l.add(endpts.getEndpoint1());
		l.add(endpts.getEndpoint2());
		return l;
	}

    public void setClusters(Precinct a, Precinct b) {
    	
    	endpts.setEndpoint1(a);;
    	endpts.setEndpoint2(b);;
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
