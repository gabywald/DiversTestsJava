package gabywald.rest.authentication;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;

/**
 * 
 * @author Gabriel Chandesris (2024)
 */
public class BearerAuthFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        
        // final SecurityContext securityContext = requestContext.getSecurityContext();
        
        // Extract credentials from the request header
        String authHeader = requestContext.getHeaderString("Authorization");
        
        boolean isOKandDone = false;
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Decode and validate credentials
            String credentials = new String(Base64.decodeBase64(authHeader.substring("Bearer ".length())));
            System.out.println("With Token : '" + credentials + "'");
            
            isOKandDone = true;
        }
        
        Response.Status respCode = (isOKandDone) ? Response.Status.OK : Response.Status.NOT_FOUND ;
        
        if (isOKandDone) { System.out.println( "Filter is OK !" ); } else { ; }
        
        responseContext.setStatus(respCode.getStatusCode());
        responseContext.setStatusInfo(respCode);
        // responseContext.setEntity(outputData);
        
    }

}
