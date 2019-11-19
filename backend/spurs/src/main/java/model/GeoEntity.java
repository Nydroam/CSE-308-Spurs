package model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

import model.Election.ElectionType;
import model.Election.Party;
import model.Election.Race;


@MappedSuperclass
public abstract class GeoEntity {

	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected long id;
	//@OneToMany(targetEntity=Edge.class)
	//protected List<Edge> adjacentEdges;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	/*
	public List<Edge> getAdjacentEdges(){
		return adjacentEdges;
	}
	
	public void setAdjacentEdges(List<Edge> adjacentEdges) {
		this.adjacentEdges = adjacentEdges;
	}
	*/
	public abstract long getPopulation();
	public abstract long getPopulation(Race r);
	public abstract long getNumVoters(ElectionType election);
	public abstract long getNumVoters(ElectionType election, Party p);
	public abstract float getCompactnessScore();
	public abstract Geometry getGeometry();
}
