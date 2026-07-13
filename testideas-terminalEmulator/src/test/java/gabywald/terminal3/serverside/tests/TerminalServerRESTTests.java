package gabywald.terminal3.serverside.tests;


import java.io.IOException;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

// import gabywald.terminal3.serverside.TerminalServer;
import gabywald.terminal3.serverside.TerminalServerREST;
import gabywald.utilities.logger.Logger;
import gabywald.utilities.logger.Logger.LoggerLevel;
import gabywald.utilities.others.PropertiesLoader;

/**
 * @author Gabriel Gabriel Chandesris (2026)
 */
class TerminalServerRESTTests {
	private TerminalServerREST tServer = null;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		this.tServer = new TerminalServerREST(); // (TerminalServerREST) TerminalServer.builder( true );
		Assertions.assertNotNull(this.tServer);
		this.tServer.start();
	}

	@AfterEach
	void tearDown() throws Exception {
		this.tServer.shutdownNow();
		this.tServer  = null;
		Assertions.assertNull(this.tServer);
	}
	
	private static HttpResponse authentication(String serverName, int serverAUTHport, String serverAUTH, String serverTOKS, String serverTOKU, String clientUAUA, String login, String psswd) 
			throws ClientProtocolException, IOException  {
		
	    HttpUriRequest requestAuth = new HttpGet( "http://" + serverName + ":" + serverAUTHport + "/" + serverAUTH + "" );
	    String toBasic = login + ":" + Base64.encodeBase64String( psswd.getBytes() );
	    requestAuth.setHeader("Authorization", "Basic " + Base64.encodeBase64String( toBasic.getBytes() ));
	    requestAuth.setHeader("user-agent", clientUAUA);
		HttpResponse httpResponseAuth = HttpClientBuilder.create().build().execute( requestAuth );
		
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "Status Code: {" + httpResponseAuth.getStatusLine().getStatusCode() + "}");
		if (httpResponseAuth.getStatusLine().getStatusCode() == Status.OK.getStatusCode()) {
			JWTVerifier verifier = JWT.require( Algorithm.HMAC256( serverTOKS ) ).withIssuer( serverTOKU ).build();
			try {
				DecodedJWT decodedJWT = verifier.verify( httpResponseAuth.getHeaders("Authorization")[0].getValue().substring("Bearer ".length()) );
				Logger.printlnLog(LoggerLevel.LL_DEBUG, "decodedJWT Subject {" + decodedJWT.getSubject() + "}");
			} catch (JWTVerificationException e) { 
				System.out.println(e.getMessage());
				return null;
			}
			
			return httpResponseAuth;
		} else { return null; }
	}
	
	/**
	 * 
	 * @param serverName
	 * @param serverMAINport
	 * @param serverMAIN
	 * @param clientUAUA
	 * @param header
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private static HttpResponse callServiceServer(String serverName, int serverMAINport, String serverMAIN, String clientUAUA, Header bearerHeader) 
			throws ClientProtocolException, IOException {
		HttpUriRequest requestService = new HttpGet( "http://" + serverName + ":" + serverMAINport + "/" + serverMAIN + "" );
		requestService.addHeader( bearerHeader );
		requestService.setHeader("user-agent", clientUAUA);
		HttpResponse httpResponseService = HttpClientBuilder.create().build().execute( requestService );
		
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "Status Code: {" + httpResponseService.getStatusLine().getStatusCode() + "}");
		if (httpResponseService.getStatusLine().getStatusCode() == Status.OK.getStatusCode()) 
			{ return httpResponseService; }
		else { return null; }
	}
	
	private static HttpResponse callCommandServer(	String serverName, int serverMAINport, String serverMAIN, 
													String clientUAUA, Header bearerHeader, 
													String command) 
			throws ClientProtocolException, IOException {
		HttpUriRequest requestService = new HttpPost( "http://" + serverName + ":" + serverMAINport + "/" + serverMAIN + "" );
		requestService.addHeader( bearerHeader );
		requestService.setHeader("user-agent", clientUAUA);
		// TODO reflexions about : put in content of request ? encryption ?
		requestService.setHeader("Command", command);
		HttpResponse httpResponseService = HttpClientBuilder.create().build().execute( requestService );
		
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "Status Code: {" + httpResponseService.getStatusLine().getStatusCode() + "}");
		if (httpResponseService.getStatusLine().getStatusCode() == Status.OK.getStatusCode()) 
			{ return httpResponseService; }
		else { return null; }
	}

	@Test
	void test() {
		PropertiesLoader plClient = new PropertiesLoader("terminalemulatorClient.properties");
		// Initialize some values to connect server !
		String serverName = plClient.getProperty( "gabywald.terminal.server.name" );
		// String serverIPAD = plClient.getProperty( "gabywald.terminal.server.ipadress" );
		String serverAUTH = plClient.getProperty( "gabywald.terminal.server.authentication" );
		String serverMAIN = plClient.getProperty( "gabywald.terminal.server.mainservice" );
		String serverTOKS = plClient.getProperty( "gabywald.terminal.server.token.secretKey" );
		String serverTOKU = plClient.getProperty( "gabywald.terminal.server.token.issuer" );
		String clientUAUA = plClient.getProperty( "gabywald.terminal.client.useragent" );
	    String  login = plClient.getProperty( "gabywald.terminal.user.username" ), 
	    		psswd = plClient.getProperty( "gabywald.terminal.user.password" );
		int serverAUTHport = Integer.parseInt(plClient.getProperty( "gabywald.terminal.server.port.authentication" ));
		int serverMAINport = Integer.parseInt(plClient.getProperty( "gabywald.terminal.server.port.services" ));
		
		// Request Authentication to Server !
		HttpResponse httpResponseAuth = null;
		try {
			httpResponseAuth = TerminalServerRESTTests.authentication(serverName, serverAUTHport, serverAUTH, serverTOKS, serverTOKU, clientUAUA, login, psswd);
		} catch (IOException e) { e.printStackTrace();Assertions.fail(e); }
		
		Assertions.assertNotNull(httpResponseAuth);
		
		// Request standard service calling...
		HttpResponse httpResponseService = null;
		try {
			httpResponseService = TerminalServerRESTTests.callServiceServer(serverName, serverMAINport, serverMAIN, clientUAUA, httpResponseAuth.getHeaders("Authorization")[0]);
		} catch (IOException e) { e.printStackTrace();Assertions.fail(e); }
		
		Assertions.assertNotNull(httpResponseService);
		
		try {
			String helloUser = EntityUtils.toString(httpResponseService.getEntity(), "UTF-8");
			Logger.printlnLog(LoggerLevel.LL_FORUSER, " => '" + httpResponseService.getStatusLine() + "' <= " );
			Logger.printlnLog(LoggerLevel.LL_FORUSER, " => '" + helloUser + "' <= " );
			Assertions.assertNotNull( helloUser );
		} catch (ParseException | IOException e) { e.printStackTrace();Assertions.fail(e); }
		
		// Request standard service calling...
		HttpResponse httpResponseServiceCMD = null;
		try {
			httpResponseServiceCMD = TerminalServerRESTTests.callCommandServer(	serverName, serverMAINport, serverMAIN, 
																		clientUAUA, httpResponseAuth.getHeaders("Authorization")[0], 
																		"help");
		} catch (IOException e) { e.printStackTrace(); }
		
		Assertions.assertNotNull(httpResponseServiceCMD);
		
		try {
			String outputOfCMD = EntityUtils.toString(httpResponseServiceCMD.getEntity(), "UTF-8");
			Logger.printlnLog(LoggerLevel.LL_FORUSER, " => '" + httpResponseServiceCMD.getStatusLine() + "' <= " );
			Logger.printlnLog(LoggerLevel.LL_NONE, " => '" + outputOfCMD + "' <= " );
			Assertions.assertNotNull( outputOfCMD );
			Assertions.assertNotNull( httpResponseServiceCMD.getHeaders("prompt")[0].getValue() );
		} catch (ParseException | IOException e) { e.printStackTrace();Assertions.fail(e); }
	}

}
