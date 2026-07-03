package gabywald.launchers;

import gabywald.utilities.logger.Logger;
import gabywald.utilities.logger.Logger.LoggerLevel;
import picocli.CommandLine;

/**
 * 
 * @author Gabriel Chandesris (2026)
 */
public class TerminalEmulatorLaunchers {

	public static void main(String[] args) {
		TerminalEmulatorCommand tec = new TerminalEmulatorCommand();
		int exitCode = new CommandLine(tec).execute(args);
		// System.exit(exitCode);
		if (tec.isLogEnabled(TerminalEmulatorCommand.LogLevel.TheEnum.info)) 
					{ Logger.printlnLog(LoggerLevel.LL_FORUSER, exitCode + "" ); }
	}
	
}
