package dk.brics.automaton;

import java.util.List;

public abstract class AbstractInternalState {

    static String detail = "";
    static String detailTemplate = "\"%s\"";

    String getDetail() {
        return AbstractInternalState.detail;
    }

    public abstract AbstractInternalState clone();

    public abstract AbstractInternalState mergeWith(AbstractInternalState toMerge);

    public abstract List<AbstractState> splitState(char minC, char maxC, boolean accept);

    /**
     * Executes the step-transition for this internal state.
     *
     * @return true if transition is valid, false otherwise
     */
    public abstract boolean step();

    public abstract void reset();

    public abstract boolean isAccept();

}
