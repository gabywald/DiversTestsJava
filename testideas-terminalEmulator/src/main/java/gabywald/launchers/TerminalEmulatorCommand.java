package gabywald.launchers;

import gabywald.terminal2.TerminalEmulator;
import gabywald.terminal3.clientside.TerminalClient;
import gabywald.terminal3.serverside.TerminalServer;
import gabywald.utilities.logger.Logger;
import gabywald.utilities.logger.Logger.LoggerLevel;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * 
 * @author Gabriel Chandesris (2026)
 */
@Command(
		name = "testideas-terminalemulator",
		version = "1.0",
		description = "Application CLI with picocli.", 
		// subcommands = { EncodeCommand.class, DecodeCommand.class }, 
		mixinStandardHelpOptions = true)
public class TerminalEmulatorCommand implements Runnable {

	static class VersionOption {
		enum TheEnum { version1, version2, version3 }

		TheEnum actualValue = TheEnum.version3;

		@Option(names = {"-v1", "--version1"}, 
				description = "First Version of Terminal Emulator. ")
		void setVersion1(boolean b) { this.actualValue = TheEnum.version1; }
		
		@Option(names = {"-v2", "--version2"}, 
				description = "Second Version of Terminal Emulator. ")
		void setVersion2(boolean b) { this.actualValue = TheEnum.version2; }
		
		@Option(names = {"-v3", "--version3"}, 
				description = "Third Version of Terminal Emulator. ")
		void setVersion3(boolean b) { this.actualValue = TheEnum.version3; }

		boolean isVersion1()	{ return (this.actualValue == TheEnum.version1); }
		boolean isVersion2()	{ return (this.actualValue == TheEnum.version2); }
		boolean isVersion3()	{ return (this.actualValue == TheEnum.version3); }
	}
	@ArgGroup(exclusive = true, heading = "Version Options%n", multiplicity = "1")
	VersionOption vOption = new VersionOption();
	
	static class OptionsClientServer {
		enum TheEnum { onlyClient, onlyServer, both }

		TheEnum actualValue = TheEnum.both;

		@Option(names = {"-c", "--onlyclient"}, 
				description = "(Apply on v3 only) Only Client Execution. ")
		void setOnlyClient(boolean b) { this.actualValue = TheEnum.onlyClient; }
		
		@Option(names = {"-s", "--onlyserver"}, 
				description = "(Apply on v3 only) Only Server Execution. ")
		void setOnluServer(boolean b) { this.actualValue = TheEnum.onlyServer; }
		
		@Option(names = {"-b", "--both"}, 
				description = "(Apply on v3 only) Server and Client Execution. ")
		void setBOTH(boolean b) { this.actualValue = TheEnum.both; }

		boolean isOnlyClient()	{ return (this.actualValue == TheEnum.onlyClient); }
		boolean isOnlyServer()	{ return (this.actualValue == TheEnum.onlyServer); }
		boolean isBoth()		{ return (this.actualValue == TheEnum.both); }
	}
	@ArgGroup(exclusive = true, heading = "(Apply on v3 only) Client/Server Options%n", multiplicity = "0..1") // multiplicity = "1")
	OptionsClientServer csOptions = new OptionsClientServer();
	
	static class OptionRESTorWebSocket {
		enum TheEnum { restExchange, webSocketExchange }

		TheEnum actualValue = TheEnum.restExchange;

		@Option(names = {"-r", "--rest"}, 
				description = "(Apply on v3 only) Client / Server exchanges on REST mode. ")
		void setRestExchange(boolean b) { this.actualValue = TheEnum.restExchange; }
		
		@Option(names = {"-w", "--websocket"}, 
				description = "(Apply on v3 only) Client / Server exchanges on Web Socket mode. ")
		void setWebSocketExchanges(boolean b) { this.actualValue = TheEnum.webSocketExchange; }
		
		boolean isRestExchange()	{	 return (this.actualValue == TheEnum.restExchange); }
		boolean isWebSocketExchanges()	{ return (this.actualValue == TheEnum.webSocketExchange); }
	}
	@ArgGroup(exclusive = true, heading = "(Apply on v3 only) REST/WebSocket Options%n", multiplicity = "0..1") // multiplicity = "1")
	OptionRESTorWebSocket rwOptions = new OptionRESTorWebSocket();
	
	/**
	 * Log Level. 
	 */
	public static class LogLevel {
		// public enum TheEnum { none, error, warn, info, debug, trace }
		public enum TheEnum { trace, debug, info, warn, error, none }

		TheEnum actualValue = TheEnum.none;

		@Option(names = "--debug", description = "Sets log level to DEBUG.")
		void setDebug(boolean b)	{ this.actualValue = TheEnum.debug; }

		@Option(names = "--info", description = "Sets log level to INFO.")
		void setInfo(boolean b)		{ this.actualValue = TheEnum.info; }

		@Option(names = "--warn", description = "Sets log level to WARN.")
		void setWarn(boolean b)		{ this.actualValue = TheEnum.warn; }
		
		@Option(names = "--error", description = "Sets log level to ERROR.")
		void setError(boolean b)	{ this.actualValue = TheEnum.error; }
		
		@Option(names = "--trace", description = "Sets log level to NONE.")
		void setTrace(boolean b)	{ this.actualValue = TheEnum.trace; }
		
		@Option(names = "--none", description = "Sets log level to NONE.")
		void setNone(boolean b)		{ this.actualValue = TheEnum.none; }
		
	}
	@ArgGroup(exclusive = true, heading = "Log Level Options%n", multiplicity = "0..1")
	LogLevel logLevel = new LogLevel();
	
	public boolean isLogEnabled(LogLevel.TheEnum checkValue) {
		return checkValue.ordinal() >= logLevel.actualValue.ordinal();
	}
	
	public LogLevel.TheEnum setLogLevel(LogLevel.TheEnum nextValue) { 
		LogLevel.TheEnum prevValue = this.logLevel.actualValue;
		this.logLevel.actualValue = nextValue;
		return prevValue;
	}
	
	@Override
	public void run() {
		if (this.vOption.isVersion1()) { 
			Logger.printlnLog(LoggerLevel.LL_FORUSER, "Terminal Emulator Version 1. "); 
			gabywald.terminal.gui.TerminalFrame frame = new gabywald.terminal.gui.TerminalFrame();
	        frame.setVisible(true);
	    }
		else if (this.vOption.isVersion2()) { 
			Logger.printlnLog(LoggerLevel.LL_FORUSER, "Terminal Emulator Version 2. ");
			new TerminalEmulator();
		}
		else if (this.vOption.isVersion3()) { 
			Logger.printlnLog(LoggerLevel.LL_FORUSER, "Terminal Emulator Version 3. ");
			if ( (this.csOptions.isOnlyServer()) || (this.csOptions.isBoth()) ) {
				Logger.printlnLog(LoggerLevel.LL_FORUSER, "\t Launching Server... ");
				TerminalServer ts = TerminalServer.builder( this.rwOptions.isRestExchange() );
				ts.start(); // Starting server !!
			}
			if ( (this.csOptions.isOnlyClient()) || (this.csOptions.isBoth()) ) {
				Logger.printlnLog(LoggerLevel.LL_FORUSER, "\t Launching GUI... ");
				gabywald.terminal3.clientside.gui.TerminalFrame.getInstance().setVisible(true);
				Logger.printlnLog(LoggerLevel.LL_FORUSER, "\t Launching Client... ");
				/* TerminalClient tc = */TerminalClient.builder( this.rwOptions.isRestExchange() );
			}
		}
		else
			{ Logger.printlnLog(LoggerLevel.LL_WARNING, "Terminal Emulator UNKNOWN VERSION. "); }
	}
	
}
