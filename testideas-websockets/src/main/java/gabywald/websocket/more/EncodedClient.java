package gabywald.websocket.more;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.ContainerProvider;
import javax.websocket.EncodeException;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.ClientEndpoint;
import javax.websocket.WebSocketContainer;

import gabywald.websocket.chatServerSide.messages.Message;
import gabywald.websocket.chatServerSide.messages.MessageDecoder;
import gabywald.websocket.chatServerSide.messages.MessageEncoder;

/**
 * 
 * @author Gabriel Chandesris (2024)
 */
@ClientEndpoint(decoders = MessageDecoder.class, 
				encoders = MessageEncoder.class )
public class EncodedClient {

	Session session = null;
	private MessageHandler handler;

	public EncodedClient(URI endpointURI) {
		try {
			WebSocketContainer container = ContainerProvider.getWebSocketContainer();
			this.session = container.connectToServer(this, endpointURI);
			// ***** session.addMessageHandler(new ServerMessageHandler(this.session.getBasicRemote()));
			// ***** this.addMessageHandler(this.session.getBasicRemote());
		} catch (Exception e) { throw new RuntimeException(e); }
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

	// @OnMessage
	public void processMessage(String message) {
		System.out.println("Received message in client: " + message);
	}
	
	@OnMessage
	public void processMessage(Message message) {
		System.out.println("Received OBJECT message in client: " + message.getContent());
	}

	public void sendMessage(String message) {
		try {
			this.session.getBasicRemote().sendText(message);
		} catch (IOException ex) {
			Logger.getLogger(EncodedClient.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public void sendMessage(Message message) {
		try {
			this.session.getBasicRemote().sendObject(message);
		} catch (IOException | EncodeException ex) {
			Logger.getLogger(EncodedClient.class.getName()).log(Level.SEVERE, null, ex);
		}
	}


	public static interface MessageHandler {
		public void handleMessage(String message);
	}


	public void closeConnection() throws IOException { this.session.close(); }
	
	// TODO closeConnection(String reason)
	// public void closeConnection(String reason) { this.session.close(reason); }

}
