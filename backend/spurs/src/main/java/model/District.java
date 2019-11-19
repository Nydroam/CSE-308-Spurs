package model;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import model.Election.ElectionType;
import model.Election.Party;
import model.Election.Race;

@Entity
public class District extends GeoEntity{
	
	@ManyToOne
	private State state;
	@OneToOne
	private Geometry geometry;
	@OneToMany(targetEntity=Precinct.class, mappedBy="district", cascade=CascadeType.ALL)
	private Set<Precinct> precincts;
	
	public District() {
	}

	public Geometry getGeometry() {
		return geometry;
	}

	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

	public Set<Precinct> getPrecincts() {
		return precincts;
	}

	public void setPrecincts(Set<Precinct> precincts) {
		this.precincts = precincts;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	@Override
	public long getPopulation() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getPopulation(Race r) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getNumVoters(ElectionType election) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getNumVoters(ElectionType election, Party p) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getCompactnessScore() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
