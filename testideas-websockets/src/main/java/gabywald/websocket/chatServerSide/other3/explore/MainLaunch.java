package gabywald.websocket.chatServerSide.other3.explore;

import org.glassfish.tyrus.server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.websocket.DeploymentException;

/**
 * 
 * @author Gabriel Chandesris (2024)
 */
public class MainLaunch {

	// // curl -i -N -H "Connection: Upgrade" -H "Upgrade: websocket" -H "Sec-WebSocket-Version: 13" http://localhost:8080/websocket
	// // curl -i -N -H "Connection: Upgrade" -H "Upgrade: websocket" -H "Sec-WebSocket-Version: 13" http://localhost:8080/websocket/server
	// // "Sec-WebSocket-Key: SGVsbG8sIHdvcmxkIQ==" => 'Hello, world!' (cf. echo "..." | base64 -d
	// curl -i -N -H "Connection: Upgrade" -H "Upgrade: websocket" -H "Sec-WebSocket-Version: 13" -H "Sec-WebSocket-Key: SGVsbG8sIHdvcmxkIQ==" http://localhost:8080/websocket/server
	// curl -i -N -H "Connection: Upgrade" -H "Upgrade: websocket" -H "Sec-WebSocket-Version: 13" -H "Sec-WebSocket-Key: SGVsbG8sIHdvcmxkIQ==" --header "Content-Type: application/json" --data '{"message": "Hello, WebSocket!"}' http://localhost:8080/websocket/server
	// NOTE : contextPath porté par annotation classe Server
	// https://www.jmdoudoux.fr/java/dej/chap-websockets.htm
	// https://www.videosdk.live/developer-hub/websocket/curl-websocket
	// https://developer.mozilla.org/en-US/docs/Web/HTTP/Protocol_upgrade_mechanism
	// curl --include --no-buffer --header "Content-Type: application/json" --data '{"message": "Hello, WebSocket!"}' http://localhost:8080/websocket
	// curl -i -N -H "Connection: Upgrade" -H "Upgrade: websocket" --header "Content-Type: application/json" --data '{"message": "Hello, WebSocket!"}' http://localhost:8080/websocket
	// https://stackoverflow.com/questions/42324473/http-1-1-426-upgrade-required
	// https://gist.github.com/htp/fbce19069187ec1cc486b594104f01d0?permalink_comment_id=2396440

	public static void main(String[] args) {
		runServer();
	}

	private static void runServer() {
		Server server = null;
		try {
			server = new Server("localhost", 8080, "", null, WebSocketServer2.class);
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			server.start();
			System.out.println("Please press a key to stop the server.");
			// reader.readLine()
			String data = null;
			do {
				data = reader.readLine();
				System.out.println("'" + data + "'");
			} while ( ! data.equals("quit"));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DeploymentException e) {
			e.printStackTrace();
		} finally {
			if (server != null) { server.stop(); }
		}
	}
}
