package gabywald.jersey;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.sun.net.httpserver.HttpServer;

public class JerseyLauncher {
	
	private int port = 8080;
	private String baseURL = null;
	private ResourceConfig config = null;
	
	private boolean start = true;
	
	private HttpServer httpServer = null;
	
	public JerseyLauncher(int port, String baseURL, boolean start, ResourceConfig config) {
		this.port = port;
		this.baseURL = baseURL;
		
//		StringBuilder sbURI = new StringBuilder();
//		sbURI.append("http://").append(this.baseURL).append(":").append(this.port).append("/");
//		URI baseUri		= URI.create(sbURI.toString()); 
		
		StringBuilder sbURII = new StringBuilder();
		sbURII.append("http://").append(this.baseURL).append("/");
		URI baseUri2	= UriBuilder.fromUri(sbURII.toString()).port(this.port).build();
		
		// ***** this.config.packages(packages);
//		this.config	= new JerseyMainActivator(packages);
//		this.config.register(new BinderAuthentication2Token());
//		this.config.register(new BinderAuthentication2Login());
		this.config = config;
		
		this.start = start;
		
		this.httpServer = JdkHttpServerFactory.createHttpServer(baseUri2, this.config, start);
		
		System.out.println(this.httpServer.getAddress());
		
		System.out.println("this.config.getClasses(): (" + this.config.getClasses().size() + ")");
		for (Class<?> classe : this.config.getClasses()) {
			System.out.println("\t" + classe.toGenericString());
		}
		
	}
	
	public void start() {
		if (!this.start) {
			this.httpServer.start();
			this.start = true;
		}
	}
	
	public void stop() {
		if (this.start) {
			this.httpServer.stop(0);
		}
	}

}
