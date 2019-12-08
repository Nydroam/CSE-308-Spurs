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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.google.gson.annotations.Expose;

import model.Election.ElectionType;
import model.Election.Party;
import model.Election.Race;

@Entity
public class District{
	
	private long id;
	private State state;
	@Expose
	private Set<Precinct> precincts;
	
	public District() {
	}
	
	public District(long id, State state) {
		this.id = id;
		this.state = state;
	}
	
	@Id
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@OneToMany(targetEntity=Precinct.class, fetch = FetchType.LAZY, mappedBy="district", cascade=CascadeType.ALL)
	public Set<Precinct> getPrecincts() {
		return precincts;
	}

	public void setPrecincts(Set<Precinct> precincts) {
		this.precincts = precincts;
	}
	
	@ManyToOne
	@JoinColumn(name="stateId")
	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	
	@Transient
	public long getPopulation() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Transient
	public long getPopulation(Race r) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Transient
	public long getNumVoters(ElectionType election) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Transient
	public long getNumVoters(ElectionType election, Party p) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Transient
	public float getCompactnessScore() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
