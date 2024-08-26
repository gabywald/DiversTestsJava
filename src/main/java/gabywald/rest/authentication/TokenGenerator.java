package gabywald.rest.authentication;

import java.util.Date;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;

import gabywald.rest.users.User;

/**
 * 
 * NOTE : https://www.baeldung.com/java-auth0-jwt
 * 
 * @author Gabriel Chandesris (2024)
 */
@Path("authenticate")
@Produces(MediaType.TEXT_PLAIN)
public class TokenGenerator {
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response getToken(@HeaderParam("login") String login,
                             @HeaderParam("password") String psswd) {
        JSONObject jsonObject = new JSONObject();
        
        if ( (login == null) || (psswd == null) ) { 
            jsonObject.put("message", "Bad Authentification !"); 
            return Response.status(Response.Status.PRECONDITION_FAILED).entity(jsonObject.toString()).build();
        } else {
            // DONE authenticate !! Check login + password
            User user = User.getUser(login, new String(Base64.decodeBase64(psswd)));
            if (user == null)  {
                jsonObject.put("message", "Bad Authentification !"); 
                return Response.status(Response.Status.PRECONDITION_FAILED).entity(jsonObject.toString()).build();
            }
            String token = this.generateToken(login, Base64.decodeBase64(psswd).toString(), user.getRoleSTR());
            jsonObject.put("token", token); 
            return Response    .ok(jsonObject.toString(), MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token).build();
        }
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getToken(@HeaderParam("Authorization") String authorization) {
        JSONObject jsonObject = new JSONObject();
        
        if (authorization == null) { 
            jsonObject.put("message", "Bad Authentification !"); 
            return Response.status(Response.Status.PRECONDITION_FAILED).entity(jsonObject.toString()).build();
        } else {
            String tmpAuth = new String( Base64.decodeBase64(authorization.substring("Basic ".length())) );
            String login = tmpAuth.split(":")[0];
            String psswd = tmpAuth.split(":")[1];
            // DONE authenticate !! Check login + password
            User user = User.getUser(login, new String(Base64.decodeBase64(psswd)));
            if (user == null)  {
                jsonObject.put("message", "Bad Authentification !"); 
                return Response.status(Response.Status.PRECONDITION_FAILED).entity(jsonObject.toString()).build();
            }
            // Generate Token !
            String token = this.generateToken(user.getUsername(), new String(Base64.decodeBase64(psswd)), user.getRoleSTR());
            jsonObject.put("token", token); 
            return Response    .ok(jsonObject.toString(), MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token).build();
        }
    }
    
    // Secret key to sign the token
    static final String secretKey = "yourSecretKey";
    static final String issuer = "gabywald";
    static final String CLAIM_USER = "user";
    static final String CLAIM_ROLE = "role";
    static final Algorithm algorithm = Algorithm.HMAC256( TokenGenerator.issuer );
    static final JWTVerifier verifier = JWT.require( TokenGenerator.algorithm ).withIssuer( TokenGenerator.issuer ).build();

    // Generate a JWT
    private String generateToken(String login, String password, String role) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 3600000); // Token valid for 1 hour
        
        return JWT.create()
                  .withIssuer( TokenGenerator.issuer )
                  .withSubject( "'" + TokenGenerator.issuer + "' Details" )
                  .withClaim(TokenGenerator.CLAIM_USER, login)
                  .withClaim(TokenGenerator.CLAIM_ROLE, role)
                  .withIssuedAt( now )
                  .withExpiresAt( expiration )
                  .withJWTId(UUID.randomUUID().toString())
                  // .withNotBefore(new Date(System.currentTimeMillis() + 1000L))
                  .sign( TokenGenerator.algorithm ) ;
    }

}
