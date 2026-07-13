package gabywald.terminal3.clientside;

import java.io.IOException;
import java.net.URI;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;

import gabywald.global.structures.Pair;
import gabywald.terminal3.clientside.gui.TerminalFrame;
import gabywald.utilities.logger.Logger;
import gabywald.utilities.logger.Logger.LoggerLevel;
import gabywald.utilities.others.PropertiesLoader;

/**
 * @author Gabriel Chandesris (2026)
 */
public abstract class TerminalClient {
	
	private static TerminalClient instance = null;
	
	public static TerminalClient getInstance() { return TerminalClient.instance; }
	
	private static PropertiesLoader plClient = new PropertiesLoader("terminalemulatorClient.properties");
	
	public static String getProperty(String key) { return TerminalClient.plClient.getProperty(key); }
	
	public static TerminalClient builder(boolean isREST) {
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "TerminalClient.builder: {" + isREST + "}");
		Logger.printlnLog(LoggerLevel.LL_DEBUG, (isREST)?"REST":"WebSocket");
		if (TerminalClient.instance  == null) 
			{ TerminalClient.instance = (isREST) ? TerminalClient.builderREST() : TerminalClient.builderWebSocket(); }
		return TerminalClient.instance;
	}
	
	private static TerminalClientREST builderREST() {
		// Initialize some values to connect server !
		String serverName = TerminalClient.plClient.getProperty( "gabywald.terminal.server.name" );
		@SuppressWarnings("unused")
		String serverIPAD = TerminalClient.plClient.getProperty( "gabywald.terminal.server.ipadress" );
		String serverAUTH = TerminalClient.plClient.getProperty( "gabywald.terminal.server.authentication" );
		String serverMAIN = TerminalClient.plClient.getProperty( "gabywald.terminal.server.mainservice" );
		String serverTOKS = TerminalClient.plClient.getProperty( "gabywald.terminal.server.token.secretKey" );
		String serverTOKU = TerminalClient.plClient.getProperty( "gabywald.terminal.server.token.issuer" );
		String clientUAUA = TerminalClient.plClient.getProperty( "gabywald.terminal.client.useragent" );
	    String  login = TerminalClient.plClient.getProperty( "gabywald.terminal.user.username" ), 
	    		psswd = TerminalClient.plClient.getProperty( "gabywald.terminal.user.password" );
		int serverAUTHport = Integer.parseInt(TerminalClient.plClient.getProperty( "gabywald.terminal.server.port.authentication" ));
		int serverMAINport = Integer.parseInt(TerminalClient.plClient.getProperty( "gabywald.terminal.server.port.services" ));
		
		// Request Authentication to Server !
		HttpResponse httpResponseAuth = null;
		try {
			httpResponseAuth = TerminalClientREST.authentication(serverName, serverAUTHport, serverAUTH, serverTOKS, serverTOKU, clientUAUA, login, psswd);
		} catch (IOException e) { e.printStackTrace(); }
		
		if (httpResponseAuth == null) {
			Logger.printlnLog(LoggerLevel.LL_ERROR, "NO AUTHENTICATION !");
			return null;
		}
		
		// Request standard service calling...
		HttpResponse httpResponseService = null;
		try {
			httpResponseService = TerminalClientREST.callServiceServer(serverName, serverMAINport, serverMAIN, clientUAUA, httpResponseAuth.getHeaders("Authorization")[0]);
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
			httpResponseServiceCMD = TerminalClientREST.callCommandServer(	serverName, serverMAINport, serverMAIN, 
																			clientUAUA, 
																			httpResponseAuth.getHeaders("Authorization")[0], 
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
		
		return new TerminalClientREST(serverName, serverAUTHport, serverAUTH, serverMAINport, serverMAIN, 
									  serverTOKS, serverTOKU, clientUAUA, 
									  login, psswd, httpResponseAuth.getHeaders("Authorization")[0]);
	}
	
	private static TerminalClient builderWebSocket() {
		// Initialize some values to connect server !
		String serverName = TerminalClient.plClient.getProperty( "gabywald.terminal.server.name" );
		@SuppressWarnings("unused")
		String serverIPAD = TerminalClient.plClient.getProperty( "gabywald.terminal.server.ipadress" );
		String serverAUTH = TerminalClient.plClient.getProperty( "gabywald.terminal.server.authentication" );
		String serverMAIN = TerminalClient.plClient.getProperty( "gabywald.terminal.server.mainservice" );
		@SuppressWarnings("unused")
		String serverTOKS = TerminalClient.plClient.getProperty( "gabywald.terminal.server.token.secretKey" );
		@SuppressWarnings("unused")
		String serverTOKU = TerminalClient.plClient.getProperty( "gabywald.terminal.server.token.issuer" );
		@SuppressWarnings("unused")
		String clientUAUA = TerminalClient.plClient.getProperty( "gabywald.terminal.client.useragent" );
		String  login = TerminalClient.plClient.getProperty( "gabywald.terminal.user.username" ), 
	    		psswd = TerminalClient.plClient.getProperty( "gabywald.terminal.user.password" );
		int serverAUTHport = Integer.parseInt(TerminalClient.plClient.getProperty( "gabywald.terminal.server.port.authentication" ));
		int serverMAINport = Integer.parseInt(TerminalClient.plClient.getProperty( "gabywald.terminal.server.port.services" ));
		
		TerminalClientWebSocketContainer tcws = null;
			String basicbasicWS		= URI.create("ws://" + serverName + ":" + (serverMAINport-1) + "/").toString();
			String authenticationWS = URI.create("ws://" + serverName + ":" + serverAUTHport + "/" + serverAUTH + "?login=" + login + "&psswd=" + Base64.encodeBase64String( psswd.getBytes() )).toString();
			String servicesCMDsssWS = URI.create("ws://" + serverName + ":" + serverMAINport + "/" + serverMAIN + "?token=" + "").toString();
			
			Logger.printlnLog(LoggerLevel.LL_DEBUG, "basicbasicWS----: {" + basicbasicWS    .toString() + "}");
			Logger.printlnLog(LoggerLevel.LL_DEBUG, "authenticationWS: {" + authenticationWS.toString() + "}");
			Logger.printlnLog(LoggerLevel.LL_DEBUG, "servicesCMDsssWS: {" + servicesCMDsssWS.toString() + "}");
			
			tcws = TerminalClientWebSocketContainer.build(basicbasicWS, authenticationWS, servicesCMDsssWS);
		return tcws;
	}

	/**
	 * 
	 * @param cmd (String)
	 * @return (Pair<String, String>) Result of Command and actualization of Prompt !
	 */
	public abstract Pair<String, String> callCommandServer(String cmd);
}
