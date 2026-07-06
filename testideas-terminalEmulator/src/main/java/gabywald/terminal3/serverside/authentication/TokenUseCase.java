package gabywald.terminal3.serverside.authentication;


import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import gabywald.utilities.logger.Logger;
import gabywald.utilities.logger.Logger.LoggerLevel;

/**
 * 
 * @author Gabriel Chandesris (2024)
 */
@Path("terminalemulator")
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
                DecodedJWT decodedJWT = TokenGenerator.verifier.verify(token);
                
                Claim claim = decodedJWT.getClaim( TokenGenerator.CLAIM_USER );
                
                // Authorize and respond
                return Response.ok("Hello, '" + claim.asString() + "'").build();
            } catch (JWTVerificationException e) {
            	Logger.printlnLog(LoggerLevel.LL_ERROR, "UNAUTHORIZED: '" + e.getMessage() + "'");
                Response.status(Response.Status.UNAUTHORIZED).build(); 
            }
        }
        // Unauthorized response
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    
    @POST
    // @Path("command")
    public Response getTransmission(@HeaderParam("Authorization") String authHeader, @HeaderParam("Command") String command) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring("Bearer ".length());
            try {
                DecodedJWT decodedJWT = TokenGenerator.verifier.verify(token);
                
                // Claim claim = decodedJWT.getClaim( TokenGenerator.CLAIM_USER );
                
                Logger.printlnLog(LoggerLevel.LL_DEBUG, "RECEIVED COMMAND: '" + command + "'");
                
                // Authorize and respond
                return Response.ok("Results of '" + command + "'").build();
            } catch (JWTVerificationException e) {
            	Logger.printlnLog(LoggerLevel.LL_ERROR, "UNAUTHORIZED: '" + e.getMessage() + "'");
                Response.status(Response.Status.UNAUTHORIZED).build(); 
            }
        }
        // Unauthorized response
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}
