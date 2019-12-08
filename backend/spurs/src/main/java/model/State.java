package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.google.gson.annotations.Expose;

import lineitem.BlocLineItem;
import model.Election.ElectionType;
import model.Election.Party;
import model.Election.Race;


@Entity
public class State{

	private long id;
	@Expose
	private StateName stateName;
	private Set<District> districts;
	private Set<Precinct> precincts;
	private Set<PrecinctCluster> precinctClusters;
	
	public enum StateName {
		CALIFORNIA, RHODEISLAND, PENNSYLVANIA;
	}

	public State() {
	}

	public State(long id, StateName stateName) {
		this.setId(id);
		this.stateName = stateName;
	}

	@Id
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Enumerated(EnumType.STRING)
	public StateName getStateName() {
		return stateName;
	}

	public void setStateName(StateName stateName) {
		this.stateName = stateName;
	}
	
	@OneToMany(targetEntity = District.class, mappedBy = "state", cascade = CascadeType.ALL)
	public Set<District> getDistricts() {
		return districts;
	}

	public void setDistricts(Set<District> districts) {
		this.districts = districts;
	}
	
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(targetEntity = Precinct.class, mappedBy = "state", cascade = CascadeType.ALL)
	public Set<Precinct> getPrecincts() {
		return precincts;
	}

	public void setPrecincts(Set<Precinct> precincts) {
		this.precincts = precincts;
	}
	
	@Transient
	public Set<PrecinctCluster> initializePrecinctClusters() {
		Map<Precinct, PrecinctCluster> precinctMapping = new HashMap<Precinct, PrecinctCluster>();
		precinctClusters = new HashSet<PrecinctCluster>();
		for (Precinct p: precincts) {
			PrecinctCluster cluster = new PrecinctCluster(p);
			precinctMapping.put(p, cluster);
		}
		
		//Map<PrecinctEdge, PrecinctClusterEdge> precinctEdgeMapping = new HashMap<PrecinctEdge, PrecinctClusterEdge>();
		for (Precinct p: precincts) {
			Set<PrecinctClusterEdge> exteriorEdges = new HashSet<PrecinctClusterEdge>();
			for (PrecinctEdge edge: p.getAdjacentEdges()) {
				PrecinctCluster endpoint1 = null;
				PrecinctCluster endpoint2 = null;
				
				if (edge.getEndpoints().size() > 1) {
					for (Precinct endpoint: edge.getEndpoints()) {
						if (endpoint.equals(p)) {
							endpoint1 = precinctMapping.get(p);
						}else {
							endpoint2 = precinctMapping.get(endpoint);
						}
					}
				}
				exteriorEdges.add(new PrecinctClusterEdge(endpoint1, endpoint2));
			}
			precinctMapping.get(p).setExteriorEdges(exteriorEdges);
		}
		return precinctClusters;
	}

	public List<BlocLineItem> isVotingAsBloc(ElectionType electionType, float voteThresh, float raceThresh) {
		List<BlocLineItem> validBlocs = new ArrayList<BlocLineItem>();
		for (Precinct precinct : precincts) {
			BlocLineItem b = precinct.isBloc(electionType, voteThresh, raceThresh);
			if (b != null) {
				validBlocs.add(b);
			}
		}
		return validBlocs;
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

}
