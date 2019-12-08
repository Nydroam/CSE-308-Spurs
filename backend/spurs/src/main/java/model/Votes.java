package model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import model.Election.Party;

@Entity
public class Votes {
	
	private VotesKey votesKey;
	private Election election;
	private int numVotes;
	
	public Votes() {	
	}

	public Votes(Election election, Party party, int numVotes) {
		votesKey = new VotesKey(election, party);
		this.numVotes = numVotes;
	}
	
	@Id
	public VotesKey getVotesKey() {
		return votesKey;
	}
	
	public void setVotesKey(VotesKey votesKey) {
		this.votesKey = votesKey;
	}
	
	@ManyToOne
	@JoinColumns({
	    @JoinColumn(name="electionType", referencedColumnName="electionType", insertable=false, updatable=false),
	    @JoinColumn(name="precinctId", referencedColumnName="precinctId", insertable=false, updatable=false)
	  })
	public Election getElection() {
		return election;
	}

	public void setElection(Election election) {
		this.election = election;
	}
	
	@Transient	
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
