package lineitem;

import com.google.gson.annotations.Expose;

import model.Demographic;
import model.Election.Party;
import model.Precinct;
import model.Votes;

public class BlocLineItem {
	@Expose
	private Precinct precinct;
	@Expose
	private Demographic demographic;
	@Expose
	private Party party;
	@Expose
	private int votes;
	@Expose
	private int totalVotes;
	
	public BlocLineItem(Precinct precinct, Demographic demographic, Party party, int votes, int totalVotes) {
		this.precinct = precinct;
		this.demographic = demographic;
		this.party = party;
		this.votes = votes;
		this.totalVotes = totalVotes;
	}
}
