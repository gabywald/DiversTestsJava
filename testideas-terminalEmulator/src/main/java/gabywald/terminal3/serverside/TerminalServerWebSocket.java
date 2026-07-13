package gabywald.terminal3.serverside;

import gabywald.terminal3.serverside.wsservers.TerminalServerAuthServer;
import gabywald.terminal3.serverside.wsservers.TerminalServerBasicServer;
import gabywald.terminal3.serverside.wsservers.TerminalServerCommandServer;
import gabywald.utilities.logger.Logger;
import gabywald.utilities.logger.Logger.LoggerLevel;

/**
 * 
 * @author Gabriel Chandesris (2024, 2026)
 */
public class TerminalServerWebSocket extends TerminalServer {
	
	private static Thread thrServer = null;
	
	private TerminalServerBasicServer serverBasic				= null;
	private TerminalServerAuthServer serverAuthentication		= null;
	private TerminalServerCommandServer serverOfServices		= null;
	
	public TerminalServerWebSocket() {
		@SuppressWarnings("unused")
		String serverName		= TerminalServer.getProperty( "gabywald.terminal.server.servername" );
		@SuppressWarnings("unused")
		String serverAUTH 		= TerminalServer.getProperty( "gabywald.terminal.server.authentication" );
		@SuppressWarnings("unused")
		String serverMAIN 		= TerminalServer.getProperty( "gabywald.terminal.server.mainservice" );
		int portAuthentication	= Integer.parseInt(TerminalServer.getProperty("gabywald.terminal.server.port.authentication"));
		int portServices		= Integer.parseInt(TerminalServer.getProperty("gabywald.terminal.server.port.services"));
		this.serverBasic			= new TerminalServerBasicServer(portServices-1);
		this.serverAuthentication	= new TerminalServerAuthServer(portAuthentication);
		this.serverOfServices		= new TerminalServerCommandServer(portServices);
		
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "this.serverBasic---------: {" + this.serverBasic          + "} ");
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "this.serverAuthentication: {" + this.serverAuthentication + "} ");
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "this.serverOfServices----: {" + this.serverOfServices     + "} ");
	}
	
	@Override
	public void run() {
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "TerminalServerWebSocket RUN BEGIN");
		
		this.serverBasic.start();
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "\t serverBasic STARTED !");
		this.serverAuthentication.start();
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "\t serverAuthentication STARTED !");
		this.serverOfServices.start();
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "\t serverOfServices STARTED !");
			
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "TerminalServerWebSocket RUN END");
	}

	@Override
	public void start() {
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "TerminalServerWebSocket Start BEGIN");
		if (TerminalServerWebSocket.thrServer == null) {
			TerminalServerWebSocket.thrServer = new Thread(this);
			TerminalServerWebSocket.thrServer.start();
		}
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "TerminalServerWebSocket Start END");
	}

	@Override
	public void shutdownNow() {
		if (serverBasic != null)			{ this.serverBasic.stop(); }
		if (serverAuthentication != null)	{ this.serverAuthentication.stop(); }
		if (serverOfServices != null)		{ this.serverOfServices.stop(); }
	}
	
}
