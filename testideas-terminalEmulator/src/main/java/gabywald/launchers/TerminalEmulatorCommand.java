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

	/**
	 * Code Command. 
	 */
	static class Version {
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
	Version codVersion = new Version();
	
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
		if (codVersion.isVersion1()) { 
			Logger.printlnLog(LoggerLevel.LL_FORUSER, "Terminal Emulator Version 1. "); 
			gabywald.terminal.gui.TerminalFrame frame = new gabywald.terminal.gui.TerminalFrame();
	        frame.setVisible(true);
	    }
		else if (codVersion.isVersion2()) { 
			Logger.printlnLog(LoggerLevel.LL_FORUSER, "Terminal Emulator Version 2. ");
			new TerminalEmulator();
		}
		else if (codVersion.isVersion3()) { 
			Logger.printlnLog(LoggerLevel.LL_FORUSER, "Terminal Emulator Version 3. ");
			Logger.printlnLog(LoggerLevel.LL_FORUSER, "\t Launching Server... ");
			Thread thrServer = new Thread(TerminalServer.getInstance());
			thrServer.start();
			Logger.printlnLog(LoggerLevel.LL_FORUSER, "\t Launching GUI... ");
			gabywald.terminal3.clientside.gui.TerminalFrame.getInstance().setVisible(true);
			Logger.printlnLog(LoggerLevel.LL_FORUSER, "\t Launching Client... ");
			TerminalClient.getInstance();
		}
		else
			{ Logger.printlnLog(LoggerLevel.LL_WARNING, "Terminal Emulator UNKNOWN VERSION. "); }
	}
	
}
