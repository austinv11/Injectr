package injectr.annotation.proxy;

import injectr.annotation.Aspect;
import injectr.util.SimpleEdgeFactory;
import injectr.util.SimpleEdgeFactory.SimpleEdge;
import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DefaultDirectedGraph;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnnotationInheritanceResolver {

    private static final Collection<String> JAVA_PACKAGE_PREFIXES = new CopyOnWriteArraySet<>();

    static {
        JAVA_PACKAGE_PREFIXES.add("java");
        JAVA_PACKAGE_PREFIXES.add("javax");
        JAVA_PACKAGE_PREFIXES.add("com.sun");
        JAVA_PACKAGE_PREFIXES.add("sun");
        JAVA_PACKAGE_PREFIXES.add("oracle");
        JAVA_PACKAGE_PREFIXES.add("org.xml");
        JAVA_PACKAGE_PREFIXES.add("com.oracle");
    }

    private final Graph<Class<? extends Annotation>, SimpleEdge<Class<? extends Annotation>>> dependencies
            = new DefaultDirectedGraph<>(new SimpleEdgeFactory<>());
    private final AllDirectedPaths<Class<? extends Annotation>, SimpleEdge<Class<? extends Annotation>>> pathfinder
            = new AllDirectedPaths<>(dependencies);

    public AnnotationInheritanceResolver() {
        dependencies.addVertex(Aspect.class);
    }

    public Set<SimpleEdge<Class<? extends Annotation>>> resolveDependencies(Class<? extends Annotation> annotationClass) {
        if (Aspect.class.equals(annotationClass))
            return Collections.emptySet();

        if (!dependencies.containsVertex(annotationClass)) {
            moveTrees(getRelevantAnnotationClasses(annotationClass), dependencies);
            pruneUnrootedVertices();
        }

        if (!dependencies.containsVertex(annotationClass))
            return Collections.emptySet();

        Set<SimpleEdge<Class<? extends Annotation>>> edges = new HashSet<>();
        pathfinder.getAllPaths(Aspect.class, annotationClass, true, null).forEach(path -> edges.addAll(path.getEdgeList()));
        return edges;
    }

    public Set<Class<? extends Annotation>> flattenDependencies(Class<? extends Annotation> annotationClass) {
        return resolveDependencies(annotationClass).stream()
                .flatMap(edge -> Stream.of(edge.getSource(), edge.getSink()))
                .filter(cls -> !annotationClass.equals(cls))
                .collect(Collectors.toSet());
    }

    private static Graph<Class<? extends Annotation>, SimpleEdge<Class<? extends Annotation>>> getRelevantAnnotationClasses(Class<? extends Annotation> annotationClass) {
        Graph<Class<? extends Annotation>, SimpleEdge<Class<? extends Annotation>>> annotationBranches
                = new DefaultDirectedGraph<>(new SimpleEdgeFactory<>());
        if (isNotJavaAnnotation(annotationClass)) {
            recursivelyResolveTree(annotationBranches, annotationClass);
        }
        return annotationBranches;
    }

    private void pruneUnrootedVertices() {
        new HashSet<>(dependencies.vertexSet()) //We need to mask the original set to prevent ConcurrentModificationExceptions
                .stream()
                .filter(vertex -> pathfinder.getAllPaths(Aspect.class, vertex, true, null).size() == 0)
                .forEach(dependencies::removeVertex);
    }

    private static <V, E extends SimpleEdge<V>> void moveTrees(Graph<V, E> from, Graph<V, E> to) {
        from.edgeSet().forEach(edge -> safeAddEdge(to, edge.getSource(), edge.getSink()));
    }

    private static void recursivelyResolveTree(Graph<Class<? extends Annotation>, SimpleEdge<Class<? extends Annotation>>> graph, Class<? extends Annotation> annotationClass) {
        for (Annotation annotation : annotationClass.getAnnotations()) {
            Class<? extends Annotation> annotationCls = annotation.annotationType();
            if (isNotJavaAnnotation(annotationCls) && !graph.containsEdge(annotationCls, annotationClass)) {
                safeAddEdge(graph, annotationCls, annotationClass);
                recursivelyResolveTree(graph, annotationCls);
            }
        }
    }

    private static <V, E> void safeAddEdge(Graph<V, E> graph, V source, V sink) {
        graph.addVertex(source);
        graph.addVertex(sink);
        graph.addEdge(source, sink);
    }

    private static boolean isNotJavaAnnotation(Class<? extends Annotation> annotationClass) {
        if (annotationClass.getPackage() == null) return false; //Ignore internal proxies
        return JAVA_PACKAGE_PREFIXES.stream().noneMatch(prefix -> annotationClass.getPackage().getName().startsWith(prefix));
    }
}
