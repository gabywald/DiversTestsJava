package gabywald.websocket.chatServerSide.other3.explore;

import java.io.IOException;

import javax.websocket.server.ServerEndpoint;

import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

/**
 * 
 * @author Gabriel Chandesris (2024)
 */
@ServerEndpoint(value = "/websocket")
public class EchoEndpoint extends Endpoint {
	@Override
	public void onOpen(final Session session, EndpointConfig config) {
		session.addMessageHandler(new MessageHandler.Whole<String>() {
			@Override
			public void onMessage(String msg) {
				try {
					session.getBasicRemote().sendText(msg);
				} catch (IOException e) { ; }
			}
		});
	}
}
