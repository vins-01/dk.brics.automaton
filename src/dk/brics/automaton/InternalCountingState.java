package dk.brics.automaton;

public class InternalCountingState extends AbstractInternalState {

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

    public int hashCode() {
        final int prime = 31;
        int result = (int) (min ^ (min >>> 32));
        result = prime * result + (int) (max ^ (max >>> 32));
        return result;
    }

}
