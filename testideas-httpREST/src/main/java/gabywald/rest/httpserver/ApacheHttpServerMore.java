package gabywald.rest.httpserver;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import gabywald.rest.httpclient.HttpConnection;

public class ApacheHttpServerMore {

	private final HttpServer httpServer;

	public ApacheHttpServerMore(int port, 
								final String baseRoot) {
		this.httpServer = ServerBootstrap.bootstrap()
				.setListenerPort(port)
				.registerHandler("/favicon.ico", new EmptyHandler())
				.registerHandler("*", new MyHandler())
				.create();
	}
	
	public void start() throws IOException {
		this.httpServer.start();
	}
	
	public void stop() throws IOException {
		this.httpServer.stop();
	}

	/**
	 * A simple handler sleep 10 seconds
	 */
	private static class MyHandler implements HttpRequestHandler {
		@Override
		public void handle(	HttpRequest httpRequest, 
				HttpResponse httpResponse, 
				HttpContext httpContext) 
						throws HttpException, IOException {

			String uri = httpRequest.getRequestLine().getUri();
			System.out.println(Instant.now().toString() + ": " + this.toString() + " " + uri + " Start");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException ex) 
				{ ; }
			System.out.println(Instant.now().toString() + ": " + this.toString() + " " + uri + " Finish");
		}
	}

	/**
	 * Deals with the /favicon.ico
	 */
	private static class EmptyHandler implements HttpRequestHandler {
		@Override
		public void handle(	HttpRequest httpRequest, 
				HttpResponse httpResponse, 
				HttpContext httpContext) 
						throws HttpException, IOException {
			;
		}
	}

	public static void main (String[] args) {
		ApacheHttpServerMore ahsm = new ApacheHttpServerMore(61337, "/");
		try {
			ahsm.start();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		System.out.println("hc0");
		HttpConnection hc0 = new HttpConnection(true, "http://localhost:61337/");
		hc0.setRequestProperty(HttpHeaders.ACCEPT, ContentType.WILDCARD.getMimeType());
		hc0.sendRequest( null, null );
		System.out.println(hc0.getResponse());
		
		System.out.println("hc1");
		HttpConnection hc1 = new HttpConnection(true, "http://localhost:61337/SpringTPRest/");
		hc1.setRequestProperty(HttpHeaders.ACCEPT, ContentType.WILDCARD.getMimeType());
		hc1.sendRequest( null, null );
		System.out.println(hc1.getResponse());
		
		System.out.println("hc2");
		HttpConnection hc2 = new HttpConnection(true, "http://localhost:61337/SpringTPRest/home");
		hc2.setRequestProperty(HttpHeaders.ACCEPT, ContentType.WILDCARD.getMimeType());
		hc2.sendRequest( null, null );
		System.out.println(hc2.getResponse());
		
		System.out.println("hc3");
		HttpConnection hc3 = new HttpConnection(false, "http://localhost:61337/SpringTPRest/datas/FN-8176");
		hc3.setRequestProperty(HttpHeaders.ACCEPT, ContentType.WILDCARD.getMimeType());
		hc3.sendRequest( null, null );
		System.out.println(hc3.getResponse());
		
		System.out.println("hc4");
		HttpConnection hc4 = new HttpConnection(true, "http://localhost:61337/SpringTPRest/custom");
		hc4.setRequestProperty(HttpHeaders.ACCEPT, ContentType.WILDCARD.getMimeType());
		Map<String, String> params = new HashMap<String, String>();
		for (int i = 0 ; i < 3 ; i++) 
			{ params.put("key"+i, "value"+i); }
		hc4.sendRequest( null, params );
		System.out.println(hc4.getResponse());
		
		System.out.println("hc5");
		HttpConnection hc5 = new HttpConnection(false, "http://localhost:61337/SpringTPRest/objectKey");
		hc5.setRequestProperty(HttpHeaders.ACCEPT, ContentType.WILDCARD.getMimeType());
		hc5.setContentType(ContentType.APPLICATION_JSON.getMimeType());
		String data = "{\"username\":\"xyz\",\"password\":\"12345\"}";
		hc5.sendRequest( data, null );
		System.out.println(hc5.getResponse());
	}
}
