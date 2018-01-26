package injectr.annotation;

import java.lang.annotation.*;

/**
 * This annotation represents the base of all Injectr annotations. This is used to enforce psuedo-polymorphism in
 * annotations as they cannot normally follow standard inheritance rules.
 *
 * @see injectr.annotation
 * @see injectr.annotation.proxy
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Aspect {

}
