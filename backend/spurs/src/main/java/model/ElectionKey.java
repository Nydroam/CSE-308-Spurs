package model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import model.Election.ElectionType;

@Embeddable
public class ElectionKey implements Serializable{

	@ManyToOne
	private Precinct precinct;
	private ElectionType electionType;
	
	public ElectionKey() {
	}
	
	public ElectionKey(Precinct precinct, ElectionType type) {
		this.precinct = precinct;
		this.electionType = type;
	}
	
	public Precinct getPrecinct() {
		return precinct;
	}

	public void setPrecinct(Precinct precinct) {
		this.precinct = precinct;
	}

	public ElectionType getElectionType() {
		return electionType;
	}

	public void setElectionType(ElectionType electionType) {
		this.electionType = electionType;
	}
	
	public String toString() {
		return precinct.getId() + electionType.toString();
	}
}
