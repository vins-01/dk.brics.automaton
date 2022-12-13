package dk.brics.automaton;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface IState extends Serializable, Comparable<AbstractState> {
    Set<AbstractTransition> getTransitions();

    void addTransition(AbstractTransition t);

    void setAccept(boolean accept);

    boolean isAccept();

    IState step(char c);

    List<AbstractTransition> getSortedTransitions(boolean to_first);
}
