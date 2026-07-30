package gabywald.websocket.onemoretime.server;

import org.glassfish.tyrus.server.Server;
import java.util.UUID;

/**
 * @author Gabriel Chandesris (2026)
 */
public class AuthServer {
	private Server server;
	private final int port;

	public AuthServer(int port) {
		this.port = port;
	}

	public void start() {
		this.server = new Server("localhost", this.port, "/", null, AuthEndpoint.class);
		try {
			this.server.start();
			System.out.println("Auth Server started on port " + this.port);
		} catch (Exception e) {
			System.err.println("Failed to start Auth Server: " + e.getMessage());
		}
	}

	public void stop() {
		if (this.server != null) {
			this.server.stop();
		}
	}

	// Génère un token unique
	public static String generateToken() {
		return UUID.randomUUID().toString();
	}
}
