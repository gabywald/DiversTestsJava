package gabywald.jersey.test;

import java.util.HashMap;
import java.util.Map;


import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gabywald.jersey.JerseyLauncher;
import gabywald.jersey.JerseyLauncherBuilder;
import gabywald.rest.httpclient.HttpConnection;

/**
 * 
 * @author Gabriel Chandesris (2022)
 */
public class HttpConnectionTests {
	
	public JerseyLauncher jl = null;

	@BeforeEach
	void setUp() throws Exception {
		
		int port				= 8080;
		String url 				= "localhost";
		String packages1		= "gabywald.jersey.commonresources";
		String packages2		= "gabywald.jersey.resources";
		
		JerseyLauncherBuilder jlb = new JerseyLauncherBuilder();
		Assertions.assertNotNull( jlb );
		
		this.jl = jlb.addPort(port).addURL(url)
				.addPackages(packages1).addPackages(packages2)
				.addStart(true).build();
		Assertions.assertNotNull( jl );
		
		// ... 
		
	}
	
	@AfterEach
	void tearDown() {
		this.jl.stop(  );
	}

	@Test
	public void HttpConnectionTest() {
		
		System.out.println("hc1");
		HttpConnection hc1 = new HttpConnection(true, "http://localhost:8080/SpringTPRest/");
		hc1.setRequestProperty(HttpHeaders.ACCEPT, ContentType.WILDCARD.getMimeType());
		hc1.sendRequest( null, null );
		System.out.println(hc1.getResponse());
		// TODO Assertions++
		
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
