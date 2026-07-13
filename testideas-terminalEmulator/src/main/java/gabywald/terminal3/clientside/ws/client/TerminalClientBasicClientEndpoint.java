package gabywald.terminal3.clientside.ws.client;

import java.io.IOException;

import javax.websocket.ClientEndpoint;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import gabywald.utilities.logger.Logger;
import gabywald.utilities.logger.Logger.LoggerLevel;

/**
 * @author Gabriel Chandesris (2026)
 */
@ClientEndpoint
public class TerminalClientBasicClientEndpoint extends Endpoint {
	private Session session;

	@Override
	public void onOpen(Session session, EndpointConfig config) {
		this.session = session;
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "Connected to Basic Server");
		session.addMessageHandler(new MessageHandler.Whole<String>() {
			@Override
			public void onMessage(String message) {
				Logger.printlnLog(LoggerLevel.LL_DEBUG, "Received Basic response: {" + message + "}");
			}
		});
	}

	public void close() throws IOException {
		if (this.session != null && this.session.isOpen()) 
			{ this.session.close(); }
	}

//	@OnMessage
//	public void onMessage(String message) 
//		{ Logger.printlnLog(LoggerLevel.LL_DEBUG, " ***** Received message: " + message); }
		
}
