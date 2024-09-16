package gabywald.rest.jersey;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import gabywald.rest.httpclient.HttpConnection;

// import org.glassfish.jersey.servlet.ServletContainer;

public class JerseyLauncher {

	public static void main (String[] args) {
		int port				= 9090;
		URI baseUri				= UriBuilder.fromUri("http://localhost/").port(port).build();
		ResourceConfig config	= new ResourceConfig(); // new ResourceConfig(HelloWorldResource.class);
		
		config.packages("gabywald.rest.jersey");
		
//		Application app = new Application() {
//            @Override
//            public Set<Class<?> > getClasses() {
//                Set<Class<?> > res = new HashSet<Class<?> >();
//                res.add(org.example.MainResource.class);
//                return res;
//            }
//        };
//        config.add(app);

		com.sun.net.httpserver.HttpServer httpServer = 
				JdkHttpServerFactory.createHttpServer(baseUri, config, true);
		
		// httpServer.start();
		
//		HttpServer server		= ServerBootstrap.bootstrap() // JdkHttpServerFactory.createHttpServer(baseUri, config);
//				.setListenerPort(port)
//				// TODO
//				.registerHandler(pattern, handler)
//				.create();
		
		System.out.println("hc0");
		HttpConnection hc0 = new HttpConnection(true, "http://localhost:9090/helloworld");
		hc0.setRequestProperty(HttpHeaders.ACCEPT, ContentType.WILDCARD.getMimeType());
		hc0.sendRequest( null, null );
		System.out.println(hc0.getResponse());
		
		System.out.println("hc1");
		HttpConnection hc1 = new HttpConnection(true, "http://localhost:9090/hello");
		hc1.setRequestProperty(HttpHeaders.ACCEPT, ContentType.WILDCARD.getMimeType());
		hc1.sendRequest( null, null );
		System.out.println(hc1.getResponse());
		
	}

	//	public static void main (String[] args) {
	//        // Jersey
	//        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
	//        ServletContainer jerseyServletContainer = new ServletContainer(new AppResourceConfig());
	//        ServletHolder jerseyServletHolder = new ServletHolder(jerseyServletContainer);
	//        servletContextHandler.setContextPath("/");
	//        servletContextHandler.addServlet(jerseyServletHolder, "/api/*");
	//
	//        // Wire up Jetty
	//        HandlerCollection handlerList = new HandlerCollection();
	//        handlerList.setHandlers(new Handler[]{ servletContextHandler });
	//        Server server = new Server(configuration.getInt("Server.Port"));
	//        server.setHandler(handlerList);
	//        server.start();
	//        server.join();
	//	}
	//	
	//	public class AppResourceConfig extends ResourceConfig {
	//	    public AppResourceConfig() {
	//	        register(new AppBinder());
	//	        packages("org.sandbox.resources");
	//	    }
	//	}
	//	
	//	public class AppBinder extends AbstractBinder {
	//	    @Override
	//	    protected void configure() {
	//	        bind(new StringService()).to(StringService.class);
	//	    }
	//	}
}
