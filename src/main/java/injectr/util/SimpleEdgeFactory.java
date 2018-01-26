package injectr.util;

import org.jgrapht.EdgeFactory;

import java.util.Objects;

public class SimpleEdgeFactory<V> implements EdgeFactory<V, SimpleEdgeFactory.SimpleEdge<V>> {

    @Override
    public SimpleEdge<V> createEdge(V sourceVertex, V targetVertex) {
        return new SimpleEdge<>(sourceVertex, targetVertex);
    }

    public static class SimpleEdge<V> {

        private final V source, sink;

        public SimpleEdge(V source, V sink) {
            this.source = source;
            this.sink = sink;
        }

        public V getSource() {
            return source;
        }

        public V getSink() {
            return sink;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SimpleEdge)) return false;
            SimpleEdge<?> edge = (SimpleEdge<?>) o;
            return Objects.equals(getSource(), edge.getSource()) &&
                    Objects.equals(getSink(), edge.getSink());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getSource(), getSink());
        }
    }
}
