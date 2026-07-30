package gabywald.terminal3.serverside.restmodules;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import gabywald.terminal3.serverside.TerminalServer;
import gabywald.terminal3.serverside.filesystem.TerminalState;
import gabywald.terminal3.serverside.shell.CommandFactory;
import gabywald.terminal3.serverside.shell.CommandParser;
import gabywald.terminal3.serverside.shell.ICommand;
import gabywald.terminal3.serverside.users.User;
import gabywald.terminal3.serverside.users.UserDB;
import gabywald.utilities.logger.Logger;
import gabywald.utilities.logger.Logger.LoggerLevel;

/**
 * 
 * @author Gabriel Chandesris (2024)
 */
@Path("terminalemulator")
@Produces(MediaType.TEXT_PLAIN)
public class TokenUseCase {
	
	public static String WELCOME_MESSAGE =	 
			  "===============================================\n" 
			+ "   TERMINAL EMULATOR - Java 8 / Swing / REST\n"
			+ "   Type 'help' for a list of available commands\n"
			+ "   Type 'exit' to quit\n"
			+ "===============================================\n\n";
	
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
				DecodedJWT decodedJWT = TerminalServer.verifier.verify(token);
				Claim claim = decodedJWT.getClaim( TerminalServer.CLAIM_USER );
				
				// Authorize and respond
				return Response.ok(TokenUseCase.WELCOME_MESSAGE + "Hello, '" + claim.asString() + "'\n").build();
			} catch (JWTVerificationException e) {
				Logger.printlnLog(LoggerLevel.LL_ERROR, "UNAUTHORIZED: '" + e.getMessage() + "'");
				Response.status(Response.Status.UNAUTHORIZED).build(); 
			}
		}
		// Unauthorized response
		return Response.status(Response.Status.UNAUTHORIZED).build();
	}
	
	@POST
	public Response getTransmission(@HeaderParam("Authorization") String authHeader, 
									@HeaderParam("Command") String command) {
		
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring("Bearer ".length());
			try {
				DecodedJWT decodedJWT = TerminalServer.verifier.verify(token);
				// Claim claim = decodedJWT.getClaim( TokenGenerator.CLAIM_USER );
				
				Logger.printlnLog(LoggerLevel.LL_DEBUG, "RECEIVED COMMAND: '" + command + "' ... !");
				
				// Getting the correct CMD
				String[] parts = CommandParser.parse(command);
				if (parts.length == 0) { return Response.ok("...").build(); }
				String commandName = parts[0]; // 
				String[] cmdArgs = new String[parts.length - 1];
				System.arraycopy(parts, 1, cmdArgs, 0, cmdArgs.length);
				ICommand cmd = CommandFactory.getCommand(commandName);
				
				// Getting correct State / PATH (orompt)
				String login = decodedJWT.getClaim(TerminalServer.CLAIM_LOGIN).asString();
				String username = decodedJWT.getClaim(TerminalServer.CLAIM_USER).asString();
				User currentUser = UserDB.getInstance().getUserWithName(login, username);
				if (currentUser == null) 
					{ Response.status(Status.BAD_REQUEST.getStatusCode(), "User '" + login + "' not found !"); }
				TerminalState ts = currentUser.getState();
				
				// Executing CMD
				String result = cmd.execute(ts, cmdArgs);
				
				// Authorize and respond
				return Response.ok( result ).header("prompt", ts.getPrompt()).build();
			} catch (JWTVerificationException e) {
				Logger.printlnLog(LoggerLevel.LL_ERROR, "UNAUTHORIZED: '" + e.getMessage() + "'");
				Response.status(Response.Status.UNAUTHORIZED).build(); 
			}
		}
		// Unauthorized response
		return Response.status(Response.Status.UNAUTHORIZED).build();
	}
	
}
