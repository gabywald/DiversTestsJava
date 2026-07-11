package gabywald.terminal3.serverside;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;

import gabywald.terminal3.serverside.restmodules.BasicAuthFilter;
import gabywald.terminal3.serverside.restmodules.BearerAuthFilter;
import gabywald.terminal3.serverside.restmodules.TokenGenerator;
import gabywald.terminal3.serverside.restmodules.TokenUseCase;
import gabywald.utilities.others.PropertiesLoader;

/**
 * 
 * @author Gabriel Chandesris (2026)
 */
public class TerminalServer implements Runnable {
	
	private static TerminalServer instance = null;
	private static Thread thrServer = null;
	
	public static TerminalServer getInstance() {
		if (TerminalServer.instance == null) 
			{ TerminalServer.instance = new TerminalServer(); }
		return TerminalServer.instance;
	}
	
    public final String BASE_PATH = "http://" + TerminalServer.plServer.getProperty("gabywald.terminal.server.servername") + "/";
    public final URI BASE_URI_TOKENGENERATOR = UriBuilder.fromUri( BASE_PATH )
    		.port( Integer.parseInt(TerminalServer.plServer.getProperty("gabywald.terminal.server.port.authentication")) ).build();
    public final URI BASE_URI_TOKENUSERUSAGE = UriBuilder.fromUri( BASE_PATH )
    		.port( Integer.parseInt(TerminalServer.plServer.getProperty("gabywald.terminal.server.port.services")) ).build();
    private HttpServer serverAuthentication = null;
    private HttpServer serverOfServices = null;
	
	private static PropertiesLoader plServer = new PropertiesLoader("terminalemulatorServer.properties");
	
	public static String getProperty(String key) 
		{ return TerminalServer.plServer.getProperty(key); }
	
	private TerminalServer() {
        ResourceConfig rcAuthentication = new ResourceConfig();
        rcAuthentication.registerClasses(BasicAuthFilter.class);
        rcAuthentication.registerClasses(TokenGenerator.class);
        rcAuthentication.property(LoggingFeature.LOGGING_FEATURE_LOGGER_LEVEL_SERVER, Level.WARNING.getName());
        this.serverAuthentication = GrizzlyHttpServerFactory.createHttpServer(BASE_URI_TOKENGENERATOR, rcAuthentication);
        
        ResourceConfig rcServices = new ResourceConfig();
        rcServices.registerClasses(BearerAuthFilter.class);
        rcServices.registerClasses(TokenUseCase.class);
        rcServices.property(LoggingFeature.LOGGING_FEATURE_LOGGER_LEVEL_SERVER, Level.WARNING.getName());
        this.serverOfServices = GrizzlyHttpServerFactory.createHttpServer(BASE_URI_TOKENUSERUSAGE, rcServices);
	}

	@Override
	public void run() {
		try {
			this.serverAuthentication.start();
			this.serverOfServices.start();
		} catch (IOException e) { e.printStackTrace(); }
	}
	
//	public void shutdown() {
//		this.serverAuthentication.shutdown();
//		this.serverOfServices.shutdown();
//	}
	
	public void start() {
		if (TerminalServer.thrServer == null) {
			TerminalServer.thrServer = new Thread(this);
			TerminalServer.thrServer.start();
		}
	}
	
	public void shutdownNow() {
		if (TerminalServer.thrServer != null) {
			this.serverAuthentication.shutdownNow();
			this.serverOfServices.shutdownNow();
			TerminalServer.thrServer = null;
		}
	}
	
}
