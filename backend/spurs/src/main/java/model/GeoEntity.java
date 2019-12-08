package model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import model.Election.ElectionType;
import model.Election.Party;
import model.Election.Race;


@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class GeoEntity {

	protected long id;
	protected List<Coordinate> geometry;
	//@OneToMany(targetEntity=Edge.class)
	//protected List<Edge> adjacentEdges;
	@Id
	@Column(name="id")
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
	@Transient
	public abstract long getPopulation();
	public abstract long getPopulation(Race r);
	public abstract long getNumVoters(ElectionType election);
	public abstract long getNumVoters(ElectionType election, Party p);
	@Transient
	public abstract float getCompactnessScore();
	@OneToMany(targetEntity=Coordinate.class, mappedBy="geoEntity", cascade=CascadeType.ALL)
	public abstract List<Coordinate> getGeometry();
	public abstract void setGeometry(List<Coordinate> geometry);
	
}
