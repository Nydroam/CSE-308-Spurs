package model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import model.Election.Party;

@Embeddable
public class VotesKey implements Serializable{
	
	@ManyToOne
	private Election election;
	private Party party;
	
	public VotesKey() {
	}
	
	public VotesKey(Election election, Party party) {
		this.election = election;
		this.setParty(party);
	}

	public Election getElection() {
		return election;
	}
	
	public Party getParty() {
		return party;
	}

	public void setParty(Party party) {
		this.party = party;
	}
}
