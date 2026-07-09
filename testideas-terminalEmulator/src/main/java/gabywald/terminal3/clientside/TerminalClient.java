package gabywald.terminal3.clientside;

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

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import gabywald.global.structures.Pair;
import gabywald.terminal3.clientside.gui.TerminalFrame;
import gabywald.utilities.logger.Logger;
import gabywald.utilities.logger.Logger.LoggerLevel;
import gabywald.utilities.others.PropertiesLoader;

/**
 * 
 * @author Gabriel Chandesris (2026)
 */
public class TerminalClient {
	
	private static TerminalClient instance = null;
	
	public static TerminalClient getInstance() {
		if (TerminalClient.instance == null) 
			{ TerminalClient.instance = TerminalClient.builder(); }
		return TerminalClient.instance;
	}
	
	private static PropertiesLoader plClient = new PropertiesLoader("terminalemulatorClient.properties");
	
	private static TerminalClient builder() {
		// Initialize some values to connect server !
		String serverName = TerminalClient.plClient.getProperty( "gabywald.terminal.server.name" );
		String serverIPAD = TerminalClient.plClient.getProperty( "gabywald.terminal.server.ipadress" );
		String serverAUTH = TerminalClient.plClient.getProperty( "gabywald.terminal.server.authentication" );
		String serverMAIN = TerminalClient.plClient.getProperty( "gabywald.terminal.server.mainservice" );
		String serverTOKS = TerminalClient.plClient.getProperty( "gabywald.terminal.server.token.secretKey" );
		String serverTOKU = TerminalClient.plClient.getProperty( "gabywald.terminal.server.token.issuer" );
		String clientUAUA = TerminalClient.plClient.getProperty( "gabywald.terminal.client.useragent" );
	    String  login = TerminalClient.plClient.getProperty( "gabywald.terminal.user.username" ), 
	    		psswd = TerminalClient.plClient.getProperty( "gabywald.terminal.user.password" );
		int serverAUTHport = Integer.parseInt(TerminalClient.plClient.getProperty( "gabywald.terminal.server.port.authentication" ));
		int serverMAINport = Integer.parseInt(TerminalClient.plClient.getProperty( "gabywald.terminal.server.port.mainservice" ));
		
		// Request Authentication to Server !
		HttpResponse httpResponseAuth = null;
		try {
			httpResponseAuth = TerminalClient.authentication(serverName, serverAUTHport, serverAUTH, serverTOKS, serverTOKU, clientUAUA, login, psswd);
		} catch (IOException e) { e.printStackTrace(); }
		
		if (httpResponseAuth == null) {
			Logger.printlnLog(LoggerLevel.LL_ERROR, "NO AUTHENTICATION !");
			return null;
		}
		
		// Request standard service calling...
		HttpResponse httpResponseService = null;
		try {
			httpResponseService = TerminalClient.callServiceServer(serverName, serverMAINport, serverMAIN, clientUAUA, httpResponseAuth.getHeaders("Authorization")[0]);
		} catch (IOException e) { e.printStackTrace(); }
		
		if (httpResponseService == null) {
			Logger.printlnLog(LoggerLevel.LL_ERROR, "NO SERVICE !");
			return null;
		}
		
		try {
			String helloUser = EntityUtils.toString(httpResponseService.getEntity(), "UTF-8");
			Logger.printlnLog(LoggerLevel.LL_FORUSER, " => '" + httpResponseService.getStatusLine() + "' <= " );
			Logger.printlnLog(LoggerLevel.LL_FORUSER, " => '" + helloUser + "' <= " );
			TerminalFrame.getInstance().appendOutput( helloUser );
		} catch (ParseException | IOException e) { e.printStackTrace(); }
		
		// Request standard service calling...
		HttpResponse httpResponseServiceCMD = null;
		try {
			httpResponseServiceCMD = TerminalClient.callCommandServer(	serverName, serverMAINport, serverMAIN, 
																		clientUAUA, httpResponseAuth.getHeaders("Authorization")[0], 
																		"help");
		} catch (IOException e) { e.printStackTrace(); }
		
		if (httpResponseServiceCMD == null) {
			Logger.printlnLog(LoggerLevel.LL_ERROR, "NO COMMAND !");
			return null;
		}
		
		try {
			String outputOfCMD = EntityUtils.toString(httpResponseServiceCMD.getEntity(), "UTF-8");
			Logger.printlnLog(LoggerLevel.LL_FORUSER, " => '" + httpResponseServiceCMD.getStatusLine() + "' <= " );
			Logger.printlnLog(LoggerLevel.LL_NONE, " => '" + outputOfCMD + "' <= " );
			TerminalFrame.getInstance().appendOutput( outputOfCMD );
			TerminalFrame.getInstance().setPrompt(httpResponseServiceCMD.getHeaders("prompt")[0].getValue());
			TerminalFrame.getInstance().updatePrompt();
		} catch (ParseException | IOException e) { e.printStackTrace(); }
		
		return new TerminalClient(serverName, serverAUTHport, serverAUTH, serverMAINport, serverMAIN, 
								  serverTOKS, serverTOKU, clientUAUA, 
								  login, psswd, httpResponseAuth.getHeaders("Authorization")[0]);
	}
	
	/**
	 * 
	 * @param serverName
	 * @param serverAUTHport
	 * @param serverAUTH
	 * @param serverTOKS
	 * @param serverTOKU
	 * @param clientUAUA
	 * @param login
	 * @param psswd
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
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
	
	private String serverName;
	private int serverAUTHport;
	private String serverAUTH;
	private int serverMAINport;
	private String serverMAIN;
	private String serverTOKS;
	private String serverTOKU;
	private String clientUAUA;
	private String login;
	private String psswd;
	private Header bearerHeader;

	private TerminalClient(	String serverName, int serverAUTHport, String serverAUTH, int serverMAINport, String serverMAIN, 
							String serverTOKS, String serverTOKU, String clientUAUA, 
							String login, String psswd, Header bearerHeader) {
		this.serverName = serverName;
		this.serverAUTHport = serverAUTHport;
		this.serverAUTH = serverAUTH;
		this.serverMAINport = serverMAINport;
		this.serverMAIN = serverMAIN;
		this.serverTOKS = serverTOKS;
		this.serverTOKU = serverTOKU;
		this.clientUAUA = clientUAUA;
		this.login = login;
		this.psswd = psswd;
		this.bearerHeader = bearerHeader;
	}
	
	public Pair<String, String> callCommandServer(String cmd) {
		HttpResponse responseCMD = null;
		try {
			responseCMD = TerminalClient.callCommandServer(	this.serverName, this.serverMAINport, this.serverMAIN, 
															this.clientUAUA, this.bearerHeader, 
															cmd);
		} catch (IOException e) { e.printStackTrace(); }
		
		if (responseCMD == null) {
			Logger.printlnLog(LoggerLevel.LL_ERROR, "NO COMMAND !");
			 // return "Error (" + responseCMD.getStatusLine().getStatusCode() + ")";
			return new Pair<String, String> (null, null);
		}
		
		String outputOfCMD = null;
		try {
			outputOfCMD = EntityUtils.toString(responseCMD.getEntity(), "UTF-8");
			Logger.printlnLog(LoggerLevel.LL_NONE, "PROMPT: " + responseCMD.getHeaders("prompt")[0].getValue());
			// TerminalFrame.getInstance().setPrompt(responseCMD.getHeaders("prompt")[0].getValue());
			// TODO Check output Status Code !!
		} catch (ParseException | IOException e) { e.printStackTrace(); }
		return new Pair<String, String> (outputOfCMD, responseCMD.getHeaders("prompt")[0] != null ? responseCMD.getHeaders("prompt")[0].getValue() : null);
	}
	
	
}
