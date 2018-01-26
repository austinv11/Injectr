package injectr.annotation.proxy;

import injectr.annotation.Aspect;
import org.junit.Test;

import java.lang.annotation.*;
import java.util.Arrays;
import java.util.Set;

import static org.junit.Assert.*;

public class AnnotationInheritanceResolverTest {

    @Test
    public void testInitialInheritanceResolution() {
        AnnotationInheritanceResolver resolver = new AnnotationInheritanceResolver();
        Set<Class<? extends Annotation>> deps = resolver.flattenDependencies(Nested.class);
        assertTrue(deps.containsAll(Arrays.asList(Inheriting.class, Base.class, Aspect.class)));
        assertFalse(deps.contains(Nested.class));
        assertTrue(resolver.flattenDependencies(Aspect.class).isEmpty());
    }

    @Test
    public void testFailure() {
        AnnotationInheritanceResolver resolver = new AnnotationInheritanceResolver();
        Set<Class<? extends Annotation>> deps = resolver.flattenDependencies(BrokenBase.class);
        assertTrue(deps.isEmpty());
    }

    @Test
    public void testMulipleDependencies() {
        AnnotationInheritanceResolver resolver = new AnnotationInheritanceResolver();
        Set<Class<? extends Annotation>> deps1 = resolver.flattenDependencies(Inheriting2.class);
        Set<Class<? extends Annotation>> deps2 = resolver.flattenDependencies(Inheriting.class);
        Set<Class<? extends Annotation>> deps3 = resolver.flattenDependencies(Nested.class);
        assertTrue(deps3.containsAll(Arrays.asList(Inheriting.class, Base.class, Aspect.class)));
        assertFalse(deps3.containsAll(Arrays.asList(Nested.class, Inheriting2.class)));
        assertFalse(deps1.contains(Inheriting.class));
        assertFalse(deps2.contains(Nested.class));
    }

    @Aspect
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.ANNOTATION_TYPE)
    @interface Base {

    }

    @Base
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.ANNOTATION_TYPE)
    @interface Inheriting {

    }

    @Base
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.ANNOTATION_TYPE)
    @interface Inheriting2 {

    }

    @Inheriting
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.ANNOTATION_TYPE)
    @interface Nested {

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.ANNOTATION_TYPE)
    @interface BrokenBase {

    }
}
