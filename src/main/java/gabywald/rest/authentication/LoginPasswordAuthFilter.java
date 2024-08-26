package gabywald.rest.authentication;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.apache.commons.codec.binary.Base64;

/**
 * 
 * @author Gabriel Chandesris (2024)
 */
@Provider
public class LoginPasswordAuthFilter implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // final SecurityContext securityContext = requestContext.getSecurityContext();
        
        // Extract credentials from the request header
        String loginHeader = requestContext.getHeaderString("login");
        String psswdHeader = requestContext.getHeaderString("password");
        
        boolean isOKandDone = false;
        
        if ( (loginHeader != null) && (psswdHeader != null) ) {
            // Decode and validate credentials
            String psswd = new String(Base64.decodeBase64( psswdHeader ));
            String credentials = loginHeader + ":" + psswd;
            
            isOKandDone = true;
            String outputData = "LP credentials=[" + credentials + "]";
            System.out.println( outputData );
        } 
        
        if ( ! isOKandDone) { requestContext.abortWith(    Response.status(Response.Status.NOT_FOUND)
                                            .entity( "Resource not found. " )
                                            .build() ); }

    }

}
