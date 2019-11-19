package model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

@Entity
public class Election {
	public enum Party {
		DEMOCRAT, REPUBLICAN, OTHER;
	}

	public enum Race {
		ASIAN, BLACK, WHITE, NHPI, AMIN, HISP;
	}

	public enum ElectionType {
		PRESIDENTIAL2016, SENATE2014, SENATE2016, SENATE2018, GUBERNATORIAL2014, GUBERNATORIAL2018;
	}

	
	@Id
	private ElectionKey electionKey;
	private int year;
	private Party winningParty;
	@OneToMany(targetEntity=Votes.class)
	private List<Votes> votesByParty;
   
	public Election() {
	}
	
	public ElectionKey getElectionKey() {
		return electionKey;
	}
	
	public void setElectionKey(ElectionKey electionKey) {
		this.electionKey = electionKey;
	}
	
	public ElectionType getElectionType() {
		return electionKey.getElectionType();
	}
	
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public Party getWinningParty() {
		return winningParty;
	}

	public void setWinningParty(Party winningParty) {
		this.winningParty = winningParty;
	}

	public List<Votes> getVotesByParty() {
		return votesByParty;
	}

	public void setVotesByParty(List<Votes> votesByParty) {
		this.votesByParty = votesByParty;
	}

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
