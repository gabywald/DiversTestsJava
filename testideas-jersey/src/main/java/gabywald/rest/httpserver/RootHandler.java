package gabywald.rest.httpserver;

import java.io.IOException;
import java.io.OutputStream;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * https://www.codeproject.com/tips/1040097/create-simple-http-server-in-java
 * @author Gabriel Chandesris (20180727)
 */
public class RootHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange he) throws IOException {
		String response = "<h1>Server start success if you see this message</h1>"; // + "<h1>Port: " + port + "</h1>";
		he.sendResponseHeaders(200, response.length());
		OutputStream os = he.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
}
//public class RootHandler implements HttpRequestHandler {
//
//	@Override
//	public void handle(HttpRequest request, HttpResponse response, HttpContext context)
//			throws HttpException, IOException {
//		String responseSTR = "<h1>Server start success if you see this message</h1>";
//
//		// TODO show content of "Header[] headers = request.getAllHeaders();"
//		
//		response.setStatusCode( HttpStatus.SC_OK );
//		response.setEntity( new StringEntity(responseSTR, "utf-8") );
//	}
//}
