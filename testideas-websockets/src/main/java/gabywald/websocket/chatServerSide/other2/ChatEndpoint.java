package gabywald.websocket.chatServerSide.other2;

import jakarta.inject.Inject;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import gabywald.websocket.chatServerSide.messages.Message;
import gabywald.websocket.chatServerSide.messages.MessageDecoder;
import gabywald.websocket.chatServerSide.messages.MessageEncoder;

@ServerEndpoint(value = "/chatEndpoint/{username}",
                encoders = {MessageEncoder.class},
                decoders = {MessageDecoder.class})
public class ChatEndpoint {

    @Inject
    ChatSessionController chatSessionController;

    private static Session session;
    private static Set<Session> chatters = new CopyOnWriteArraySet<>();

    @OnOpen
    public void messageOpen(Session session,
            @PathParam("username") String username) throws IOException, EncodeException {
    	ChatEndpoint.session = session;
        Map<String,String> chatusers = chatSessionController.getUsers();
        chatusers.put(session.getId(), username);
        chatSessionController.setUsers(chatusers);
        chatters.add(session);
        Message message = new Message();
        message.setUsername(username);
        message.setMessage("Welcome " + username);
        broadcast(message);
    }

    @OnMessage
    public void messageReceiver(Session session,
            Message message) throws IOException, EncodeException {
        Map<String,String> chatusers = chatSessionController.getUsers();
        message.setUsername(chatusers.get(session.getId()));
        broadcast(message);
    }

    @OnClose
    public void close(Session session) {
        chatters.remove(session);
        Message message = new Message();
        Map<String,String> chatusers = chatSessionController.getUsers();
        String chatuser = chatusers.get(session.getId());
        message.setUsername(chatuser);
        chatusers.remove(chatuser);
        message.setMessage("Disconnected from server");

    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("There has been an error with session " + session.getId());
    }

    private static void broadcast(Message message)
            throws IOException, EncodeException {

        chatters.forEach(session -> {
            synchronized (session) {
                try {
                    session.getBasicRemote().
                    sendObject(message);
                } catch (IOException | EncodeException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
