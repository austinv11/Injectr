package injectr.annotation.proxy;

import injectr.util.BreadthFirstIterator;
import injectr.util.SimpleEdgeFactory.SimpleEdge;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class AnnotationProxy implements InvocationHandler {

    private static final MethodHandles.Lookup lookup = MethodHandles.lookup();

    private final AnnotationInheritanceResolver resolver;
    private final Annotation annotation;
    private final Class<? extends Annotation> toClass; //Allows for prioritization of delegation, potentially speeds up proxied calls
    private final List<Set<Class<? extends Annotation>>> inherited;
    private final Map<Method, MethodHandle> cachedHandles = new HashMap<>();

    public AnnotationProxy(AnnotationInheritanceResolver resolver, Annotation annotation,
                           Class<? extends Annotation> toClass, List<Set<Class<? extends Annotation>>> inherited) {
        this.resolver = resolver;
        this.annotation = annotation;
        this.toClass = toClass;
        this.inherited = inherited;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodHandle handle = cachedHandles.computeIfAbsent(method, (m) -> {
            //First search the original annotation
            for (Method annotationMethod : annotation.annotationType().getDeclaredMethods()) {
                if (areMethodsSimilar(method, annotationMethod))
                    return handleFor(annotationMethod, annotation);
            }

            //Next check the casted class
            for (Method annotationMethod : toClass.getDeclaredMethods()) {
                if (areMethodsSimilar(method, annotationMethod))
                    return handleFor(annotationMethod, climbToFind(toClass));
            }

            //Next check all parent types, inefficient on first run :(
            for (Class<? extends Annotation> toCheck : new BreadthFirstIterator<>(inherited)) {
                for (Method annotationMethod : toCheck.getDeclaredMethods()) {
                    if (areMethodsSimilar(method, annotationMethod))
                        return handleFor(annotationMethod, climbToFind(toCheck));
                }
            }

            throw new NoSuchElementException(m.toGenericString());
        });
        return handle.invokeWithArguments(args);
    }

    Annotation climbToFind(Class<? extends Annotation> target) {
        GraphPath<Class<? extends Annotation>, SimpleEdge<Class<? extends Annotation>>> path =
                DijkstraShortestPath.findPathBetween(resolver.dependencies, target, annotation.annotationType());
        SimpleEdge<Class<? extends Annotation>> firstEdge = path.getEdgeList().get(0); //First will contain the class which has the annotation value we are looking for
        return firstEdge.getSink().getAnnotation(target);
    }

    static MethodHandle handleFor(Method method, Object toBind) {
        method.setAccessible(true);
        try {
            return lookup.unreflect(method).bindTo(toBind);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    static boolean areMethodsSimilar(Method method1, Method method2) {
        if (method1.equals(method2))
            return true;

        //Logic is loosely based on Method#equals, Executable#equalParamTypes, andParameter#equals
        if (method1.getName().equals(method2.getName()) && method1.getReturnType().equals(method2.getReturnType())) {
            if (method1.getParameterCount() == method2.getParameterCount()) {
                for (int i = 0; i < method1.getParameterCount(); i++) {
                    Parameter[] method1Params = method1.getParameters();
                    Parameter[] method2Params = method2.getParameters();
                    if (!method1Params[i].getType().equals(method2Params[i].getType()))
                        return false;
                }
                return true;
            }
        }
        return false;
    }
}
