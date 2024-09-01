package gabywald.websocket.chatServerSide.other3.explore;

import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * 
 * @author Gabriel Chandesris (2024)
 */
@ServerEndpoint(value = "/basicEndpoint")
public class BasicEndpoint { 
	@OnMessage
	public void onMessage(Session session, String message) {
		// perform an action
	}
}
