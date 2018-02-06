package injectr.util.logic;

/**
 * Logical observer which represents the XOR operator.
 *
 * @see injectr.util.logic.LogicalObserver#xor(LogicalObserver)
 * @see injectr.util.logic.LogicalObserver#xor(LogicalObserver[])
 */
public class XorObserver<T> implements LogicalObserver<T> {

    private final LogicalObserver<T> original, next;

    public XorObserver(LogicalObserver<T> original, LogicalObserver<T> next) {
        this.original = original;
        this.next = next;
    }

    @Override
    public boolean observe(T in) {
        return original.observe(in) ^ next.observe(in);
    }
}
