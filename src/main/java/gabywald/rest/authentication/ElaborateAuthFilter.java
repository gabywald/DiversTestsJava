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
public class ElaborateAuthFilter implements ContainerResponseFilter {

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		
    	// final SecurityContext securityContext = requestContext.getSecurityContext();
    	
        // Extract credentials from the request header
        String authHeader = requestContext.getHeaderString("Authorization");
        
        boolean isOKandDone = false;
        boolean hasLoginAndPassword = false;
        
        if (authHeader != null && authHeader.startsWith("Basic ")) {
            // Decode and validate credentials
            String credentials = new String(Base64.decodeBase64(authHeader.substring("Basic ".length())));
            System.out.println( "credentials=[" + credentials + "]" );
           
            hasLoginAndPassword = ( (requestContext.getHeaderString("login") != null) && (requestContext.getHeaderString("password") != null) );
            
            isOKandDone = true;
        }
        
        Response.Status respCode = ( (isOKandDone) && (hasLoginAndPassword) ) ? Response.Status.OK : Response.Status.NOT_FOUND ;
        		// (isOKandDone) ? ( (hasLoginAndPassword) ? Response.Status.OK : Response.Status.PARTIAL_CONTENT ) : Response.Status.NOT_FOUND ;
        
        if ( (isOKandDone) && (hasLoginAndPassword) ) { System.out.println( "Filter is OK !" ); } else { ; }
        
    	responseContext.setStatus(respCode.getStatusCode());
    	responseContext.setStatusInfo(respCode);
        // responseContext.setEntity(outputData);
    	
	}

}
