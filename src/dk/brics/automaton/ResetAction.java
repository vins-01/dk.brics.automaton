package dk.brics.automaton;

public class ResetAction implements TransitionAction {

    public void executeStartingStateAction(AbstractState startingState) {
        if (startingState == null || startingState.internalState == null) {
            return;
        }
        startingState.internalState.reset();
    }

    public void executeArrivalStateAction(AbstractState arrivalState) {

    }

}
