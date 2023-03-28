package dk.brics.automaton;

import java.util.ArrayList;
import java.util.List;

public class InternalCountingState extends AbstractInternalState {

    public static class InternalCountingCondition<T extends AbstractInternalState> implements ConditionalState<T> {

        public static <T extends AbstractInternalState> InternalCountingState toInternalState(ConditionalState<T> conditionalState) {
            if (!(conditionalState instanceof InternalCountingCondition)) {
                return null;
            }
            return new InternalCountingState(((InternalCountingCondition<T>) conditionalState).min, ((InternalCountingCondition<T>) conditionalState).max);
        }

        private final long min, max;

        private InternalCountingCondition(long min, long max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public String getDetail() {
            return this.min + "," + this.max;
        }

        @Override
        public boolean test(AbstractInternalState abstractState) {
            return  abstractState instanceof InternalCountingState &&
                    ((InternalCountingState) abstractState).count >= this.min &&
                    ((InternalCountingState) abstractState).count <= this.max;
        }

        public int hashCode() {
            final int prime = 31;
            int result = (int) (min ^ (min >>> 32));
            result = prime * result + (int) (max ^ (max >>> 32));
            return result;
        }
    }

    public static <T extends AbstractInternalState> ConditionalState<T> toConditionalState(AbstractInternalState state) {
        if (!(state instanceof InternalCountingState)) {
            return null;
        }
        return new InternalCountingCondition<>(((InternalCountingState) state).min, ((InternalCountingState) state).max);
    }

    long min, max, count;

    InternalCountingState(long min, long max, long count) {
        super();
        this.min = min;
        this.max = max;
        this.count = count;
    }

    InternalCountingState(long min, long max) {
        this(min, max, 0);
    }

    String getDetail() {
        return this.min + "," + this.max;
    }

    public InternalCountingState clone() {
        return new InternalCountingState(min, max, count);
    }

    public AbstractInternalState mergeWith(AbstractInternalState toMerge) {
        AbstractInternalState merged = null;
        if (toMerge == null) {
            merged = this.clone();
        }
        if (toMerge instanceof InternalCountingState) {
            InternalCountingState countingState = ((InternalCountingState) toMerge);
            long min = this.min;
            long max = this.max;
            if (countingState.min < this.min) {
                min = countingState.min;
            }
            if (countingState.max > this.max) {
                max = countingState.max;
            }
            merged = new InternalCountingState(min, max);
        }
        return merged;
    }

    public List<AbstractState> splitState(char minC, char maxC, boolean accept) {
        List<AbstractState> splittedStates = new ArrayList<>(3);
        long min = this.min;
        long max = this.max;
        AbstractState startingState = null;
        if (this.min > 1) {
            min = 1;
            max = this.min - 1;
            startingState = new CountState(min, max, !accept);
            startingState.addTransition(
                new Transition(minC, maxC, startingState, new InternalCountingCondition<>(min, max), false)
            );
            splittedStates.add(startingState);
        }
        long midMin = max < this.min ? this.min - max : this.min;
        long midMax = max < this.max ? this.max - max : this.max;
        final CountState midState = new CountState(midMin, midMax, accept);
        midState.addTransition(
            new Transition(minC, maxC, midState, new InternalCountingCondition<>(midMin, midMax - 1), false)
        );
        splittedStates.add(midState);

        CountState finalState = null;
        if (this.max < Long.MAX_VALUE) {
            finalState = new CountState(1, Long.MAX_VALUE, !accept);
            finalState.addTransition(
                    new Transition(minC, maxC, finalState, new InternalCountingCondition<>(1, Long.MAX_VALUE), false)
            );
            splittedStates.add(finalState);
        }

        if (startingState != null) {
            startingState.addTransition(
                new Transition(minC, maxC, midState, new InternalCountingCondition<>(this.min, this.min), true)
            );
        }
        if (finalState != null) {
            midState.addTransition(
                new Transition(minC, maxC, finalState, new InternalCountingCondition<>(midMax, midMax), true)
            );
        }
        return splittedStates;
    }

    public boolean isAccept() {
        return this.canStep(this.count, this.min, this.max);
    }

    private boolean canStep(long counter, long lower, long upper) {
        return counter >= lower && counter <= upper;
    }

    public boolean step() {
        this.count++;
        return canStep(this.count, this.min, this.max);
    }

    public void reset() {
        this.count = 0;
    }

    public int hashCode() {
        final int prime = 31;
        int result = (int) (min ^ (min >>> 32));
        result = prime * result + (int) (max ^ (max >>> 32));
        return result;
    }

}
