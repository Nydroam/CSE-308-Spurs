package model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
public class Election {
	public enum Party {
		DEMOCRAT, REPUBLICAN, OTHER;
	}

	public enum Race {
		ASIAN, BLACK, WHITE, NHPI, AMIN, HISP;
	}

	public enum ElectionType {
		PRES16, SEN14, SEN16, SEN18, GOV14, GOV18;
	}
	
	private Precinct precinct;
	private ElectionKey electionKey;
	private int year;
	private Party winningParty;
	private List<Votes> votesByParty;
   
	public Election() {
	}
	
	public Election(Precinct precinct, ElectionType electionType, Party winningParty) {
		this.precinct = precinct;
		this.winningParty = winningParty;
		setElectionKey(new ElectionKey(precinct, electionType));
		switch(electionType) {
		case GOV18:{
			this.year = 2018;
			break;
		}
		case SEN18:{
			this.year = 2018;
			break;
		}
		case PRES16:{
			this.year = 2016;
			break;
		}
		default:
			break;
		}
	}
	
	@Id
	public ElectionKey getElectionKey() {
		return electionKey;
	}
	
	public void setElectionKey(ElectionKey electionKey) {
		this.electionKey = electionKey;
	}
	
	@ManyToOne
	@JoinColumn(name="precinctId", insertable=false, updatable=false)
	public Precinct getPrecinct() {
		return precinct;
	}

	public void setPrecinct(Precinct precinct) {
		this.precinct = precinct;
	}
	
	@Transient
	public ElectionType getElectionType() {
		return electionKey.getElectionType();
	}
	
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	@Enumerated(EnumType.STRING)
	public Party getWinningParty() {
		return winningParty;
	}

	public void setWinningParty(Party winningParty) {
		this.winningParty = winningParty;
	}

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(targetEntity=Votes.class, mappedBy="election", cascade=CascadeType.ALL)
	public List<Votes> getVotesByParty() {
		return votesByParty;
	}

	public void setVotesByParty(List<Votes> votesByParty) {
		this.votesByParty = votesByParty;
	}

	@Transient
	public Map<Party, Integer> getVotesByPartyAsMap() {
		return votesByParty.stream().collect(
				Collectors.toMap(Votes::getParty, 
						Votes::getNumVotes));
	}
	
	@Transient
	public long getVotes(){
		long sum = 0;
        for (Votes v: votesByParty) {
        	sum += v.getNumVotes();
        }
		return sum;
	}
}
