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
public class TerminalClientAuthClientEndpoint extends Endpoint {
	private Session session;
	private String token;
	private String toshow;

	@Override
	public void onOpen(Session session, EndpointConfig config) {
		this.session = session;
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "Connected to Auth Server");
		session.addMessageHandler(new MessageHandler.Whole<String>() {
			@Override
			public void onMessage(String message) {
				Logger.printlnLog(LoggerLevel.LL_DEBUG, "Received whole: {" + message + "}");
				token	= message.split(";")[0].split(":")[1];
				toshow	= message.split(";")[1].split(":")[1];
				Logger.printlnLog(LoggerLevel.LL_DEBUG, "Received token: {" + token + "}");
			}
		});
	}

	public String getToken()	{ return this.token; }
	public String getMessage()	{ return this.toshow; }

	public void close() throws IOException {
		if (this.session != null && this.session.isOpen()) 
			{ this.session.close(); }
	}

//	@OnMessage
//	public void onMessage(String message) 
//		{ Logger.printlnLog(LoggerLevel.LL_DEBUG, " ***** Received message: " + message); }
	
}
