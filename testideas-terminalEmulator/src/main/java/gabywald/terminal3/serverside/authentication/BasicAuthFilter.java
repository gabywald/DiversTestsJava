package gabywald.terminal3.serverside.authentication;


import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.apache.commons.codec.binary.Base64;

import gabywald.utilities.logger.Logger;
import gabywald.utilities.logger.Logger.LoggerLevel;

/**
 * 
 * @author Gabriel Chandesris (2024)
 */
@Provider
public class BasicAuthFilter implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) {
        
        // final SecurityContext securityContext = requestContext.getSecurityContext();
        
        // Extract credentials from the request header
        String authHeader = requestContext.getHeaderString("Authorization");
        
        boolean isOKandDone = false;
        
        if (authHeader != null && authHeader.startsWith("Basic ")) {
            // Decode and validate credentials
            String credentials = new String(Base64.decodeBase64(authHeader.substring("Basic ".length())));
            
            isOKandDone = true;
            String outputData = "Basic credentials=[" + credentials + "]";
            Logger.printlnLog(LoggerLevel.LL_DEBUG, outputData);
        } 
        
        if ( ! isOKandDone) { requestContext.abortWith( Response.status(Response.Status.NOT_FOUND)
                                            .entity( "Resource not found. " )
                                            .build() ); }
    }
    
}
