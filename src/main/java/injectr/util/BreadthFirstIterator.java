package injectr.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class BreadthFirstIterator<T, C1 extends Collection<C2>, C2 extends Collection<T>> implements Iterator<T>, Iterable<T> {

    private final C1 backing;
    private final Iterator<C2> backingIterator;
    private Iterator<T> currIterator;

    public BreadthFirstIterator(C1 backing) {
        this.backing = backing;
        this.backingIterator = backing.iterator();
    }

    private void ensureObtainedIterator() {
        if (currIterator == null && backingIterator.hasNext())
            nextIterator();
    }

    private void nextIterator() {
        currIterator = backingIterator.next().iterator();
    }


    private void nextIfNeeded() {
        if (!currIterator.hasNext()) {
            if (backingIterator.hasNext())
                nextIterator();
            else
                currIterator = null;
        }
    }

    private void bootstrap() {
        ensureObtainedIterator();
        nextIfNeeded();
    }

    @Override
    public boolean hasNext() {
        bootstrap();
        return currIterator != null && currIterator.hasNext();
    }

    @Override
    public T next() {
        bootstrap();
        if (currIterator == null || !currIterator.hasNext()) throw new NoSuchElementException();
        return currIterator.next();
    }

    @Override
    public void remove() {
        bootstrap();
        if (currIterator != null)
            currIterator.remove();
    }

    @Override
    public Iterator<T> iterator() {
        return this;
    }
}
