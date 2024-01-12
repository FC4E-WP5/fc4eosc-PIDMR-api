package org.grnet.pidmr.interceptors;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@InterceptorBinding
@Target({METHOD, TYPE})
@Retention(RUNTIME)
public @interface ManageEntity {

    /**
    The type needs to be associated with a specific entity type, and this entity type is determined by the DiscriminatorValue declared on the entity.
     */
    @Nonbinding String entityType();
}
