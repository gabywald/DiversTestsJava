package gabywald.websocket.onemoretime.client;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

/**
 * @author Gabriel Chandesris (2026)
 */
@ClientEndpoint
public class WebSocketClient {
	private Session session;
	private CountDownLatch latch;
	private String receivedMessage;

	public WebSocketClient() {
		this.latch = new CountDownLatch(1);
	}

	public void connectToAuthServer(String uri) throws Exception {
		WebSocketContainer container = ContainerProvider.getWebSocketContainer();
		URI serverUri = URI.create(uri);
		container.connectToServer(this, serverUri);
		this.latch.await(5, TimeUnit.SECONDS); // Attendre la réception du token
	}

	public void connectToCommandServer(String uri) throws Exception {
		WebSocketContainer container = ContainerProvider.getWebSocketContainer();
		URI serverUri = URI.create(uri);
		container.connectToServer(this, serverUri);
	}

	public String getReceivedMessage() {
		return this.receivedMessage;
	}

	@OnOpen
	public void onOpen(Session session) {
		this.session = session;
		System.out.println("Connected to server: " + session.getId());
	}

	@OnMessage
	public void onMessage(String message) {
		this.receivedMessage = message;
		System.out.println("Received message: " + message);
		this.latch.countDown(); // Libérer le latch après réception
	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		System.out.println("Disconnected from server: " + closeReason);
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		System.err.println("Error: " + throwable.getMessage());
	}

	public void sendMessage(String message) throws IOException {
		if (this.session != null && session.isOpen()) {
			this.session.getBasicRemote().sendText(message);
		}
	}

	public void close() throws IOException {
		if (this.session != null && this.session.isOpen()) {
			this.session.close();
		}
	}
}
