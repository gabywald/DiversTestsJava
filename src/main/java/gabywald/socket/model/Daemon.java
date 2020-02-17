package gabywald.socket.model;

// import gabywald.socket.view.FrameDaemon;

import gabywald.utilities.logger.Logger;
import gabywald.utilities.logger.Logger.LoggerLevel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * 
 * @author Gabriel Chandesris (2013, 2015)
 */
public class Daemon implements Runnable {
	private DaemonClientThread[] tabClient;
	protected int port;
	protected String out;
	
	protected ServerSocket server;
	
	public Daemon(int atta) {
		this.port	= atta;
		this.out	= new String("");
		
		try {
			this.server				= new ServerSocket();
			SocketAddress sockAddr	= new InetSocketAddress(this.port);
			this.server.bind(sockAddr);
			this.out = "Serveur opérationnel sur le port: "+this.port;
			this.tabClient = new DaemonClientThread[0];
		} catch (IOException e) {
			/** e.printStackTrace(); */
			this.out = new String("Transmission error (daemon:constructor) !");
		}
	}
	
	public void start() {
		Thread thrThis = new Thread(this);
		thrThis.start();
	}
	
	public void run() {
		try {
			do {
				Socket client						= this.server.accept();
				DaemonClientThread newDaemonClient	= new DaemonClientThread(client, this);
				this.addClient(newDaemonClient);
				newDaemonClient.start(); // this.getLastClient().start();
			} while(true);
		} catch (IOException e) {
			/** e.printStackTrace(); */
			this.out = new String("Transmission error (daemon:run) !");
		}
	}
	
	protected void addClient(DaemonClientThread client) {
		DaemonClientThread[] nextTabClient = new DaemonClientThread[this.tabClient.length+1];
		for (int i = 0 ; i < this.tabClient.length ; i++) 
			{ nextTabClient[i] = this.tabClient[i]; }
		nextTabClient[this.tabClient.length] = client;
		this.tabClient = nextTabClient;
	
		Logger.printlnLog(LoggerLevel.LL_INFO, "Connection from [" + client.getHost() + ":" + client.getPort() + "]");
		
	}
	
	public void remClient(DaemonClientThread client) {
		DaemonClientThread[] nextTabClient = new DaemonClientThread[this.tabClient.length-1];
		int i = 0;
		for ( ; (i < this.tabClient.length) 
						&& (!this.tabClient[i].equals(client)) ; i++) 
			{ nextTabClient[i] = this.tabClient[i]; }
		i++;
		for ( ; (i < this.tabClient.length) ; i++) 
			{ nextTabClient[i] = this.tabClient[i]; }
		this.tabClient = nextTabClient;
		
		Logger.printlnLog(LoggerLevel.LL_INFO, "Close connection [" + client.getHost() + ":" + client.getPort() + "]");
	}
	
//	private DaemonClientThread getLastClient() {
//		if (this.tabClient.length > 0) 
//			{ return this.tabClient[this.tabClient.length-1]; } 
//		else  { return null; }
//	}
	
	public String getOutput() { return this.out; }
	
}
