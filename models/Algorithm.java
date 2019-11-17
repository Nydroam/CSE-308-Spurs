public class Algorithm {

    //PROPERTIES
    public State startState;
    public State currentState;

    //METHODS

    public State runPhase1(StateID state){
        return null;
    }

    // HELPER
    private boolean isInRange(float min_frac, float max_frac, int target, int total){
        return min_frac < (float)target/total && (float)target/total < max_frac;
    }
}