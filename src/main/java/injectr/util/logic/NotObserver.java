package injectr.util.logic;

/**
 * Logical observer which represents the NOT operator.
 *
 * @see LogicalObserver#negate()
 */
public class NotObserver<T> implements LogicalObserver<T> {

    private final LogicalObserver<T> original;

    public NotObserver(LogicalObserver<T> original) {
        this.original = original;
    }

    @Override
    public boolean observe(T in) {
        return !original.observe(in);
    }
}
