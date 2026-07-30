package gabywald.terminal3.serverside.restmodules;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import gabywald.terminal3.serverside.TerminalServer;
import gabywald.terminal3.serverside.users.User;
import gabywald.terminal3.serverside.users.UserDB;

/**
 * 
 * <br/>NOTE : https://www.baeldung.com/java-auth0-jwt
 * @author Gabriel Chandesris (2026)
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
            User user = UserDB.getInstance().getUser(login, new String(Base64.decodeBase64(psswd)));
            if (user == null)  {
                jsonObject.put("message", "Bad Authentification !"); 
                return Response.status(Response.Status.PRECONDITION_FAILED).entity(jsonObject.toString()).build();
            }
            // String token = this.generateToken(login, Base64.decodeBase64(psswd).toString(), user.getRoleSTR()); 
            String token = TerminalServer.generateToken(login, user.getUsername(), user.getRoleSTR());
            jsonObject.put("token", token); 
            return Response .ok(jsonObject.toString(), MediaType.APPLICATION_JSON)
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
            User user = UserDB.getInstance().getUser(login, new String(Base64.decodeBase64(psswd)));
            if (user == null)  {
                jsonObject.put("message", "Bad Authentification !"); 
                return Response.status(Response.Status.PRECONDITION_FAILED).entity(jsonObject.toString()).build();
            }
            // Generate Token !
            // String token = this.generateToken(user.getUsername(), new String(Base64.decodeBase64(psswd)), user.getRoleSTR());
            String token = TerminalServer.generateToken(login, user.getUsername(), user.getRoleSTR());
            jsonObject.put("token", token); 
            return Response    .ok(jsonObject.toString(), MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token).build();
        }
    }
    


}
