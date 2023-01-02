package dk.brics.automaton;

public abstract class AbstractTransition implements ITransition {

    static void appendCharString(char c, StringBuilder b) {
        if (c >= 0x21 && c <= 0x7e && c != '\\' && c != '"')
            b.append(c);
        else {
            b.append("\\u");
            String s = Integer.toHexString(c);
            if (c < 0x10)
                b.append("000").append(s);
            else if (c < 0x100)
                b.append("00").append(s);
            else if (c < 0x1000)
                b.append("0").append(s);
            else
                b.append(s);
        }
    }

    /*
     * CLASS INVARIANT: min<=max
     */
    char min;
    char max;
    AbstractState to;
    ConditionalState<AbstractInternalState> conditionalState;

    /**
     * Constructs a new singleton interval transition.
     * @param c transition character
     * @param to destination state
     */
    public AbstractTransition(char c, AbstractState to)	{
        min = max = c;
        this.to = to;
    }

    /**
     * Constructs a new transition.
     * Both end points are included in the interval.
     * @param min transition interval minimum
     * @param max transition interval maximum
     * @param to destination state
     */
    public AbstractTransition(char min, char max, AbstractState to)	{
        if (max < min) {
            char t = max;
            max = min;
            min = t;
        }
        this.min = min;
        this.max = max;
        this.to = to;
    }

    public AbstractTransition(char min, char max, AbstractState to, AbstractInternalState internalState)	{
        if (max < min) {
            char t = max;
            max = min;
            min = t;
        }
        this.min = min;
        this.max = max;
        this.to = to;
        this.conditionalState = ConditionalState.toConditionalState(internalState);
    }

    public AbstractTransition(char min, char max, AbstractState to, ConditionalState<AbstractInternalState> conditionalState)	{
        if (max < min) {
            char t = max;
            max = min;
            min = t;
        }
        this.min = min;
        this.max = max;
        this.to = to;
        this.conditionalState = conditionalState;
    }

    /**
     * Returns minimum of this transition interval.
     */
    @Override
    public char getMin() {
        return min;
    }

    /**
     * Returns maximum of this transition interval.
     */
    @Override
    public char getMax() {
        return max;
    }

    /**
     * Returns destination of this transition.
     */
    @Override
    public AbstractState getDest() {
        return to;
    }

    @Override
    public void appendDot(StringBuilder b) {
        b.append(" -> ").append(to.number).append(" [label=\"");
        appendCharString(min, b);
        if (min != max) {
            b.append("-");
            appendCharString(max, b);
        }
        if (conditionalState != null) {
            b.append(" | ").append(conditionalState.getDetail());
        }
        b.append("\"]\n");
    }

    @Override
    public boolean step(AbstractInternalState startingState, char c) {
        return this.min <= c && c <= this.max && (this.conditionalState == null || this.conditionalState.test(startingState));
    }

}
