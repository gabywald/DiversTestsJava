package gabywald.websocket.onemoretime.server;

import org.glassfish.tyrus.server.Server;

/**
 * @author Gabriel Chandesris (2026)
 */
public class CommandServer {
	private Server server;
	private final int port;

	public CommandServer(int port) {
		this.port = port;
	}

	public void start() {
		this.server = new Server("localhost", this.port, "/", null, CommandEndpoint.class);
		try {
			this.server.start();
			System.out.println("Command Server started on port " + this.port);
		} catch (Exception e) {
			System.err.println("Failed to start Command Server: " + e.getMessage());
		}
	}

	public void stop() {
		if (this.server != null) {
			this.server.stop();
		}
	}
}
