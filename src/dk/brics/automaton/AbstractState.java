package dk.brics.automaton;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractState implements IState {

    boolean accept;
    Set<AbstractTransition> transitions;
    AbstractInternalState internalState;

    int number;

    int id;
    static int next_id;

    /**
     * Constructs a new state. Initially, the new state is a reject state.
     */
    public AbstractState() {
        resetTransitions();
        id = next_id++;
    }

    /**
     * Resets transition set.
     */
    final void resetTransitions() {
        transitions = new HashSet<>();
    }

    /**
     * Returns the set of outgoing transitions.
     * Subsequent changes are reflected in the automaton.
     * @return transition set
     */
    @Override
    public Set<AbstractTransition> getTransitions()	{
        return transitions;
    }

    /**
     * Adds an outgoing transition.
     * @param t transition
     */
    @Override
    public void addTransition(AbstractTransition t)	{
        transitions.add(t);
    }

    /**
     * Sets acceptance for this state.
     * @param accept if true, this state is an accept state
     */
    @Override
    public void setAccept(boolean accept) {
        this.accept = accept;
    }

    /**
     * Returns acceptance status.
     * @return true is this is an accept state
     */
    @Override
    public boolean isAccept() {
        return accept;
    }

    /**
     * Performs lookup in transitions, assuming determinism.
     * @param c character to look up
     * @return destination state, null if no matching outgoing transition
     * @see #step(char, Collection)
     */
    @Override
    public AbstractState step(char c) {
        AbstractTransition t = this.getTransition(c);
        if (t != null)
            return t.to;
        return null;
    }

    public AbstractTransition getTransition(char c) {
        for (AbstractTransition t : transitions)
            if (t.min <= c && c <= t.max)
                return t;
        return null;
    }

    public AbstractState countingStep(char c) {
        for (AbstractTransition t : transitions)
            if (t.step(this.internalState, c))
                return executeTransition(t);
        return null;
    }

    protected AbstractState executeTransition(AbstractTransition t) {
        AbstractState dest = this;
        if (t.to != null) {
            if (t.to.internalState != null)
                t.to.internalState.step();
            dest = t.to;
            if (t.action != null) {
                t.action.executeStartingStateAction(this);
                t.action.executeArrivalStateAction(t.to);
            }
        }
        return dest;
    }

    /**
     * Performs lookup in transitions, allowing nondeterminism.
     * @param c character to look up
     * @param dest collection where destination states are stored
     * @see #step(char)
     */
    public void step(char c, Collection<AbstractState> dest) {
        for (AbstractTransition t : transitions)
            if (t.min <= c && c <= t.max)
                dest.add(t.to);
    }

    void addEpsilon(AbstractState to) {
        this.addEpsilon(to, null);
    }

    void addEpsilon(AbstractState to, AbstractInternalState state) {
        this.addEpsilon(to, null, state);
    }

    void addEpsilon(AbstractState to, Boolean reset, AbstractInternalState state) {
        if (to.accept)
            accept = true;
        if (this.internalState == null && to.internalState != null) {
            this.internalState = to.internalState;
        }
        copyTransition(to.transitions, reset, state);
    }

    private void copyTransition(Set<AbstractTransition> transitions, Boolean reset, AbstractInternalState state) {
        this.transitions.addAll(
            transitions
                .stream()
                .map(t ->
                        new Transition(
                                t.min, t.max, t.to,
                                state == null ? t.conditionalState : ConditionalState.toConditionalState(state),
                                reset == null ? t.reset : reset
                        )
                )
                .collect(Collectors.toSet())
        );
    }

    /** Returns transitions sorted by (min, reverse max, to) or (to, min, reverse max) */
    AbstractTransition[] getSortedTransitionArray(boolean to_first) {
        AbstractTransition[] e = transitions.toArray(new AbstractTransition[transitions.size()]);
        Arrays.sort(e, new TransitionComparator(to_first));
        return e;
    }

    /**
     * Returns sorted list of outgoing transitions.
     * @param to_first if true, order by (to, min, reverse max); otherwise (min, reverse max, to)
     * @return transition list
     */
    @Override
    public List<AbstractTransition> getSortedTransitions(boolean to_first)	{
        return Arrays.asList(getSortedTransitionArray(to_first));
    }

    /**
     * Compares this object with the specified object for order.
     * States are ordered by the time of construction.
     */
    public int compareTo(AbstractState s) {
        return s.id - id;
    }

    public String appendDot(StringBuilder b, AbstractState initial) {
        b.append("  ").append(number);
        String label = getLabel();
        if (accept)
            b.append(" [shape=doublecircle,label=").append(label).append("];\n");
        else
            b.append(" [shape=circle,label=").append(label).append("];\n");
        if (this == initial) {
            b.append("  initial [shape=plaintext,label=").append(label).append("];\n");
            b.append("  initial -> ").append(number).append("\n");
        }
        for (AbstractTransition t : transitions) {
            b.append("  ").append(number);
            t.appendDot(b);
        }
        return b.toString();
    }

    String getLabel() {
        return String.format(
            AbstractInternalState.detailTemplate,
            Optional.ofNullable(this.internalState)
                    .map(AbstractInternalState::getDetail)
                    .orElse(AbstractInternalState.detail)
        );
    }
}
