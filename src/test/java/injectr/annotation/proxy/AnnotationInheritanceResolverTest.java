package injectr.annotation.proxy;

import injectr.annotation.Aspect;
import injectr.annotation.AspectOverride;
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
    public void testMultipleDependencies() {
        AnnotationInheritanceResolver resolver = new AnnotationInheritanceResolver();
        Set<Class<? extends Annotation>> deps1 = resolver.flattenDependencies(Inheriting2.class);
        Set<Class<? extends Annotation>> deps2 = resolver.flattenDependencies(Inheriting.class);
        Set<Class<? extends Annotation>> deps3 = resolver.flattenDependencies(Nested.class);
        assertTrue(deps3.containsAll(Arrays.asList(Inheriting.class, Base.class, Aspect.class)));
        assertFalse(deps3.containsAll(Arrays.asList(Nested.class, Inheriting2.class)));
        assertFalse(deps1.contains(Inheriting.class));
        assertFalse(deps2.contains(Nested.class));
    }

    @Test
    public void testMultipleInheritance() {
        AnnotationInheritanceResolver resolver = new AnnotationInheritanceResolver();
        Set<Class<? extends Annotation>> deps = resolver.flattenDependencies(Multi.class);
        assertTrue(deps.containsAll(Arrays.asList(Inheriting.class, Inheriting2.class, Base.class, Aspect.class)));
    }

    @Test
    public void testInstanceChecking() {
        AnnotationInheritanceResolver resolver = new AnnotationInheritanceResolver();
        assertTrue(resolver.isInstanceOf(Inheriting.class, Base.class));
        assertFalse(resolver.isInstanceOf(Base.class, Inheriting.class));
        assertFalse(resolver.isInstanceOf(Base.class, Base.class));
    }

    @Test
    public void testProxyCasting() {
        AnnotationInheritanceResolver resolver = new AnnotationInheritanceResolver();
        assertEquals(Nested.class.getAnnotation(Inheriting.class).value(), "Test");
        Nested annotation1 = AnnotationTest.class.getAnnotation(Nested.class);
        Multi annotation2 = AnnotationTest2.class.getAnnotation(Multi.class);
        assertEquals(annotation1.value(), "Test3");
        Inheriting castedAnnotation1 = resolver.cast(annotation1, Inheriting.class);
        assertEquals(castedAnnotation1.value(), "Test3");

        Inheriting castedAnnotation2 = resolver.cast(annotation2, Inheriting.class);
        assertEquals(castedAnnotation2.value(), "Test2");
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
        String value();
    }

    @Base
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.ANNOTATION_TYPE)
    @interface Inheriting2 {

    }

    @Inheriting(value = "Test")
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Nested {
        @AspectOverride String value();
    }

    @Inheriting(value = "Test2")
    @Inheriting2
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Multi {

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.ANNOTATION_TYPE)
    @interface BrokenBase {

    }

    @Nested(value = "Test3")
    static class AnnotationTest {

    }

    @Multi
    static class AnnotationTest2 {

    }
}
