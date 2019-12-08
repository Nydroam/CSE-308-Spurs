package model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import model.Election.ElectionType;

@Embeddable
public class ElectionKey implements Serializable{

	private long precinctId;
	private ElectionType electionType;
	
	public ElectionKey() {
	}
	
	public ElectionKey(Precinct precinct, ElectionType type) {
		this.precinctId = precinct.getId();
		this.electionType = type;
	}
	
	public long getPrecinctId() {
		return precinctId;
	}

	public void setPrecinctId(long precinctId) {
		this.precinctId = precinctId;
	}

	@Enumerated(EnumType.STRING)
	@Column(name="electionType", insertable=false, updatable=false)
	public ElectionType getElectionType() {
		return electionType;
	}

	public void setElectionType(ElectionType electionType) {
		this.electionType = electionType;
	}
	
	public String toString() {
		return precinctId + electionType.toString();
	}
}
