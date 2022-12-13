package dk.brics.automaton;

public abstract class AbstractInternalState {

    static String detail = "";
    static String detailTemplate = "\"%s\"";

    String getDetail() {
        return AbstractInternalState.detail;
    }

    public abstract AbstractInternalState clone();

    public abstract AbstractInternalState mergeWith(AbstractInternalState toMerge);

}
