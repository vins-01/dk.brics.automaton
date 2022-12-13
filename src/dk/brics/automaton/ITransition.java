package dk.brics.automaton;

import java.io.Serializable;

public interface ITransition extends Serializable, Cloneable {
    char getMin();

    char getMax();

    AbstractState getDest();

    void appendDot(StringBuilder b);
}
