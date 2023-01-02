package dk.brics.automaton;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface ConditionalState<T extends AbstractInternalState> extends Predicate<T> {

    static <T extends AbstractInternalState> AbstractInternalState toInternalState(ConditionalState<T> conditionalState) {
        Stream<Function<ConditionalState<T>, AbstractInternalState>> mappers =
                Stream.of(InternalCountingState.InternalCountingCondition::toInternalState);
        return mappers
                .map(mapper -> mapper.apply(conditionalState))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    static <T extends AbstractInternalState> ConditionalState<T> toConditionalState(T internalState) {
        Stream<Function<T, ConditionalState<T>>> mappers = Stream.of(InternalCountingState::toConditionalState);
        return mappers
                .map(mapper -> mapper.apply(internalState))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    default String getDetail() {
        return AbstractInternalState.detail;
    }

    @Override
    boolean test(T abstractState);
}
