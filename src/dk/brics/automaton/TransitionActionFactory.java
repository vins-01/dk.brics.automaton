package dk.brics.automaton;

public final class TransitionActionFactory {

    private TransitionActionFactory() {}

    private static final ResetAction resetAction = new ResetAction();

    public static ResetAction getResetAction() {
        return resetAction;
    }
}
