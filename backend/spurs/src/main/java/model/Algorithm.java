package model;

import java.util.List;

import model.Election.ElectionType;

public class Algorithm {
	public State startState;
    public State currentState;

    public Algorithm() {
    }
    
    public List<Demographic> runPhase0(State state, ElectionType electionType, float voteThresh, float raceThresh) {
    	return state.isVotingAsBloc(electionType, voteThresh, raceThresh);
    }
    
    public State runPhase1(State state){
        return null;
    }

    private boolean isInRange(float min_frac, float max_frac, int target, int total){
        return min_frac < (float)target/total && (float)target/total < max_frac;
    }
}
