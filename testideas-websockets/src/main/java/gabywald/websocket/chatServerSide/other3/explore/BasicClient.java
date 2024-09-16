package gabywald.websocket.chatServerSide.other3.explore;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.ContainerProvider;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.ClientEndpoint;
import javax.websocket.WebSocketContainer;

/**
 * 
 * @author Gabriel Chandesris (2024)
 */
@ClientEndpoint
public class BasicClient {

	Session session = null;
	private MessageHandler handler;

	public BasicClient(URI endpointURI) {
		try {
			WebSocketContainer container = ContainerProvider.getWebSocketContainer();
			this.session = container.connectToServer(this, endpointURI);
			// ***** session.addMessageHandler(new ServerMessageHandler(this.session.getBasicRemote()));
			// ***** this.addMessageHandler(this.session.getBasicRemote());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@OnOpen
	public void onOpen(Session session) {
		this.session = session;
		try {
			this.session.getBasicRemote().sendText("Opening connection");
		} catch (IOException ex) {
			System.out.println(ex);
		}
	}

	public void addMessageHandler(MessageHandler msgHandler) {
		this.handler = msgHandler;
	}

	@OnMessage
	public void processMessage(String message) {
		System.out.println("Received message in client: " + message);
	}

	public void sendMessage(String message) {
		try {
			this.session.getBasicRemote().sendText(message);
		} catch (IOException ex) {
			Logger.getLogger(BasicClient.class.getName()).log(Level.SEVERE, null, ex);
		}
	}


	public static interface MessageHandler {
		public void handleMessage(String message);
	}


	public void closeConnection() throws IOException { this.session.close(); }

	// TODO closeConnection(String reason)
	// public void closeConnection(String reason) { this.session.close(reason); }

}
