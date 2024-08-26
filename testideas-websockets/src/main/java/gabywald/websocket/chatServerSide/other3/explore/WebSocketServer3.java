package gabywald.websocket.chatServerSide.other3.explore;

import java.io.IOException;

import javax.websocket.MessageHandler;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint;
import javax.websocket.server.ServerEndpoint;

import gabywald.websocket.chatServerSide.messages.MessageDecoder;
import gabywald.websocket.chatServerSide.messages.MessageEncoder;

import javax.websocket.Session;

@ServerEndpoint(value = "/websocket/server",
                encoders = {MessageEncoder.class},
                decoders = {MessageDecoder.class})
public class WebSocketServer3 {
    @OnOpen
    public void onOpen(Session session) {
        System.out.println(String.format("Connection Established(3)::%s", session.getId()));
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println(String.format("Connection closed(3)::%s", session.getId()));
    }

    @OnError
    public void onError(Throwable t) {
        System.out.println(t.toString());
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        System.out.println(String.format("Received message(3)::%s - sessionId::%s", message, session.getId()));
    }
    
    private class ServerMessageHandler3 implements MessageHandler.Whole<String> {
        private RemoteEndpoint.Basic _remoteEndpoint;

        ServerMessageHandler3(RemoteEndpoint.Basic remoteEndpoint) {
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
