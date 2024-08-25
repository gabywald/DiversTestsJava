package gabywald.websocket.chatServerSide.other;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
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

@ServerEndpoint(  value="/chat/{username}", 
				  decoders = MessageDecoder.class, 
				  encoders = MessageEncoder.class )
public class ChatEndpoint {

	private Session session;
	private static Set<ChatEndpoint> chatEndpoints = new CopyOnWriteArraySet<>();
	private static HashMap<String, String> users = new HashMap<>();

	@OnOpen
	public void onOpen( Session session, 
						@PathParam("username") String username) 
			throws IOException, EncodeException {

		this.session = session;
		ChatEndpoint.chatEndpoints.add(this);
		ChatEndpoint.users.put(session.getId(), username);

		Message message = new Message();
		message.setFrom(username);
		message.setContent("Connected!");
		broadcast(message);
	}

	@OnMessage
	public void onMessage(Session session, Message message) 
			throws IOException, EncodeException {

		message.setFrom(ChatEndpoint.users.get(session.getId()));
		broadcast(message);
	}

	@OnClose
	public void onClose(Session session) 
			throws IOException, EncodeException {

		ChatEndpoint.chatEndpoints.remove(this);
		Message message = new Message();
		message.setFrom(ChatEndpoint.users.get(session.getId()));
		message.setContent("Disconnected!");
		broadcast(message);
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		// Do error handling here
	}

	private static void broadcast(Message message) 
			throws IOException, EncodeException {

		ChatEndpoint.chatEndpoints.forEach(endpoint -> {
			synchronized (endpoint) {
				try {
					endpoint.session.getBasicRemote().
					sendObject(message);
				} catch (IOException | EncodeException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/* ***** */
	
//	public ChatEndpoint(int port) throws UnknownHostException {
//		super(new InetSocketAddress(port));
//	}
	
//	public static void main(String[] args) throws InterruptedException, IOException {
//		int port = 8887; // 843 flash policy port
//		try { port = Integer.parseInt(args[0]); } 
//		catch (Exception ex) { ; }
//		ChatEndpoint s = new ChatEndpoint(port);
//		s.start();
//		System.out.println("ChatServer started on port: " + s.getPort());
//
//		BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
//		while (true) {
//			String in = sysin.readLine();
//			s.broadcast(in);
//			if (in.equals("exit")) {
//				s.stop(1000);
//				break;
//			}
//		}
//	}
	
//	public static void main(String[] args) {
//        Server server = null;
//        try {
//            server = new Server("localhost", 8080, "/websocket", null, ChatEndpoint.class);
//            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//            server.start();
//            System.out.println("Please press a key to stop the server.");
//            reader.readLine();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } catch (DeploymentException e) {
//            throw new RuntimeException(e);
//        } finally {
//            if (server != null) {
//                server.stop();
//            }
//        }
//	}
}
