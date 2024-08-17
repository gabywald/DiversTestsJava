package gabywald.websocket.chatServerSide.other3.explore;

import javax.websocket.MessageHandler;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.Session;

import java.io.IOException;

@ServerEndpoint(value = "/websocket/server")
public class WebSocketServer1 {
    @OnOpen
    public void onOpen(Session session) {
        System.out.println(String.format("Connection Established(1)::%s", session.getId()));
        RemoteEndpoint.Basic remoteEndpoint = session.getBasicRemote();
        // session.addMessageHandler(new ServerMessageHandler1(remoteEndpoint));
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println(String.format("Connection closed(1)::%s", session.getId()));
    }

    @OnError
    public void onError(Throwable t) {
        System.out.println(t.toString());
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        System.out.println(String.format("Received message(1)::%s - sessionId::%s", message, session.getId()));
    }

    private class ServerMessageHandler1 implements MessageHandler.Whole<String> {
        private RemoteEndpoint.Basic _remoteEndpoint;

        ServerMessageHandler1(RemoteEndpoint.Basic remoteEndpoint) {
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
