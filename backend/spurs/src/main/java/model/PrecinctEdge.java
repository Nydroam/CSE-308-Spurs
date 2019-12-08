package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
public class PrecinctEdge {
	
	private String id;
	private Set<Precinct> endpoints;
    private float mmJoinability;
    private float nonMMJoinability;

    public PrecinctEdge() {
    }
    
    public PrecinctEdge(Precinct a, Precinct b, float mmJoinability, float nonMMJoinability) {
    	id = Math.min(a.getId(), b.getId()) + " " + Math.max(a.getId(), b.getId()); 
    	endpoints = new HashSet<Precinct>();
    	endpoints.add(a);
    	endpoints.add(b);
    	this.mmJoinability = mmJoinability;
    	this.nonMMJoinability = nonMMJoinability;
    }
    
    @Id
    public String getId() {
    	return id;
    }
    
    public void setId(String id) {
    	this.id = id;
    }
    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(mappedBy="adjacentEdges")
    public Set<Precinct> getEndpoints() {
    	return endpoints;
    }
    
    public void setEndpoints(Set<Precinct> endpts) {
    	this.endpoints = endpts;
    }

	public PrecinctCluster generateCluster(){
        return null;
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
