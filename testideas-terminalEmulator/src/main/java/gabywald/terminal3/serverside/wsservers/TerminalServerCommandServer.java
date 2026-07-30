package gabywald.terminal3.serverside.wsservers;

import org.glassfish.tyrus.server.Server;

import gabywald.utilities.logger.Logger;
import gabywald.utilities.logger.Logger.LoggerLevel;

/**
 * @author Gabriel Chandesris (2026)
 */
public class TerminalServerCommandServer {
	private Server server;
	private final int port;

	public TerminalServerCommandServer(int port) {
		this.port = port;
	}

	public void start() {
		this.server = new Server("localhost", this.port, "/", null, TerminalServerCommandEndpoint.class);
		try {
			this.server.start();
			Logger.printlnLog(LoggerLevel.LL_DEBUG, "Command Server started on port [" + this.port + "]");
		} catch (Exception e) {
			Logger.printlnLog(LoggerLevel.LL_ERROR, "Failed to start Command Server: {" + e.getMessage() + "}");
		}
	}

	public void stop() {
		if (this.server != null) 
			{ this.server.stop(); }
	}
}
