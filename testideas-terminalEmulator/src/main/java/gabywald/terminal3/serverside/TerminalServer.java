package gabywald.terminal3.serverside;

import java.util.Date;
import java.util.UUID;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;

import gabywald.utilities.logger.Logger;
import gabywald.utilities.logger.Logger.LoggerLevel;
import gabywald.utilities.others.PropertiesLoader;

/**
 * @author Gabriel Chandesris (2026)
 */
public abstract class TerminalServer implements Runnable {
	
	private static TerminalServer instance = null;
	
	private static PropertiesLoader plServer = new PropertiesLoader("terminalemulatorServer.properties");
	
	public static String getProperty(String key) { return TerminalServer.plServer.getProperty(key); }
	
	public static TerminalServer builder(boolean isREST) {
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "TerminalServer.builder: {" + isREST + "}");
		Logger.printlnLog(LoggerLevel.LL_DEBUG, (isREST)?"REST":"WebSocket");
		if (TerminalServer.instance  == null) 
			// { TerminalServer.instance = (isREST) ? new TerminalServerREST() : new TerminalServerWebSocket(); }
		{
			if (isREST)	{ TerminalServer.instance = new TerminalServerREST(); }
			else		{ TerminalServer.instance = new TerminalServerWebSocket(); }
		}
		return TerminalServer.instance;
	}
	
	@Override
	public abstract void run();
	
//	public abstract void shutdown();
	
	public abstract void start();
	
	public abstract void shutdownNow();
	
	
    // Secret key to sign the token
    static final String secretKey	= TerminalServer.getProperty("gabywald.terminal.server.token.secretKey"); // "yourSecretKey";
    static final String issuer		= TerminalServer.getProperty("gabywald.terminal.server.token.issuer"); // "gabywald";
    public static final String CLAIM_LOGIN = "login";
    public static final String CLAIM_USER  = "user";
    static final String CLAIM_ROLE  = "role";
    static final Algorithm algorithm = Algorithm.HMAC256( TerminalServer.secretKey );
    public static final JWTVerifier verifier = JWT.require( TerminalServer.algorithm ).withIssuer( TerminalServer.issuer ).build();

    // Generate a JWT
    public static String generateToken(String login, String user, String role) {
        Date now		= new Date();
        Date expiration	= new Date(now.getTime() + 3600000); // Token valid for 1 hour
        
        return JWT.create()
                  .withIssuer( TerminalServer.issuer )
                  .withSubject( "'" + TerminalServer.issuer + "' Details" )
                  .withClaim(TerminalServer.CLAIM_LOGIN, login)
                  .withClaim(TerminalServer.CLAIM_USER, user)
                  .withClaim(TerminalServer.CLAIM_ROLE, role)
                  .withIssuedAt( now )
                  .withExpiresAt( expiration )
                  .withJWTId(UUID.randomUUID().toString())
                  // .withNotBefore(new Date(System.currentTimeMillis() + 1000L))
                  .sign( TerminalServer.algorithm ) ;
    }
    
	// Generate UUID Token
	public static String generateUUIDToken() {
		return UUID.randomUUID().toString();
	}
	
}
