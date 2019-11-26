package model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import model.Election.Party;

@Entity
public class Votes {
	@Id
	private VotesKey votesKey;
	private int numVotes;
	
	
	
	public Votes() {	
	}

	public Votes(Election election, Party party, int numVotes) {
		votesKey = new VotesKey(election, party);
		this.numVotes = numVotes;
	}
	
	
	public VotesKey getVotesKey() {
		return votesKey;
	}
	
	public Election getElection() {
		return votesKey.getElection();
	}

	public Party getParty() {
		return votesKey.getParty();
	}

	public int getNumVotes() {
		return numVotes;
	}

	public void setNumVotes(int numVotes) {
		this.numVotes = numVotes;
	}
	
	
}
