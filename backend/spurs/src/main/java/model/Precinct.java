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
	
	private District district;
	private State state;
	private List<Election> elections;
	private List<Demographic> demographics;
	private List<PrecinctEdge> adjacentEdges;
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

	@ManyToOne
	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	@ManyToOne
	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	@OneToMany(targetEntity=PrecinctEdge.class)
	public List<PrecinctEdge> getAdjacentEdges() {
		return adjacentEdges;
	}

	public void setAdjacentEdges(List<PrecinctEdge> adjacentEdges) {
		this.adjacentEdges = adjacentEdges;
	}
	
	public HashMap<Race, Integer> getPopulationByRace() {
		return null;
	}

	public void setPopulationByRace(HashMap<Race, Integer> populationByRace) {
		
	}

	public void setGeometry(List<Coordinate> geometry) {
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

	@OneToMany(targetEntity=Coordinate.class, fetch = FetchType.LAZY, cascade=CascadeType.ALL)
	public List<Coordinate> getGeometry() {
		return geometry;
	}
	
	public Demographic isBloc(ElectionType electiontype, float voteThresh, float raceThresh) {
		Map<ElectionType, Election> electionMap = getElectionsAsMap();
		Election election = electionMap.get(electiontype);
		Map<Party, Integer> votesByParty = election.getVotesByPartyAsMap();
		
		for (Demographic d: demographics) {
			int winningPartyVotes = votesByParty.get(election.getWinningParty());
			if (d.getPopulation() / population > raceThresh && winningPartyVotes / election.getVotes() > voteThresh) {
				return d;
			}
		}
		return null;
	}

	public Election getVotingData(ElectionType type) {
		return null;
	}

	@OneToMany(targetEntity=Demographic.class, cascade=CascadeType.ALL)
	public List<Demographic> getDemographics() {
		return demographics;
	}
	
	public void setDemographics(List<Demographic> demographics) {
		this.demographics = demographics;
	}
	
	@OneToMany(targetEntity=Election.class, cascade=CascadeType.ALL)
	public List<Election> getElections() {
		return elections;
	}
	
	public void setElections(List<Election> elections) {
		this.elections = elections;
	}
	
	@Transient
	public Map<ElectionType, Election> getElectionsAsMap(){
		return elections.stream().collect(
				Collectors.toMap(Election::getElectionType,
				Function.identity()));
	}
}
