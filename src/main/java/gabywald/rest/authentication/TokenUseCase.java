package gabywald.rest.authentication;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

/**
 * 
 * @author Gabriel Chandesris (2024)
 */
@Path("protected")
@Produces(MediaType.TEXT_PLAIN)
public class TokenUseCase {
	
	/*
	 *
	 ** https://auth0.com/docs/secure/tokens/json-web-tokens/json-web-token-claims
    iss (issuer): Issuer of the JWT
    sub (subject): Subject of the JWT (the user)
    aud (audience): Recipient for which the JWT is intended
    exp (expiration time): Time after which the JWT expires
    nbf (not before time): Time before which the JWT must not be accepted for processing
    iat (issued at time): Time at which the JWT was issued; can be used to determine age of the JWT
    jti (JWT ID): Unique identifier; can be used to prevent the JWT from being replayed (allows a token to be used only once)
     ** https://www.iana.org/assignments/jwt/jwt.xhtml#claims
	 */

	@GET
	public Response getProtectedData(@HeaderParam("Authorization") String authHeader) {
	    if (authHeader != null && authHeader.startsWith("Bearer ")) {
	        String token = authHeader.substring("Bearer ".length());
	        try {
	        	
	            try {
	                DecodedJWT decodedJWT = TokenGenerator.verifier.verify(token);
	                
	                System.out.println( decodedJWT.getSubject() );
	                System.out.println( decodedJWT.getIssuer() );
	                System.out.println( decodedJWT.getHeader() );
	                System.out.println( decodedJWT.getAudience() );
	                System.out.println( decodedJWT.getClaims() );
	                
	                decodedJWT.getClaims().keySet().stream().forEach( System.out::println );
	                Claim claim = decodedJWT.getClaim( TokenGenerator.CLAIM_USER );
	                System.out.println( "*****" + claim.asString() + "*****" );
		            // Authorize and respond
		            return Response.ok("Hello, '" + claim.asString() + "'").build();
	            } catch (JWTVerificationException e) {
	                System.out.println( "UNAUTHORIZED: '" + e.getMessage() + "'");
	                Response.status(Response.Status.UNAUTHORIZED).build(); 
	            }

	        } catch (Exception e) {
	            // Token validation failed
	            return Response.status(Response.Status.UNAUTHORIZED).build();
	        }
	    }
	    // Unauthorized response
	    return Response.status(Response.Status.UNAUTHORIZED).build();
	}
}
