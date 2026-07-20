package gabywald.websocket.onemoretime.server;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

/**
 * @author Gabriel Chandesris (2026)
 */
@ServerEndpoint("/auth")
public class AuthEndpoint {
	@OnOpen
	public void onOpen(Session session) {
		System.out.println("Client connected to Auth Server: " + session.getId());
		try {
			// Générer un token et l'envoyer au client
			String token = AuthServer.generateToken();
			session.getBasicRemote().sendText(token);
			System.out.println("Token sent to client: " + token);
		} catch (IOException e) {
			System.err.println("Error sending token: " + e.getMessage());
		}
	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		System.out.println("Client disconnected from Auth Server: " + session.getId());
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		System.err.println("Error in Auth Server: " + throwable.getMessage());
	}
}
