package gabywald.websocket.chatServerSide.other3.explore;

import java.io.IOException;

import javax.websocket.MessageHandler;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint;
import javax.websocket.server.ServerEndpoint;

import javax.websocket.Session;

@ServerEndpoint(value = "/websocket/server")
public class WebSocketServer2 {
    @OnOpen
    public void onOpen(Session session) {
        System.out.println(String.format("Connection Established(2)::%s", session.getId()));
        // RemoteEndpoint.Basic remoteEndpoint = session.getBasicRemote();
        // session.addMessageHandler(new ServerMessageHandler2(remoteEndpoint));
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println(String.format("Connection closed(2)::%s", session.getId()));
    }

    @OnError
    public void onError(Throwable t) {
        System.out.println(t.toString());
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        System.out.println(String.format("Received message(2)::%s - sessionId::%s", message, session.getId()));
        try {
			session.getBasicRemote().sendText("Raiponce : '" + message + "'");
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private class ServerMessageHandler2 implements MessageHandler.Whole<String> {
        private RemoteEndpoint.Basic _remoteEndpoint;

        ServerMessageHandler2(RemoteEndpoint.Basic remoteEndpoint) {
            this._remoteEndpoint = remoteEndpoint;
        }
        public void onMessage(String message) {
            try {
            	this._remoteEndpoint.sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
