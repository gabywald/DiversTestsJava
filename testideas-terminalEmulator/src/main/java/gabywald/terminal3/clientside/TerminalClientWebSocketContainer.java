package gabywald.terminal3.clientside;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import gabywald.global.structures.Pair;
import gabywald.terminal3.clientside.gui.TerminalFrame;
import gabywald.terminal3.clientside.ws.client.TerminalClientAuthClientEndpoint;
import gabywald.terminal3.clientside.ws.client.TerminalClientBasicClientEndpoint;
import gabywald.terminal3.clientside.ws.client.TerminalClientCommandClientEndpoint;
import gabywald.terminal3.ws.messages.Message;
import gabywald.utilities.logger.Logger;
import gabywald.utilities.logger.Logger.LoggerLevel;

/**
 * 
 * @author Gabriel Chandesris (2026)
 */
public class TerminalClientWebSocketContainer extends TerminalClient {

//	private Session basicSession		= null;
//	private Session authenticateSession	= null;
//	private Session servicesSession		= null;
	
	@SuppressWarnings("unused")
	private TerminalClientBasicClientEndpoint basicEndpoint 		= null;
	@SuppressWarnings("unused")
	private TerminalClientAuthClientEndpoint authenticateEndpoint	= null;
	private TerminalClientCommandClientEndpoint servicesEndpoint	= null;
	
	private TerminalClientWebSocketContainer(	TerminalClientBasicClientEndpoint basicEndPoint, 
												TerminalClientAuthClientEndpoint authenticateEndPoint, 
												TerminalClientCommandClientEndpoint servicesEndPoint) {
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "Constructor TerminalClientWebSocketContainer BEGIN");
		this.basicEndpoint			= basicEndPoint;	
		this.authenticateEndpoint	= authenticateEndPoint;
		this.servicesEndpoint		= servicesEndPoint;
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "Constructor TerminalClientWebSocketContainer END");
	}
	
	private static TerminalClientWebSocketContainer instance = null;
	public static TerminalClientWebSocketContainer getInstance() { 
		if (TerminalClientWebSocketContainer.instance == null ) 
			{ TerminalClientWebSocketContainer.instance = new TerminalClientWebSocketContainer(null, null, null); }
		return TerminalClientWebSocketContainer.instance;
	}
	
	public static TerminalClientWebSocketContainer build(	String basicEndpointURI, 
															String authenticateEndpointURI, 
															String servicesEndpointURI) {
		WebSocketContainer container = ContainerProvider.getWebSocketContainer();
		
		StringBuilder sbContent = new StringBuilder();
		// sb.append("{");
		Arrays.asList( Message.fieldNames ).forEach( nameOfField -> {
			sbContent.append("\"").append( nameOfField ).append("\" : \"TESTBASIC" + nameOfField + "\", ");
		});
		// sb.append("}");
		sbContent.delete(sbContent.length()-2, sbContent.length());
		StringBuilder sb2send = new StringBuilder();
		sb2send.append("{ \"list\": [ txt ], ").append(sbContent.toString()).append(" }");
		
		try {
			TerminalClientBasicClientEndpoint basicClient = new TerminalClientBasicClientEndpoint();
			Session basicSession = container.connectToServer(basicClient, URI.create(basicEndpointURI));
			basicSession.getBasicRemote().sendText("message");
			
			CountDownLatch latch = new CountDownLatch(1);
			TerminalClientAuthClientEndpoint authClient = new TerminalClientAuthClientEndpoint();
			/*Session authenticateSession = */container.connectToServer(authClient, URI.create(authenticateEndpointURI));
			latch.await(5, TimeUnit.SECONDS);
			String token = authClient.getToken();
			TerminalFrame.getInstance().appendOutput( authClient.getMessage());
			
			
			TerminalClientCommandClientEndpoint serviceClient = new TerminalClientCommandClientEndpoint();
			/*Session servicesSession = */container.connectToServer(serviceClient, URI.create(servicesEndpointURI + token));
			serviceClient.sendCommand("help"); // servicesSession.getBasicRemote().sendText("help");
			
			// TerminalFrame.getInstance().appendOutput( "\n" );
			while (serviceClient.getPrompt() == null) { 
				TerminalFrame.getInstance().appendOutput( "." );
				latch.await(1, TimeUnit.SECONDS);
			}
			TerminalFrame.getInstance().appendOutput( "\n" );
			
			TerminalFrame.getInstance().appendOutput( serviceClient.getResponse() );
			TerminalFrame.getInstance().setPrompt(serviceClient.getPrompt());
			TerminalFrame.getInstance().updatePrompt();
			
			TerminalClientWebSocketContainer.instance = new TerminalClientWebSocketContainer(basicClient, authClient, serviceClient);
		} catch (DeploymentException e) {
			Logger.printlnLog(LoggerLevel.LL_ERROR, "DeploymentException: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Logger.printlnLog(LoggerLevel.LL_ERROR, "IOException: " + e.getMessage());
			e.printStackTrace();
		} catch (InterruptedException e) {
			Logger.printlnLog(LoggerLevel.LL_ERROR, "InterruptedException: " + e.getMessage());
			e.printStackTrace();
		}
		
		return TerminalClientWebSocketContainer.instance;
	}

	public void sendMessage4service(String commMSG) {
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "sendMessage4service\t" + commMSG.toString());
		try {
			this.servicesEndpoint.sendCommand(commMSG);
		} catch (IOException e) {
			e.printStackTrace();
			Logger.printlnLog(LoggerLevel.LL_ERROR, e.getMessage());
		}
	}

	@Override
	public Pair<String, String> callCommandServer(String cmd) {
		this.servicesEndpoint.emptyPrompt();
		this.sendMessage4service( cmd );
		CountDownLatch latch = new CountDownLatch(1);
		try {
			while (this.servicesEndpoint.getPrompt() == null) { 
				latch.await(1, TimeUnit.SECONDS);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			Logger.printlnLog(LoggerLevel.LL_ERROR, e.getMessage());
			return new Pair<String, String>(null, null); 
		}
		
		return new Pair<String, String>(this.servicesEndpoint.getResponse(), this.servicesEndpoint.getPrompt()); 
	}
	
}
