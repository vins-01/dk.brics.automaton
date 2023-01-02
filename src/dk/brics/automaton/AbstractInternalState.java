package dk.brics.automaton;

public abstract class AbstractInternalState {

    static String detail = "";
    static String detailTemplate = "\"%s\"";

    String getDetail() {
        return AbstractInternalState.detail;
    }

    public abstract AbstractInternalState clone();

    public abstract AbstractInternalState mergeWith(AbstractInternalState toMerge);

    /**
     * Executes the step-transition for this internal state.
     *
     * @return true if transition is valid, false otherwise
     */
    public abstract boolean step();

    public abstract void reset();

}
