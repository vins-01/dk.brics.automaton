package dk.brics.automaton;

public interface TransitionAction {

    public void executeStartingStateAction(AbstractState startingState);

    public void executeArrivalStateAction(AbstractState arrivalState);
}
