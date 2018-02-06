package injectr.util.logic;

import java.util.function.Predicate;

/**
 * This represents an observer of values which is then able to produce a boolean for its observation.
 *
 * This means that, provided an object, the implementation should be able to return true or false based on the object
 * passed.
 *
 * The {@link LogicalObserver} class is essentially a {@link Predicate}, except it is more full featured to allow for
 * some more complex logical constructs in a fluent manner.
 */
@FunctionalInterface
public interface LogicalObserver<T> {

    /**
     * This converts a {@link Predicate} to a {@link LogicalObserver}.
     *
     * @param predicate The predicate to convert.
     * @return The new {@link LogicalObserver}.
     */
    static <T> LogicalObserver<T> fromPredicate(Predicate<T>  predicate) {
        return predicate::test;
    }

    /**
     * This pairs the current logical observer with another one and compares the two observations with an AND operator.
     *
     * @param other The observer to combine with this one.
     * @return The newly composed observer containing this logic.
     *
     * @see #and(LogicalObserver[])
     */
    default LogicalObserver<T> and(LogicalObserver<T> other) {
        return new AndObserver<>(this, other);
    }

    /**
     * This combines the current logical observer with a set of other observers and compares the observations with an
     * AND operator.
     *
     * @param others The observers to combine with this one.
     * @return The newly composed observer containing this logic.
     *
     * @see #and(LogicalObserver)
     */
    default LogicalObserver<T> and(LogicalObserver<T>... others) {
        if (others.length == 0)
            return this;

        LogicalObserver<T> currObserver = this;
        for (LogicalObserver<T> other : others)
            currObserver = currObserver.and(other);

        return currObserver;
    }

    /**
     * This pairs the current logical observer with another one and compares the two observations with an OR operator.
     *
     * @param other The observer to combine with this one.
     * @return The newly composed observer containing this logic.
     *
     * @see #or(LogicalObserver[])
     */
    default LogicalObserver<T> or(LogicalObserver<T> other) {
        return new OrObserver<>(this, other);
    }

    /**
     * This combines the current logical observer with a set of other observers and compares the observations with an
     * OR operator.
     *
     * @param others The observers to combine with this one.
     * @return The newly composed observer containing this logic.
     *
     * @see #or(LogicalObserver)
     */
    default LogicalObserver<T> or(LogicalObserver<T>... others) {
        if (others.length == 0)
            return this;

        LogicalObserver<T> currObserver = this;
        for (LogicalObserver<T> other : others)
            currObserver = currObserver.or(other);

        return currObserver;
    }

    /**
     * This pairs the current logical observer with another one and compares the two observations with an XOR operator.
     *
     * @param other The observer to combine with this one.
     * @return The newly composed observer containing this logic.
     *
     * @see #xor(LogicalObserver[])
     */
    default LogicalObserver<T> xor(LogicalObserver<T> other) {
        return new XorObserver<>(this, other);
    }

    /**
     * This combines the current logical observer with a set of other observers and compares the observations with an
     * XOR operator.
     *
     * @param others The observers to combine with this one.
     * @return The newly composed observer containing this logic.
     *
     * @see #xor(LogicalObserver)
     */
    default LogicalObserver<T> xor(LogicalObserver<T>... others) {
        if (others.length == 0)
            return this;

        LogicalObserver<T> currObserver = this;
        for (LogicalObserver<T> other : others)
            currObserver = currObserver.xor(other);

        return currObserver;
    }

    /**
     * This negates the value returned by this observer.
     *
     * @return The newly composed observer containing this logic.
     */
    default LogicalObserver<T> negate() {
        return new NotObserver<>(this);
    }

    /**
     * This converts this {@link LogicalObserver} to a {@link Predicate}.
     *
     * @return The predicate corresponding to this observer.
     */
    default Predicate<T> toPredicate() {
        return this::observe;
    }

    /**
     * This is called to convert a value into a logical one.
     *
     * NOTE: It is expected that this method only passively "observes", it should not modify the object in any way.
     *
     * @param in The object to analyze.
     * @return The corresponding logical value for the object.
     */
    boolean observe(T in);
}
