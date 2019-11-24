package model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.MappedSuperclass;

@Entity
public class Edge {
	
	
	private EdgeEndpoints endpts;
    private float mmJoinability;
    private float nonMMJoinability;

    public Edge() {
    }
    
    public Edge(GeoEntity a, GeoEntity b, float mmJoinability, float nonMMJoinability) {
    	endpts = new EdgeEndpoints(a, b);
    	this.mmJoinability = mmJoinability;
    	this.nonMMJoinability = nonMMJoinability;
    }
    
    @Id
    public EdgeEndpoints getEdgeEndpoints() {
    	return endpts;
    }
    
    public void setEdgeEndpoints(EdgeEndpoints endpts) {
    	this.endpts = endpts;
    }
    
    public PrecinctCluster generateCluster(){
        return null;
    }
    
    public List<GeoEntity> getEndPoints(){
		List<GeoEntity> l = new ArrayList<GeoEntity>();
		l.add(endpts.getEndpoint1());
		l.add(endpts.getEndpoint2());
		return l;
	}

    public void setClusters(GeoEntity a, GeoEntity b) {
    	
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
