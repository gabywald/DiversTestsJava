package gabywald.socket.model;

//import gabywald.socket.controller.DaemonClientThreadObserver;

import gabywald.global.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Observable;

/**
 * 
 * @author Gabriel Chandesris (2013, 2015)
 */
public class DaemonClientThread extends Observable implements Runnable {
	private Daemon mot;
	private Socket soc;
	private String inn;
	private JSONObject out;
	// private boolean show;

	public DaemonClientThread(Socket s, Daemon mother)	{
		this.mot	= mother;
		this.soc	= s;
		this.inn	= new String("");
		this.out	= new JSONObject();
		// this.show	= true;
		this.addObserver(new DaemonClientThreadObserver());
	}
	
	private void changeAndNotify(JSONObject output) {
		this.out = output;
		this.setChanged();
		this.notifyObservers();
	}
	
	private void changeAndNotify(String output) {
		JSONObject tmpJSONobj = new JSONObject();
		tmpJSONobj.put("chgmsgdaem", output);
		this.changeAndNotify(tmpJSONobj);
	}
	
//	private void changeAndNotifyError(String output) {
//		this.out.put("error", output);
//		this.changeAndNotify(this.out);
//	}
	
	public void start() {
		Thread thrThis = new Thread(this);
		thrThis.start();
	}
	
	public void stop() {
		this.inn = "exit";
	}
	
	public void run() {
		try {
			String clientAddr	= this.soc.getInetAddress().getHostAddress();
			this.changeAndNotify("Connexion a partir de: "+clientAddr);
			InputStream cis		= this.soc.getInputStream();
			OutputStream cos	= this.soc.getOutputStream();
			byte[] readBuffer	= null;
			do {
				while (cis.available() == 0) { ; }
				readBuffer		= new byte[cis.available()];
				cis.read(readBuffer);
				this.inn		= new String(readBuffer);
				this.changeAndNotify(this.soc+" a envoyé : ["+this.inn+"]");
				JSONObject toSendToClient = this.treatment();
				// toSendToClient.put("returning", "Bien reçu : ["+this.inn+"]");
				cos.write(toSendToClient.toJSON().getBytes());
				cos.flush();
			} while ( (!"exit".equalsIgnoreCase(this.inn)) 
							&& (!"shutdown".equalsIgnoreCase(this.inn)) );
			if ("exit".equalsIgnoreCase(this.inn)) {
				cos.close();
				this.changeAndNotify("Connexion fermée: "+this.soc);
				this.mot.remClient(this);
			} // END "if ("quit".equalsIgnoreCase(this.inn))"s
			if ("shutdown".equalsIgnoreCase(this.inn)) {
				this.mot.remClient(this);
				this.changeAndNotify("Serveur arrêté. ");
				System.exit(0);
			} // END "if ("shutdown".equalsIgnoreCase(this.inn))"
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	public String getOutput() { 
		this.out.put("src", "["+this.getPort()+"]");
		return this.out.toJSON(); 
	}
	
	public String getHost()		
		{ return this.soc.getInetAddress().getHostAddress(); }
	
	public String getPort()		
		{ return this.soc.getPort()+""; }
	
	/**
	 * This method HAS TO be overrided by inheritant classes !
	 */
	public JSONObject treatment() {
		String toReturn				= "... treament ...";
		JSONObject outputJSONobj	= new JSONObject();
		// TODO in inheritant !
		outputJSONobj.put("toclient", toReturn);
		return outputJSONobj;
	}
	
	protected String getInput() 
		{ return this.inn; }
	
	protected Daemon getParent() 
		{ return this.mot; }
}
