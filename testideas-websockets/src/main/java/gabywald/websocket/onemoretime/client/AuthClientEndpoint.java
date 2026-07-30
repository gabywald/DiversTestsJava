package gabywald.websocket.onemoretime.client;

import java.io.IOException;

import javax.websocket.ClientEndpoint;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

/**
 * @author Gabriel Chandesris (2026)
 */
@ClientEndpoint
public class AuthClientEndpoint extends Endpoint {
	private Session session;
	private String token;

	@Override
	public void onOpen(Session session, EndpointConfig config) {
		this.session = session;
		System.out.println("Connected to Auth Server");
		session.addMessageHandler(new MessageHandler.Whole<String>() {
			@Override
			public void onMessage(String message) {
				token = message;
				System.out.println("Received token: " + token);
			}
		});
	}

	public String getToken() {
		return this.token;
	}

	public void close() throws IOException {
		if (this.session != null && this.session.isOpen()) {
			this.session.close();
		}
	}
}
