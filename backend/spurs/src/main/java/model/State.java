package model;

import java.util.ArrayList;
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

enum StateName {
	CALIFORNIA, RHODEISLAND, PENNSYLVANIA;
}

@Entity
public class State extends GeoEntity {

	private StateName stateName;
	private Set<District> districts;
	private Set<Precinct> precincts;
	
	private Geometry geometry;

	public State() {
	}

	public State(long id, Geometry geometry) {
		this.id = id;
		this.geometry = geometry;
	}

	public StateName getStateName() {
		return stateName;
	}

	public void setStateName(StateName stateName) {
		this.stateName = stateName;
	}
	@OneToOne
	public Geometry getGeometry() {
		return geometry;
	}

	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}
	@OneToMany(targetEntity = District.class, mappedBy = "state", cascade = CascadeType.ALL)
	public Set<District> getDistricts() {
		return districts;
	}

	public void setDistricts(Set<District> districts) {
		this.districts = districts;
	}
	@OneToMany(targetEntity = Precinct.class, mappedBy = "state", cascade = CascadeType.ALL)
	public Set<Precinct> getPrecincts() {
		return precincts;
	}

	public void setPrecincts(Set<Precinct> precincts) {
		this.precincts = precincts;
	}

	public List<Demographic> isVotingAsBloc(ElectionType electionType, float voteThresh, float raceThresh) {
		List<Demographic> validDemographics = new ArrayList<Demographic>();
		for (Precinct precinct : precincts) {
			Demographic d = precinct.isBloc(electionType, voteThresh, raceThresh);
			if (d != null) {
				validDemographics.add(d);
			}
		}
		return validDemographics;
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
