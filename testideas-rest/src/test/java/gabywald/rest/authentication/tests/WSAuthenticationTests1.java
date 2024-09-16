package gabywald.rest.authentication.tests;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;

import javax.ws.rs.core.UriBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
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

import gabywald.rest.authentication.TokenGenerator;

/**
 * Test with only TokenGenerator. 
 * 
 * @author Gabriel Chandesris (2024)
 */
class WSAuthenticationTests1 {

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
        rc.registerClasses(TokenGenerator.class);
        rc.property(LoggingFeature.LOGGING_FEATURE_LOGGER_LEVEL_SERVER, Level.WARNING.getName());
        
        this.server = GrizzlyHttpServerFactory.createHttpServer(WSAuthenticationTests1.BASE_URI, rc);
        this.server.start();
        
        System.out.println( "@BeforeEach END" );
    }
    
    @AfterEach
    void tearDown() throws Exception {
        System.out.println( "@AfterEach" );
        if (this.server != null) { this.server.shutdownNow(); }
        System.out.println( "@AfterEach END" );
    }

    public static final String BASE_PATH = "http://localhost/";
    public static final URI BASE_URI = UriBuilder.fromUri( WSAuthenticationTests1.BASE_PATH ).port(9991).build();
    private HttpServer server = null;

	@Test
	void testBASIC01() {
	    HttpUriRequest request = new HttpGet( "http://localhost:9991/" + "authenticate" );
	    try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );
			Assertions.assertEquals( HttpStatus.SC_PRECONDITION_FAILED, httpResponse.getStatusLine().getStatusCode() );
		} catch (IOException e) { 
			e.printStackTrace();
			Assertions.fail( e.getMessage() );
		}
	}
	
	@Test
	void testBASIC02() {
	    HttpUriRequest request = new HttpPost( "http://localhost:9991/" + "authenticate" );
	    try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );
			Assertions.assertEquals( HttpStatus.SC_PRECONDITION_FAILED, httpResponse.getStatusLine().getStatusCode() );
		} catch (IOException e) { 
			e.printStackTrace();
			Assertions.fail( e.getMessage() );
		}
	}
	
	@Test
	void testBASIC03() {
	    HttpUriRequest request = new HttpGet( "http://localhost:9991/" + "bozo" );
	    try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );
			Assertions.assertEquals( HttpStatus.SC_NOT_FOUND, httpResponse.getStatusLine().getStatusCode() );
		} catch (IOException e) { 
			e.printStackTrace();
			Assertions.fail( e.getMessage() );
		}
	}

}
