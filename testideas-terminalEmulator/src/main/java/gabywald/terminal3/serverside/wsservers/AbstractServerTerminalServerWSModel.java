package gabywald.terminal3.serverside.wsservers;

import javax.websocket.Session;

import org.glassfish.tyrus.server.Server;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;

import gabywald.terminal3.serverside.TerminalServer;
import gabywald.utilities.logger.Logger;
import gabywald.utilities.logger.Logger.LoggerLevel;

/**
 * @author Gabriel (Chandesris (2026)
 */
public abstract class AbstractServerTerminalServerWSModel {
	
    static final String secretKey	= TerminalServer.getProperty("gabywald.terminal.server.token.secretKey");	// "yourSecretKey";
    static final String issuer		= TerminalServer.getProperty("gabywald.terminal.server.token.issuer");		// "gabywald";
    static final String CLAIM_LOGIN = "login";
    static final String CLAIM_USER  = "user";
    static final String CLAIM_ROLE  = "role";
    static final Algorithm algorithm = Algorithm.HMAC256( AbstractServerTerminalServerWSModel.secretKey );
    static final JWTVerifier verifier = JWT	.require( AbstractServerTerminalServerWSModel.algorithm )
    										.withIssuer( AbstractServerTerminalServerWSModel.issuer ).build();
    
	public static String WELCOME_MESSAGE =     
			  "===============================================\n" 
			+ "   TERMINAL EMULATOR - Java 8 / Swing / REST\n"
			+ "   Type 'help' for a list of available commands\n"
			+ "   Type 'exit' to quit\n"
			+ "===============================================\n\n";
	
	protected Server server;
	protected final String name;
	protected final int port;
	protected final String context;
	
	protected AbstractServerTerminalServerWSModel(String name, int port, String context) {
		this.name = name;
		this.port = port;
		this.context = context;
	}
	
	public abstract void start();
	
	public void stop() {
		if (this.server != null) 
			{ this.server.stop(); }
	}
	
	static void printParameters(Session session) {
		session.getPathParameters().entrySet().stream().forEach( entry -> 
			{ Logger.printlnLog(LoggerLevel.LL_DEBUG, "\t[" + entry.getKey() + "]::{" + entry.getValue() + "}" ); });
	}
}
