package model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.google.gson.annotations.Expose;

import model.Election.Party;

@Embeddable
public class VotesKey implements Serializable{
	
	private ElectionKey electionKey;
	@Expose
	private Party party;
	
	public VotesKey() {
	}
	
	public VotesKey(Election election, Party party) {
		this.electionKey = election.getElectionKey();
		this.setParty(party);
	}
	
	public ElectionKey getElectionKey() {
		return electionKey;
	}
	
	public void setElectionKey(ElectionKey electionKey) {
		this.electionKey = electionKey;
	}
	
	@Enumerated(EnumType.STRING)
	public Party getParty() {
		return party;
	}

	public void setParty(Party party) {
		this.party = party;
	}
}
