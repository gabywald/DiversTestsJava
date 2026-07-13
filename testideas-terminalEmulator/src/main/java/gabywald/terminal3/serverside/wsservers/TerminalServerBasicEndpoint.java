package gabywald.terminal3.serverside.wsservers;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import gabywald.utilities.logger.Logger;
import gabywald.utilities.logger.Logger.LoggerLevel;

import java.io.IOException;

/**
 * @author Gabriel Chandesris (2026)
 */
@ServerEndpoint("/")
public class TerminalServerBasicEndpoint {
	@OnOpen
	public void onOpen(Session session) {
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "Client connected to Basic Server: [" + session.getId() + "]");
		AbstractServerTerminalServerWSModel.printParameters(session);
		
		try {
			String message = "Basic message sent to client";
			session.getBasicRemote().sendText(message);
			Logger.printlnLog(LoggerLevel.LL_DEBUG, message);
		} catch (IOException e) { Logger.printlnLog(LoggerLevel.LL_ERROR, "Error sending basic: {" + e.getMessage() + "}"); }
	}
	
	@OnMessage
	public void onMessage(String message, Session session) {
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "Received basic from client [" + session.getId() + "]: {" + message + "}");
		try {
			// Traiter la commande et envoyer une réponse
			String response = "basic processed: " + message;
			session.getBasicRemote().sendText(response);
		} catch (IOException e) {
			Logger.printlnLog(LoggerLevel.LL_ERROR, "Error processing basic: " + e.getMessage());
		}
	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "Client disconnected from Basic Server: " + session.getId());
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		Logger.printlnLog(LoggerLevel.LL_ERROR, "Error in Basic Server: " + throwable.getMessage());
	}
	
}
