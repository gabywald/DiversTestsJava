package gabywald.rest.httpserver;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.http.ConnectionClosedException;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpServerConnection;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.apache.http.protocol.UriHttpRequestHandlerMapper;
import org.apache.http.util.EntityUtils;

import gabywald.rest.httpclient.HttpConnection;

public class ApacheHttpServerProvider implements Runnable {

	private final ServerSocket serversocket; 
	private final HttpService httpService; 
	private boolean isRunning = false;

	private ApacheHttpServerProvider(ServerSocket serversocket, HttpService httpService) { 
		this.serversocket	= serversocket;
		this.httpService	= httpService;
	}
	
	public static ApacheHttpServerProvider createWith(	int port, 
														final String baseRoot) 
			throws ApacheHttpServerProviderException {
		ServerSocket serversocket = null;
		try {
			serversocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ApacheHttpServerProviderException( "IOException: {" + e.getMessage() + "}");
		} 

		// Set up the HTTP protocol processor 
		HttpProcessor httpproc = new ImmutableHttpProcessor(new HttpResponseInterceptor[] { 
				new ResponseDate(), 
				new ResponseServer("HTTPinternal/1.1"), 
				new ResponseContent(), 
				new ResponseConnControl() 
		}); 

		// Set up request handlers 
		UriHttpRequestHandlerMapper reqistry = new UriHttpRequestHandlerMapper();
		// TODO place read configuration here !
		reqistry.register("*", new SampleHttpRequestHandler(baseRoot)); 

		// Set up the HTTP service 
		HttpService httpService = new HttpService(httpproc, reqistry);
		
		return new ApacheHttpServerProvider(serversocket, httpService);
	}
	
	public void start() {
		this.isRunning = true;
		Thread ahspTHR = new Thread(this);
		ahspTHR.setDaemon(false);
		ahspTHR.start();
	}
	
	@Override
	public void run() {
		System.out.println("Listening on port " + this.serversocket.getLocalPort()); 
		while (this.isRunning) { // while (!Thread.interrupted()) { 
			try { 
				// Set up HTTP connection 
				Socket socket = this.serversocket.accept(); 
				DefaultBHttpServerConnection conn = new DefaultBHttpServerConnection(8 * 1024); 
				System.out.println("Incoming connection from " + socket.getInetAddress()); 
				conn.bind(socket); 

				// Start worker thread 
				ConnectionOnServer cos = new ConnectionOnServer(this.httpService, conn); 
				cos.start();
			} catch (InterruptedIOException ex) { 
				break; 
			} catch (IOException e) { 
				System.err.println("I/O error initialising connection thread: " 
						+ e.getMessage()); 
				break; 
			} 
		}
	}

//	public static void main (String[] args) {
//
//		// HttpServer server = ServerBootstrap.bootstrap().create();
//
//		int port = 8080;
//
//		try {
//			ServerSocket serversocket	= new ServerSocket(port);
//			HttpProcessor httpproc		= new ImmutableHttpProcessor( 
//					new HttpResponseInterceptor[]{
//							new ResponseDate(), 
//							new ResponseServer("Test/1.1"), 
//							new ResponseContent(), 
//							new ResponseConnControl()
//					});
//			UriHttpRequestHandlerMapper reqistry	= new UriHttpRequestHandlerMapper();
//			reqistry.register("*", new HttpRequestHandler() {
//
//				@Override
//				public void handle(	HttpRequest request, 
//						HttpResponse response, 
//						HttpContext context)
//								throws HttpException, IOException {
//					response.setStatusCode (HttpStatus.SC_OK);
//					response.setEntity (new StringEntity ("0123456789"));
//				}
//
//			});
//			HttpService httpService		= new HttpService(httpproc, reqistry);
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally { ; }
//
//		//		try {
//		//			ServerSocket server		= new ServerSocket();
//		//			SocketAddress sockAddr	= new InetSocketAddress(port);
//		//			server.bind(sockAddr);
//		//			System.out.println("Serveur opérationnel sur le port: "+port);
//		//			String line				= null;
//		//			do {
//		//				Socket client		= server.accept();
//		//				String clientAddr	= client.getInetAddress().getHostAddress();
//		//				System.out.println("Connexion à partir de: "+clientAddr);
//		//				InputStream cis		= client.getInputStream();
//		//				OutputStream cos	= client.getOutputStream();
//		//				byte[] readBuffer	= null;
//		//				do {
//		//					while (cis.available() == 0) { ; }
//		//					readBuffer		= new byte[cis.available()];
//		//					cis.read(readBuffer);
//		//					line			= new String(readBuffer);
//		//					System.out.println(client+" a envoyé : "+line);
//		//					String reply	= "Bien reçu : "+line;
//		//					cos.write(reply.getBytes());
//		//					cos.flush();
//		//				} while ( (!"quit".equalsIgnoreCase(line)) 
//		//							&& (!"shutdown".equalsIgnoreCase(line)) );
//		//				if ("quit".equalsIgnoreCase(line)) {
//		//					cos.close();
//		//					System.out.println("Connexion fermée: "+client);
//		//				}
//		//			} while (!"shutdown".equals(line));
//		//			server.close();
//		//			System.out.println("Serveur arrêté.");
//		//		} catch (IOException e) { e.printStackTrace(); }
//
//	}


	
	public static void main (String[] args) {
		
		try {
			ApacheHttpServerProvider ahsp = ApacheHttpServerProvider.createWith(61337, "/");
			ahsp.start();
			
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
			
		} catch (ApacheHttpServerProviderException e) {
			System.err.println("Cannot instanciate ApacheHttpServerProvider ! {" + e.getMessage() + "}");
		}
	}
	
	public static class SampleHttpRequestHandler implements HttpRequestHandler {
		
		private final String root;
		
		public SampleHttpRequestHandler(String root) {
			this.root = root;
		}

		@Override
		public void handle(	HttpRequest request, 
							HttpResponse response, 
							HttpContext context)
				throws HttpException, IOException {
			
			System.out.println("HANDLER: {" + this.root + "}");
			
			String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
			System.out.println("\t method: {" + method  + "}");
			
			System.out.println("\t REQUEST HEADERS: ");
			Header[] headers = request.getAllHeaders();
			for (int i = 0 ; i < headers.length ; i++) {
				System.out.println("\t\t {" + headers[i].getName() + "} => {" + headers[i].getValue() + "}");
				HeaderElement[] elts = headers[i].getElements();
				for (int j = 0 ; j < elts.length ; j++) {
					System.out.println("\t\t\t {" + elts[j].getName() + "} => {" + elts[j].getValue() + "}" 
							+ " (" + elts[j].getParameterCount() + ")");
				}
			}
			System.out.println("\t REQUEST Protocol Version: {" + request.getProtocolVersion()  + "}");
			System.out.println("\t REQUEST Request Line    : {" + request.getRequestLine()  + "}");
			
			// DONE body !
			// HttpMethod / HttpHeader constants
			// => HttpEntity : { AbstractHttpEntity, BasicHttpEntity, BufferedHttpEntity, ByteArrayEntity, EntityTemplate, FileEntity, HttpEntityWrapper, InputStreamEntity, SerializableEntity, StringEntity }
			if (method.equals("POST" /*HttpMethod.POST*/)) {
				int length = Integer.parseInt( request.getFirstHeader(HttpHeaders.CONTENT_LENGTH).getValue() );
				System.out.println( "\t REQUEST Content-Length [" + length + "]");
				
				if ( (length > 0) && (request instanceof HttpEntityEnclosingRequest) ) { 
					HttpEntity entity 		= ((HttpEntityEnclosingRequest) request).getEntity(); 
					byte[] entityContent	= EntityUtils.toByteArray(entity); 
					System.out.println("\t\t Incoming entity content (bytes): (" + entityContent.length + ")");
					String str = new String(entityContent, StandardCharsets.UTF_8);
					System.out.println("*****\n" + str + "\n*****"); 
				} // END "if ( (length > 0) && (request instanceof HttpEntityEnclosingRequest) )"
			} // END "if (method.equals("POST" /*HttpMethod.POST*/))"
			
			// TODO context ...
			
			response.setStatusCode(HttpStatus.SC_OK); 
			StringEntity body = new StringEntity("<h3>nothing</h3>", ContentType.create("text/html", (Charset) null)); 
			response.setEntity(body); 
		}
		
	}
	
	public class ConnectionOnServer implements Runnable {
		
		private final HttpService httpservice; 
		private final HttpServerConnection conn;
		private boolean isRunning = false;

		public ConnectionOnServer(	final HttpService httpservice, 
									final HttpServerConnection conn) { 
			this.httpservice	= httpservice; 
			this.conn			= conn; 
		} 
		
		public void start() {
			this.isRunning = true;
			Thread cosTHR = new Thread(this);
			// NOTE Daemon here !
			cosTHR.setDaemon( true );
			cosTHR.start();
		}

		@Override 
		public void run() { 
			System.out.println("New connection thread"); 
			HttpContext context = new BasicHttpContext(null); 
			try { 
				while ( (this.isRunning) && (this.conn.isOpen()) ) {
				// while (!Thread.interrupted() && this.conn.isOpen()) {
					this.httpservice.handleRequest(this.conn, context); 
				} 
			} catch (ConnectionClosedException ex) { 
				System.err.println("Client closed connection"); 
			} catch (IOException ex) { 
				System.err.println("I/O error: " + ex.getMessage()); 
			} catch (HttpException ex) { 
				System.err.println("Unrecoverable HTTP protocol violation: " + ex.getMessage()); 
			} finally { 
				try { 
					this.conn.shutdown(); 
				} catch (IOException ignore) 
					{ ; } 
			} 
		} 
	}

}
