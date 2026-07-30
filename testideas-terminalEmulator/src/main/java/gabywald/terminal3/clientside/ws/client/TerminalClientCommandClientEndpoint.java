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
public class TerminalClientCommandClientEndpoint extends Endpoint {
	private Session session;
	private String response;
	private String prompt;

	@Override
	public void onOpen(Session session, EndpointConfig config) {
		this.session = session;
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "Connected to Command Server");
		session.addMessageHandler(new MessageHandler.Whole<String>() {
			@Override
			public void onMessage(String message) {
				if (message.startsWith("prompt:")) 
					{ prompt = message.substring("prompt:".length()); }
				else { response = message; }
				Logger.printlnLog(LoggerLevel.LL_DEBUG, "Received Command response: {" + message + "}");
			}
		});
	}

	public void sendCommand(String command) throws IOException {
		if (this.session != null && this.session.isOpen()) {
			this.session.getBasicRemote().sendText(command);
		}
	}
	
	public String getResponse()	{ return this.response; }
	public String getPrompt()	{ return this.prompt; }

	public void emptyPrompt()	{ this.prompt = null; }

	public void close() throws IOException {
		if (this.session != null && this.session.isOpen()) 
			{ this.session.close(); }
	}


//	@OnMessage
//	public void onMessage(String message) 
//		{ Logger.printlnLog(LoggerLevel.LL_DEBUG, " ***** Received message: " + message); }
		
}
