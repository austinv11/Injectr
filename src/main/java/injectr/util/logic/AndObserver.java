package injectr.util.logic;

/**
 * Logical observer which represents the AND operator.
 *
 * @see injectr.util.logic.LogicalObserver#and(LogicalObserver)
 * @see injectr.util.logic.LogicalObserver#and(LogicalObserver[])
 */
public class AndObserver<T> implements LogicalObserver<T> {

    private final LogicalObserver<T> original, next;

    public AndObserver(LogicalObserver<T> original, LogicalObserver<T> next) {
        this.original = original;
        this.next = next;
    }

    @Override
    public boolean observe(T in) {
        return original.observe(in) && next.observe(in);
    }
}
