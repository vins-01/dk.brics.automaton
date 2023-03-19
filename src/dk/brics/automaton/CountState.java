package dk.brics.automaton;

public class CountState extends AbstractState {

    CountState() {
        super();
    }

    CountState(long min, long max) {
        super();
        this.internalState = new InternalCountingState(min, max);
    }

    CountState(long min, long max, boolean accept) {
        this(min, max);
        this.accept = accept;
    }

}
