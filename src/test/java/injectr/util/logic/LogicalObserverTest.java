package injectr.util.logic;

import org.junit.Test;

import static org.junit.Assert.*;

public class LogicalObserverTest {

    @Test
    public void testObserver() {
        AlwaysTrueObserver t = new AlwaysTrueObserver();
        AlwaysFalseObserver f = new AlwaysFalseObserver();

        assertTrue(t.observe(null));
        assertFalse(f.observe(null));

        assertFalse(t.negate().observe(null));
        assertTrue(f.negate().observe(null));

        assertTrue(t.or(new AlwaysFalseObserver()).observe(null));
        assertFalse(f.or(new AlwaysFalseObserver()).observe(null));

        assertTrue(t.and(new AlwaysTrueObserver()).observe(null));
        assertFalse(t.and(new AlwaysFalseObserver()).observe(null));

        assertTrue(t.xor(new AlwaysFalseObserver()).observe(null));
        assertFalse(t.xor(new AlwaysTrueObserver()).observe(null));
    }

    public static class AlwaysTrueObserver<T> implements LogicalObserver<T> {

        @Override
        public boolean observe(T in) {
            return true;
        }
    }

    public static class AlwaysFalseObserver<T> implements LogicalObserver<T> {

        @Override
        public boolean observe(T in) {
            return false;
        }
    }
}
