package gabywald.rest.authentication;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.SecurityContext;

/**
 * see https://github.com/payara/Payara/blob/main/appserver/payara-appserver-modules/microprofile/jwt-auth/src/main/java/fish/payara/microprofile/jwtauth/jaxrs/RolesAllowedRequestFilter.java
 * 
 * @author Gabriel Chandesris (2024)
 */
@Priority(Priorities.AUTHORIZATION)
public class RolesAllowedRequestFilter implements ContainerRequestFilter {
    
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        
        // see https://stackoverflow.com/questions/44692946/inject-securitycontext-in-service
        final SecurityContext securityContext = requestContext.getSecurityContext();
        
        // boolean hasRole = stream(rolesAllowed).anyMatch(r -> requestContext.getSecurityContext().isUserInRole(r));
        // TODO hasRole

    }

}
