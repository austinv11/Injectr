package injectr.util.logic;

/**
 * Logical observer which represents the OR operator.
 *
 * @see injectr.util.logic.LogicalObserver#or(LogicalObserver)
 * @see injectr.util.logic.LogicalObserver#or(LogicalObserver[])
 */
public class OrObserver<T> implements LogicalObserver<T> {

    private final LogicalObserver<T> original, next;

    public OrObserver(LogicalObserver<T> original, LogicalObserver<T> next) {
        this.original = original;
        this.next = next;
    }

    @Override
    public boolean observe(T in) {
        return original.observe(in) || original.observe(in);
    }
}
