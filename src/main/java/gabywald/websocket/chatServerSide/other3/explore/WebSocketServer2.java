package gabywald.websocket.chatServerSide.other3.explore;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint;
import javax.websocket.server.ServerEndpoint;

import javax.websocket.Session;

@ServerEndpoint(value = "/websocket/server", 
				decoders = MessageDecoder.class, 
				encoders = MessageEncoder.class )
public class WebSocketServer2 {
    @OnOpen
    public void onOpen(Session session) {
        System.out.println(String.format("Connection Established::%s", session.getId()));
        RemoteEndpoint.Basic remoteEndpoint = session.getBasicRemote();
        // session.addMessageHandler(new ServerMessageHandler(remoteEndpoint));
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println(String.format("Connection closed::%s", session.getId()));
    }

    @OnError
    public void onError(Throwable t) {
        System.out.println(t.toString());
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        System.out.println(String.format("Received message::%s - sessionId::%s", message, session.getId()));
    }

}
