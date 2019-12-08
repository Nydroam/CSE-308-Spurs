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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.google.gson.annotations.Expose;

import lineitem.BlocLineItem;
import model.Election.ElectionType;
import model.Election.Party;
import model.Election.Race;

@Entity
public class Precinct {
	
	@Expose
	private long id;
	@Expose
	private String name;
	@Expose
	private String county;
	private District district;
	private State state;
	private List<Coordinate> geometry;
	private List<Election> elections;
	@Expose
	private List<Demographic> demographics;
	private List<PrecinctEdge> adjacentEdges;
	@Expose
	private float compactnessScore;
	@Expose
	private long population;
	
	public Precinct() {
	}
	
	public Precinct(long id) {
		this.setId(id);
	}

	public Precinct(long id, District district) {
		this(id);
		this.district = district;
		this.state = district.getState();
	}
	
	@Id
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setElections(HashMap<ElectionType, Election> elections) {
		this.elections = null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	@ManyToOne
	@JoinColumn(name="districtId")
	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	@ManyToOne
	@JoinColumn(name="stateId")
	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToMany(targetEntity=PrecinctEdge.class, cascade=CascadeType.ALL)
	@JoinTable(
	        name = "Precinct_PrecinctEdge", 
	        joinColumns = { @JoinColumn(name = "precinctId") }, 
	        inverseJoinColumns = { @JoinColumn(name = "precinctEdgeId") }
	    )
	public List<PrecinctEdge> getAdjacentEdges() {
		return adjacentEdges;
	}

	public void setAdjacentEdges(List<PrecinctEdge> adjacentEdges) {
		this.adjacentEdges = adjacentEdges;
	}
	
	@Transient
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

	public void setPopulation(long population) {
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

	@OneToMany(targetEntity=Coordinate.class, mappedBy="precinct", cascade=CascadeType.ALL)
	public List<Coordinate> getGeometry() {
		return geometry;
	}
	
	public BlocLineItem isBloc(ElectionType electiontype, float voteThresh, float raceThresh) {
		Map<ElectionType, Election> electionMap = getElectionsAsMap();
		Election election = electionMap.get(electiontype);
		Map<Party, Integer> votesByParty = election.getVotesByPartyAsMap();
		
		for (Demographic d: demographics) {
			int winningPartyVotes = votesByParty.get(election.getWinningParty());
			if ((float)d.getPopulation() / population > raceThresh && (float)winningPartyVotes / election.getVotes() > voteThresh) {
				return new BlocLineItem(this, d, election.getWinningParty(), winningPartyVotes, (int)election.getVotes());
			}
		}
		return null;
	}

	public Election getVotingData(ElectionType type) {
		return null;
	}
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(targetEntity=Demographic.class, mappedBy="precinct", cascade=CascadeType.ALL)
	public List<Demographic> getDemographics() {
		return demographics;
	}
	
	public void setDemographics(List<Demographic> demographics) {
		this.demographics = demographics;
	}
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(targetEntity=Election.class, mappedBy="precinct", cascade=CascadeType.ALL)
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
