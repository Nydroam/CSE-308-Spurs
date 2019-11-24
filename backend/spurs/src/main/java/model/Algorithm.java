package model;

import java.util.List;

import model.Election.ElectionType;

public class Algorithm {
	public State startState;
    public State currentState;

    public Algorithm(State state) {
    	this.startState = state;
    }
    
    public List<Demographic> runPhase0(ElectionType electionType, float voteThresh, float raceThresh) {
    	return startState.isVotingAsBloc(electionType, voteThresh, raceThresh);
    }
}
