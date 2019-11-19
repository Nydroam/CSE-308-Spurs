package model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import model.Election.ElectionType;
import model.Election.Party;
import model.Election.Race;

@Entity
public class Precinct extends GeoEntity {
	
	@ManyToOne
	private District district;
	
	@ManyToOne
	private State state;
	
	@OneToMany(targetEntity=Election.class, cascade=CascadeType.ALL)
	private List<Election> elections;
	
	@OneToOne
	private Geometry geometry;
	
	@OneToMany(targetEntity=Demographic.class, cascade=CascadeType.ALL)
	private List<Demographic> demographics;
	
	@OneToMany(targetEntity=Edge.class)
	protected List<Edge> adjacentEdges;
	
	private float compactnessScore;
	private long population;

	public Precinct() {
	}
	
	public Precinct(long id) {
		this.id = id;
	}

	public void setElections(HashMap<ElectionType, Election> elections) {
		this.elections = null;
	}

	
	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	
	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public HashMap<Race, Integer> getPopulationByRace() {
		return null;
	}

	public void setPopulationByRace(HashMap<Race, Integer> populationByRace) {
		
	}

	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

	public void setCompactnessScore(float compactnessScore) {
		this.compactnessScore = compactnessScore;
	}

	public void setPopulation(int population) {
		this.population = population;
	}

	public long getPopulation() {
		return population;
	}

	public long getPopulation(Race r) {
		return 0;
	}

	public long getNumVoters(ElectionType election) {
		return 0;
	}

	public long getNumVoters(ElectionType election, Party p) {
		return 0;
	}

	public float getCompactnessScore() {
		return compactnessScore;
	}

	public Geometry getGeometry() {
		return geometry;
	}
	
	@Transient
	public Demographic isBloc(ElectionType electiontype, float voteThresh, float raceThresh) {
		Map<ElectionType, Election> electionMap = getElectionsAsMap();
		
		Election election = electionMap.get(electiontype);
		Map<Party, Integer> votesByParty = election.getVotesByPartyAsMap();
		for (Demographic d: demographics) {
			if (d.getPopulation()/population > raceThresh && votesByParty.get(election.getWinningParty())/election.getVotes() > voteThresh) {
				return d;
			}
		}
		return null;
	}

	public Election getVotingData(ElectionType type) {
		return null;
	}

	public List<Demographic> getDemographics() {
		return demographics;
	}
	
	public void setDemographics(List<Demographic> demographics) {
		this.demographics = demographics;
	}
	
	public List<Election> getElections() {
		return elections;
	}
	
	public void setElections(List<Election> elections) {
		this.elections = elections;
	}
	
	public Map<ElectionType, Election> getElectionsAsMap(){
		return elections.stream().collect(
				Collectors.toMap(Election::getElectionType,
				Function.identity()));
	}
}
