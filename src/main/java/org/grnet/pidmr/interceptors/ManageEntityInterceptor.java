package org.grnet.pidmr.interceptors;

import io.quarkus.arc.ArcInvocationContext;
import io.quarkus.security.ForbiddenException;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.grnet.pidmr.repository.ManageableEntityRepository;
import org.grnet.pidmr.util.RequestUserContext;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

@ManageEntity(entityType = "")
@Interceptor
@Priority(3000)
/**
 * Interceptor for managing entities based on specified criteria. This interceptor is designed to be used with methods annotated
 * with {@link ManageEntity}. It is intended to work when the first parameter of the intercepted method is expected to be of type {@code Long} representing an entity ID.
 * It verifies if the current user has permission to manage the entity.
 */

public class ManageEntityInterceptor {

    @Inject
    RequestUserContext requestUserContext;

    @Inject
    ManageableEntityRepository manageableEntityRepository;

    @ConfigProperty(name = "quarkus.oidc.client-id")
    @Getter
    @Setter
    private String clientID;

    @AroundInvoke
    Object canManageTheEntity(InvocationContext context) throws Exception {

        var args = context.getParameters();

        var manageEntity = Stream
                .of(context.getContextData().get(ArcInvocationContext.KEY_INTERCEPTOR_BINDINGS))
                .map(annotations-> (Set<Annotation>) annotations)
                .flatMap(java.util.Collection::stream)
                .filter(annotation -> annotation.annotationType().equals(ManageEntity.class))
                .map(annotation -> (ManageEntity) annotation)
                .findFirst()
                .get();

        if(requestUserContext.getRoles(clientID).contains("admin")){

            return context.proceed();

        } else if (args.length > 0 && args[0] instanceof Long){

            var entity = manageableEntityRepository.findByIdAndEntityType((Long) args[0], manageEntity.entityType());
            if(Objects.isNull(entity.getCreatedBy())  || !entity.getCreatedBy().equals(requestUserContext.getVopersonID())){

                throw new ForbiddenException("You do not have permission to access this resource.");

            } else{

                return context.proceed();
            }

        } else{

            return context.proceed();
        }
    }
}