package gabywald.websocket.onemoretime.server;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

/**
 * @author Gabriel Chandesris (2026)
 */
@ServerEndpoint("/command")
public class CommandEndpoint {
	@OnOpen
	public void onOpen(Session session) {
		System.out.println("Client connected to Command Server: " + session.getId());
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		System.out.println("Received command from client " + session.getId() + ": " + message);
		try {
			// Traiter la commande et envoyer une réponse
			String response = "Command processed: " + message;
			session.getBasicRemote().sendText(response);
		} catch (IOException e) {
			System.err.println("Error processing command: " + e.getMessage());
		}
	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		System.out.println("Client disconnected from Command Server: " + session.getId());
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		System.err.println("Error in Command Server: " + throwable.getMessage());
	}
}
