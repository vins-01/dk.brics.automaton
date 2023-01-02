package dk.brics.automaton;

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
