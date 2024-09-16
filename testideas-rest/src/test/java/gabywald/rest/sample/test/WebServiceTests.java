package gabywald.rest.sample.test;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;

import javax.ws.rs.core.UriBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gabywald.rest.sample.HelloResource;

/**
 * 
 * @author Gabriel Chandesris (2024)
 */
class WebServiceTests {
	
    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        System.out.println( "@BeforeAll" );
    }
    
    @AfterAll
    static void tearDownAfterClass() throws Exception {
        System.out.println( "@AfterAll" );
    }
    
    @BeforeEach
    void setUp() throws Exception {
        System.out.println( "@BeforeEach" );
        
        ResourceConfig rc = new ResourceConfig();
        rc.registerClasses(HelloResource.class);
        rc.property(LoggingFeature.LOGGING_FEATURE_LOGGER_LEVEL_SERVER, Level.WARNING.getName());
        
        this.server = GrizzlyHttpServerFactory.createHttpServer(WebServiceTests.BASE_URI, rc);
        this.server.start();
    }
    
    @AfterEach
    void tearDown() throws Exception {
        System.out.println( "@AfterEach" );
        if (this.server != null) { this.server.shutdownNow(); }
    }

    public static final String BASE_PATH = "http://localhost/api/";
    public static final URI BASE_URI = UriBuilder.fromUri( WebServiceTests.BASE_PATH ).port(9991).build();
    private HttpServer server = null;
    
	@Test
	void testBASIC01() {
	    HttpUriRequest request = new HttpGet( "http://localhost:9991/api/" );
	    try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );
			Assertions.assertEquals( HttpStatus.SC_NOT_FOUND, httpResponse.getStatusLine().getStatusCode() );
		} catch (IOException e) { 
			e.printStackTrace();
			Assertions.fail( e.getMessage() );
		}
	}
	
	@Test
	void testBASIC02() {
	    HttpUriRequest request = new HttpGet( "http://localhost:9991/api/" + "hello" );
	    try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );
			Assertions.assertEquals( HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode() );
			System.out.println( httpResponse.toString() );
			String attemptedMimeType = "text/plain";
			String mimeType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();
			System.out.println( mimeType );
			Assertions.assertEquals( attemptedMimeType, mimeType );
			Assertions.assertEquals( 16, httpResponse.getEntity().getContentLength() );
			// httpResponse.getEntity().getContent()
			String responseString = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
			System.out.println( responseString );
			Assertions.assertEquals( "Bonjour les gens", responseString );
			Assertions.assertEquals(0, httpResponse.getHeaders("name").length);
		} catch (IOException e) { 
			e.printStackTrace();
			Assertions.fail( e.getMessage() );
		}
	}
	
	@Test
	void testBASIC03() {
	    HttpUriRequest request = new HttpGet( "http://localhost:9991/api/" + "hello" + "/toto" );
	    try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );
			Assertions.assertEquals( HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode() );
			System.out.println( httpResponse.toString() );
			String attemptedMimeType = "text/plain";
			String mimeType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();
			System.out.println( mimeType );
			Assertions.assertEquals( attemptedMimeType, mimeType );
			Assertions.assertEquals( 42, httpResponse.getEntity().getContentLength() );
			// httpResponse.getEntity().getContent()
			String responseString = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
			System.out.println( responseString );
			Assertions.assertEquals( "Bonjour toto de la part de votre serviteur", responseString );
			Assertions.assertEquals(0, httpResponse.getHeaders("name").length);
		} catch (IOException e) { 
			e.printStackTrace();
			Assertions.fail( e.getMessage() );
		}
	}
	
	@Test
	void testBASIC05() {
	    HttpUriRequest request = new HttpGet( "http://localhost:9991/api/" + "hello" + "/withheaders/toto" );
	    try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );
			Assertions.assertEquals( HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode() );
			System.out.println( httpResponse.toString() );
			String attemptedMimeType = "text/plain";
			String mimeType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();
			System.out.println( mimeType );
			Assertions.assertEquals( attemptedMimeType, mimeType );
			Assertions.assertEquals( 45, httpResponse.getEntity().getContentLength() );
			// httpResponse.getEntity().getContent()
			String responseString = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
			System.out.println( responseString );
			Assertions.assertEquals( "Bonjour toto de la part de (voir l'en-tête).", responseString );
			
			System.out.println( httpResponse.getHeaders("name")[0] );
			for (int i = 0 ; i < httpResponse.getAllHeaders().length ; i++) 
				{ System.out.println( httpResponse.getAllHeaders()[i].getName() + ": " + httpResponse.getAllHeaders()[i].getValue() ); }
			Assertions.assertEquals(1, httpResponse.getHeaders("name").length);
			Assertions.assertEquals("votre serviteur", httpResponse.getHeaders("name")[0].getValue());
			for (int i = 0 ; i < httpResponse.getHeaders("name")[0].getElements().length ; i++) 
				{ System.out.println( httpResponse.getHeaders("name")[0].getElements()[i] ); }
		} catch (IOException e) { 
			e.printStackTrace();
			Assertions.fail( e.getMessage() );
		}
	}

}
