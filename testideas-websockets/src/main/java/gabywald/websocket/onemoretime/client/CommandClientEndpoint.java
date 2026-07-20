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
public class CommandClientEndpoint extends Endpoint {
	private Session session;

	@Override
	public void onOpen(Session session, EndpointConfig config) {
		this.session = session;
		System.out.println("Connected to Command Server");
		session.addMessageHandler(new MessageHandler.Whole<String>() {
			@Override
			public void onMessage(String message) {
				System.out.println("Received command response: " + message);
			}
		});
	}

	public void sendCommand(String command) throws IOException {
		if (this.session != null && this.session.isOpen()) {
			this.session.getBasicRemote().sendText(command);
		}
	}

	public void close() throws IOException {
		if (this.session != null && this.session.isOpen()) {
			this.session.close();
		}
	}
}
