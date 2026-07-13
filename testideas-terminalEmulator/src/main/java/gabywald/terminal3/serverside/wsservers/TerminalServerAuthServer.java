package gabywald.terminal3.serverside.wsservers;

import org.glassfish.tyrus.server.Server;

import gabywald.utilities.logger.Logger;
import gabywald.utilities.logger.Logger.LoggerLevel;

/**
 * @author Gabriel Chandesris (2026)
 */
public class TerminalServerAuthServer {
	private Server server;
	private final int port;

	public TerminalServerAuthServer(int port) {
		this.port = port;
	}

	public void start() {
		this.server = new Server("localhost", this.port, "/", null, TerminalServerAuthEndpoint.class);
		try {
			this.server.start();
			Logger.printlnLog(LoggerLevel.LL_DEBUG, "Auth Server started on port [" + this.port + "]");
		} catch (Exception e) {
			Logger.printlnLog(LoggerLevel.LL_ERROR, "Failed to start Auth Server: {" + e.getMessage() + "}");
		}
	}

	public void stop() {
		if (this.server != null) 
			{ this.server.stop(); }
	}

}
