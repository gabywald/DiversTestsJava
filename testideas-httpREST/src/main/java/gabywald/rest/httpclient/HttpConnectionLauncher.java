package gabywald.rest.httpclient;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;

public class HttpConnectionLauncher {

	public static void main (String[] args) {
		
		System.out.println("hc1");
		HttpConnection hc1 = new HttpConnection(true, "http://localhost:8080/SpringTPRest/");
		hc1.setRequestProperty(HttpHeaders.ACCEPT, ContentType.WILDCARD.getMimeType());
		hc1.sendRequest( null, null );
		System.out.println(hc1.getResponse());
		
		System.out.println("hc2");
		HttpConnection hc2 = new HttpConnection(true, "http://localhost:8080/SpringTPRest/home");
		hc2.setRequestProperty(HttpHeaders.ACCEPT, ContentType.WILDCARD.getMimeType());
		hc2.sendRequest( null, null );
		System.out.println(hc2.getResponse());
		
		System.out.println("hc3");
		HttpConnection hc3 = new HttpConnection(false, "http://localhost:8080/SpringTPRest/datas/FN-8176");
		hc3.setRequestProperty(HttpHeaders.ACCEPT, ContentType.WILDCARD.getMimeType());
		hc3.sendRequest( null, null );
		System.out.println(hc3.getResponse());
		
		System.out.println("hc4");
		HttpConnection hc4 = new HttpConnection(true, "http://localhost:8080/SpringTPRest/custom");
		hc4.setRequestProperty(HttpHeaders.ACCEPT, ContentType.WILDCARD.getMimeType());
		Map<String, String> params = new HashMap<String, String>();
		for (int i = 0 ; i < 3 ; i++) 
			{ params.put("key"+i, "value"+i); }
		hc4.sendRequest( null, params );
		System.out.println(hc4.getResponse());
		
		System.out.println("hc5");
		HttpConnection hc5 = new HttpConnection(false, "http://localhost:8080/SpringTPRest/objectKey");
		hc5.setRequestProperty(HttpHeaders.ACCEPT, ContentType.WILDCARD.getMimeType());
		hc5.setContentType(ContentType.APPLICATION_JSON.getMimeType());
		String data = "{\"username\":\"xyz\",\"password\":\"12345\"}";
		hc5.sendRequest( data, null );
		System.out.println(hc5.getResponse());
		
	}
	
}
