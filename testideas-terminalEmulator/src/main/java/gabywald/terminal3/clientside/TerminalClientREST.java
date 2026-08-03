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

import gabywald.global.structures.PairSimple;
import gabywald.utilities.logger.Logger;
import gabywald.utilities.logger.Logger.LoggerLevel;

/**
 * 
 * @author Gabriel Chandesris (2026)
 */
public class TerminalClientREST extends TerminalClient {
	
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
	static HttpResponse authentication(String serverName, int serverAUTHport, String serverAUTH, String serverTOKS, String serverTOKU, String clientUAUA, String login, String psswd) 
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
	
	static HttpResponse callServiceServer(String serverName, int serverMAINport, String serverMAIN, String clientUAUA, Header bearerHeader) 
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
	
	static HttpResponse callCommandServer(	String serverName, int serverMAINport, String serverMAIN, 
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
	@SuppressWarnings("unused")
	private int serverAUTHport;
	@SuppressWarnings("unused")
	private String serverAUTH;
	private int serverMAINport;
	private String serverMAIN;
	@SuppressWarnings("unused")
	private String serverTOKS;
	@SuppressWarnings("unused")
	private String serverTOKU;
	private String clientUAUA;
	@SuppressWarnings("unused")
	private String login;
	@SuppressWarnings("unused")
	private String psswd;
	private Header bearerHeader;

	TerminalClientREST(	String serverName, int serverAUTHport, String serverAUTH, int serverMAINport, String serverMAIN, 
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
	
	public PairSimple<String, String> callCommandServer(String cmd) {
		HttpResponse responseCMD = null;
		try {
			responseCMD = TerminalClientREST.callCommandServer(	this.serverName, this.serverMAINport, this.serverMAIN, 
																this.clientUAUA, this.bearerHeader, 
																cmd);
		} catch (IOException e) { e.printStackTrace(); }
		
		if (responseCMD == null) {
			Logger.printlnLog(LoggerLevel.LL_ERROR, "NO COMMAND !");
			 // return "Error (" + responseCMD.getStatusLine().getStatusCode() + ")";
			return PairSimple.of(null, null);
		}
		
		String outputOfCMD = null;
		try {
			outputOfCMD = EntityUtils.toString(responseCMD.getEntity(), "UTF-8");
			Logger.printlnLog(LoggerLevel.LL_NONE, "PROMPT: " + responseCMD.getHeaders("prompt")[0].getValue());
			// TerminalFrame.getInstance().setPrompt(responseCMD.getHeaders("prompt")[0].getValue());
			// TODO Check output Status Code !!
		} catch (ParseException | IOException e) { e.printStackTrace(); }
		return PairSimple.of(outputOfCMD, responseCMD.getHeaders("prompt")[0] != null ? responseCMD.getHeaders("prompt")[0].getValue() : null);
	}
	
	
}
