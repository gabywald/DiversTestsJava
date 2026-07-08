package gabyxald.terminal3;

import gabywald.terminal3.clientside.TerminalClient;
import gabywald.terminal3.clientside.gui.TerminalFrame;
import gabywald.terminal3.serverside.TerminalServer;
import gabywald.utilities.others.PropertiesLoader;

/**
 * 
 * @author Gabriel Chandesris (2026)
 */
public class MainTerminal3 {
	private PropertiesLoader plClient = new PropertiesLoader("terminalemulatorClient.properties");
	private PropertiesLoader plServer = new PropertiesLoader("terminalemulatorServer.properties");

	public static void main(String[] args) {
		MainTerminal3 mt = new MainTerminal3();
		mt.plClient.getProperties().list(System.out);
		mt.plServer.getProperties().list(System.out);
		
		Thread thrServer = new Thread(TerminalServer.getInstance());
		thrServer.start();
		
		TerminalFrame.getInstance().setVisible(true);
		
		TerminalClient.getInstance();
	}

}
