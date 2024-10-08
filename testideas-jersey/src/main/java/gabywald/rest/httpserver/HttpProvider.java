package gabywald.rest.httpserver;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpServer;


/**
 * https://www.codeproject.com/tips/1040097/create-simple-http-server-in-java
 * @author gchandesris (20180727)
 */
public class HttpProvider {

	public static void main(String[] args) {

		int port			= 9000;
		HttpServer server	= null;
		try {
			server = HttpServer.create(new InetSocketAddress(port), 0);

			System.out.println("server started at " + port);
			server.createContext("/", new RootHandler());
			server.createContext("/echoHeader", new EchoHeaderHandler());
			server.createContext("/echoGet", new EchoGetHandler());
			server.createContext("/echoPost", new EchoPostHandler());
			server.setExecutor(null);
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void parseQuery(String query, Map<String, Object> parameters) 
			throws UnsupportedEncodingException {

		if (query != null) {
			String pairs[] = query.split("[&]");
			for (String pair : pairs) {
				String param[] = pair.split("[=]");
				String key = null;
				String value = null;
				if (param.length > 0) {
					key = URLDecoder.decode(param[0], 
							System.getProperty("file.encoding"));
				}

				if (param.length > 1) {
					value = URLDecoder.decode(param[1], 
							System.getProperty("file.encoding"));
				}

				if (parameters.containsKey(key)) {
					Object obj = parameters.get(key);
					if (obj instanceof List<?>) {
						@SuppressWarnings("unchecked")
						List<String> values = (List<String>) obj;
						values.add(value);
					} else if (obj instanceof String) {
						List<String> values = new ArrayList<String>();
						values.add((String) obj);
						values.add(value);
						parameters.put(key, values);
					}
				} else {
					parameters.put(key, value);
				}
			}
		}
	}

}
