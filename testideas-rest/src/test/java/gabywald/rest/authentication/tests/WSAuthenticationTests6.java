package gabywald.rest.authentication.tests;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gabywald.rest.authentication.BasicAuthFilter;
import gabywald.rest.authentication.TokenGenerator;
import gabywald.rest.authentication.TokenUseCase;
import gabywald.rest.users.LibraryResource;
import gabywald.rest.authentication.BearerAuthFilter;
import gabywald.rest.authentication.RolesAllowedRequestFilter;

/**
 * 
 * 
 * TODO make it with httpS / SSL !! 
 * 
 * @author Gabriel Chandesris (2024)
 */
class WSAuthenticationTests6 {

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
        
        ResourceConfig rcAuthentication = new ResourceConfig();
        rcAuthentication.registerClasses(BasicAuthFilter.class);
        rcAuthentication.registerClasses(TokenGenerator.class);
        rcAuthentication.property(LoggingFeature.LOGGING_FEATURE_LOGGER_LEVEL_SERVER, Level.WARNING.getName());
        this.serverAuthentication = GrizzlyHttpServerFactory.createHttpServer(WSAuthenticationTests6.BASE_URI_TOKENGENERATOR, rcAuthentication);
        this.serverAuthentication.start();
        
        ResourceConfig rcServices = new ResourceConfig();
        rcServices.registerClasses(BearerAuthFilter.class);
        rcServices.registerClasses(TokenUseCase.class);
        rcServices.registerClasses(RolesAllowedRequestFilter.class);
        rcServices.registerClasses(LibraryResource.class);
        rcServices.property(LoggingFeature.LOGGING_FEATURE_LOGGER_LEVEL_SERVER, Level.WARNING.getName());
        this.serverOfServices = GrizzlyHttpServerFactory.createHttpServer(WSAuthenticationTests6.BASE_URI_TOKENUSERUSAGE, rcServices);
        this.serverOfServices.start();
        
        System.out.println( "@BeforeEach END" );
    }
    
    @AfterEach
    void tearDown() throws Exception {
        System.out.println( "@AfterEach" );
        if (this.serverAuthentication != null) { this.serverAuthentication.shutdownNow(); }
        if (this.serverOfServices != null) { this.serverOfServices.shutdownNow(); }
        System.out.println( "@AfterEach END" );
    }

    public static final String BASE_PATH = "http://localhost/";
    public static final URI BASE_URI_TOKENGENERATOR = UriBuilder.fromUri( WSAuthenticationTests6.BASE_PATH ).port(9991).build();
    public static final URI BASE_URI_TOKENUSERUSAGE = UriBuilder.fromUri( WSAuthenticationTests6.BASE_PATH ).port(9999).build();
    private HttpServer serverAuthentication = null;
    private HttpServer serverOfServices = null;

	@Test
	void testBASIC01() {
	    HttpUriRequest requestAuth01 = new HttpGet( "http://localhost:9991/authenticate" );
	    String login01 = "user", psswd01 = "psswd";
	    String toBasic01 = login01 + ":" + Base64.encodeBase64String( psswd01.getBytes() );
	    requestAuth01.setHeader("Authorization", "Basic " + Base64.encodeBase64String( toBasic01.getBytes() ));
	    requestAuth01.setHeader("user-agent", "UA nonymous 01");
	    
	    HttpUriRequest requestAuth02 = new HttpGet( "http://localhost:9991/authenticate" );
	    String login02 = "johnsmith", psswd02 = "12345";
	    String toBasic02 = login02 + ":" + Base64.encodeBase64String( psswd02.getBytes() );
	    requestAuth02.setHeader("Authorization", "Basic " + Base64.encodeBase64String( toBasic02.getBytes() ));
	    requestAuth02.setHeader("user-agent", "UA nonymous 02");
	    
	    HttpUriRequest requestAuth03 = new HttpGet( "http://localhost:9991/authenticate" );
	    String login03 = "neo", psswd03 = "54321";
	    String toBasic03 = login03 + ":" + Base64.encodeBase64String( psswd03.getBytes() );
	    requestAuth03.setHeader("Authorization", "Basic " + Base64.encodeBase64String( toBasic03.getBytes() ));
	    requestAuth03.setHeader("user-agent", "UA nonymous 03");
	    try {
			HttpResponse httpResponseAuth01 = HttpClientBuilder.create().build().execute( requestAuth01 );
			System.out.println( httpResponseAuth01.toString() );
			Assertions.assertEquals( HttpStatus.SC_OK, httpResponseAuth01.getStatusLine().getStatusCode() );
			String responseString01 = EntityUtils.toString(httpResponseAuth01.getEntity(), "UTF-8");
			System.out.println( " => '" + responseString01 + "' <= " );
			JSONObject jsonObject01 = new JSONObject( responseString01 );
			Assertions.assertTrue( jsonObject01.has( "token" ) );
			System.out.println( "TOKEN : " + jsonObject01.get("token").toString() + " //" );
			
			HttpResponse httpResponseAuth02 = HttpClientBuilder.create().build().execute( requestAuth02 );
			System.out.println( httpResponseAuth02.toString() );
			Assertions.assertEquals( HttpStatus.SC_OK, httpResponseAuth02.getStatusLine().getStatusCode() );
			String responseString02 = EntityUtils.toString(httpResponseAuth02.getEntity(), "UTF-8");
			System.out.println( " => '" + responseString02 + "' <= " );
			JSONObject jsonObject02 = new JSONObject( responseString02 );
			Assertions.assertTrue( jsonObject02.has( "token" ) );
			System.out.println( "TOKEN : " + jsonObject02.get("token").toString() + " //" );
			
			HttpResponse httpResponseAuth03 = HttpClientBuilder.create().build().execute( requestAuth03 );
			System.out.println( httpResponseAuth03.toString() );
			Assertions.assertEquals( HttpStatus.SC_OK, httpResponseAuth03.getStatusLine().getStatusCode() );
			String responseString03 = EntityUtils.toString(httpResponseAuth03.getEntity(), "UTF-8");
			System.out.println( " => '" + responseString03 + "' <= " );
			JSONObject jsonObject03 = new JSONObject( responseString03 );
			Assertions.assertTrue( jsonObject03.has( "token" ) );
			System.out.println( "TOKEN : " + jsonObject03.get("token").toString() + " //" );
			
			/* ***** ***** ***** ***** ***** */
			
			HttpUriRequest requestService01 = new HttpGet( "http://localhost:9999/protected" );
			requestService01.addHeader( httpResponseAuth01.getHeaders("Authorization")[0] );
			requestService01.setHeader("user-agent", "UA nonymous");
			HttpResponse httpResponseService01 = HttpClientBuilder.create().build().execute( requestService01 );
			System.out.println( httpResponseService01.toString() );
			Assertions.assertEquals( HttpStatus.SC_OK, httpResponseService01.getStatusLine().getStatusCode() );
			String helloUser01 = EntityUtils.toString(httpResponseService01.getEntity(), "UTF-8");
			System.out.println( " => '" + helloUser01 + "' <= " );
			Assertions.assertEquals("Hello, 'Test User'", helloUser01);
			
			HttpUriRequest requestService02 = new HttpGet( "http://localhost:9999/protected" );
			requestService02.addHeader( httpResponseAuth02.getHeaders("Authorization")[0] );
			requestService02.setHeader("user-agent", "UA nonymous");
			HttpResponse httpResponseService02 = HttpClientBuilder.create().build().execute( requestService02 );
			System.out.println( httpResponseService02.toString() );
			Assertions.assertEquals( HttpStatus.SC_OK, httpResponseService02.getStatusLine().getStatusCode() );
			String helloUser02 = EntityUtils.toString(httpResponseService02.getEntity(), "UTF-8");
			System.out.println( " => '" + helloUser02 + "' <= " );
			Assertions.assertEquals("Hello, 'John Smith'", helloUser02);
			
			HttpUriRequest requestService03 = new HttpGet( "http://localhost:9999/protected" );
			requestService03.addHeader( httpResponseAuth03.getHeaders("Authorization")[0] );
			requestService03.setHeader("user-agent", "UA nonymous");
			HttpResponse httpResponseService03 = HttpClientBuilder.create().build().execute( requestService03 );
			System.out.println( httpResponseService03.toString() );
			Assertions.assertEquals( HttpStatus.SC_OK, httpResponseService03.getStatusLine().getStatusCode() );
			String helloUser03 = EntityUtils.toString(httpResponseService03.getEntity(), "UTF-8");
			System.out.println( " => '" + helloUser03 + "' <= " );
			Assertions.assertEquals("Hello, 'Ralph Anderson'", helloUser03);
			
			/* ***** ***** ***** ***** ***** */
			
			// TODO check ROLES
			HttpUriRequest requestService04a = new HttpGet( "http://localhost:9999/addBook" );
			requestService04a.addHeader( httpResponseAuth01.getHeaders("Authorization")[0] );
			requestService04a.setHeader("user-agent", "UA nonymous");
			requestService04a.setHeader("book", "NOUVEAU LIVRE");
			HttpResponse httpResponseService04a = HttpClientBuilder.create().build().execute( requestService04a );
			System.out.println( httpResponseService04a.toString() );
			Assertions.assertEquals( HttpStatus.SC_OK, httpResponseService04a.getStatusLine().getStatusCode() );
			String helloUser04a = EntityUtils.toString(httpResponseService04a.getEntity(), "UTF-8");
			System.out.println( " => '" + helloUser04a + "' <= " );
			Assertions.assertEquals("", helloUser04a);
			
			HttpUriRequest requestService04b = new HttpGet( "http://localhost:9999/viewBooks" );
			requestService04b.addHeader( httpResponseAuth01.getHeaders("Authorization")[0] );
			requestService04b.setHeader("user-agent", "UA nonymous");
			HttpResponse httpResponseService04b = HttpClientBuilder.create().build().execute( requestService04b );
			System.out.println( httpResponseService04b.toString() );
			Assertions.assertEquals( HttpStatus.SC_OK, httpResponseService04b.getStatusLine().getStatusCode() );
			String helloUser04b = EntityUtils.toString(httpResponseService04b.getEntity(), "UTF-8");
			System.out.println( " => '" + helloUser04b + "' <= " );
			Assertions.assertEquals("", helloUser04b);
			
			HttpUriRequest requestService04c = new HttpGet( "http://localhost:9999/book/" + "9781234567890" );
			requestService04c.addHeader( httpResponseAuth01.getHeaders("Authorization")[0] );
			requestService04c.setHeader("user-agent", "UA nonymous");
			// requestService04c.setHeader("isbn", "9781234567890");
			HttpResponse httpResponseService04c = HttpClientBuilder.create().build().execute( requestService04c );
			System.out.println( httpResponseService04c.toString() );
			Assertions.assertEquals( HttpStatus.SC_OK, httpResponseService04c.getStatusLine().getStatusCode() );
			String helloUser04c = EntityUtils.toString(httpResponseService04c.getEntity(), "UTF-8");
			System.out.println( " => '" + helloUser04c + "' <= " );
			Assertions.assertEquals("", helloUser04c);
			
			
			// TODO check ROLES
			HttpUriRequest requestService05a = new HttpGet( "http://localhost:9999/addBook" );
			requestService05a.addHeader( httpResponseAuth02.getHeaders("Authorization")[0] );
			requestService05a.setHeader("user-agent", "UA nonymous");
			requestService05a.setHeader("book", "NOUVEAU LIVRE");
			HttpResponse httpResponseService05a = HttpClientBuilder.create().build().execute( requestService05a );
			System.out.println( httpResponseService05a.toString() );
			Assertions.assertEquals( HttpStatus.SC_OK, httpResponseService05a.getStatusLine().getStatusCode() );
			String helloUser05a = EntityUtils.toString(httpResponseService05a.getEntity(), "UTF-8");
			System.out.println( " => '" + helloUser05a + "' <= " );
			Assertions.assertEquals("", helloUser05a);
			
			HttpUriRequest requestService05b = new HttpGet( "http://localhost:9999/viewBooks" );
			requestService05b.addHeader( httpResponseAuth02.getHeaders("Authorization")[0] );
			requestService05b.setHeader("user-agent", "UA nonymous");
			HttpResponse httpResponseService05b = HttpClientBuilder.create().build().execute( requestService05b );
			System.out.println( httpResponseService05b.toString() );
			Assertions.assertEquals( HttpStatus.SC_OK, httpResponseService05b.getStatusLine().getStatusCode() );
			String helloUser05b = EntityUtils.toString(httpResponseService05b.getEntity(), "UTF-8");
			System.out.println( " => '" + helloUser05b + "' <= " );
			Assertions.assertEquals("", helloUser05b);
			
			HttpUriRequest requestService05c = new HttpGet( "http://localhost:9999/book/" + "9781234567890" );
			requestService05c.addHeader( httpResponseAuth02.getHeaders("Authorization")[0] );
			requestService05c.setHeader("user-agent", "UA nonymous");
			// requestService05c.setHeader("isbn", "9781234567890");
			HttpResponse httpResponseService05c = HttpClientBuilder.create().build().execute( requestService05c );
			System.out.println( httpResponseService05c.toString() );
			Assertions.assertEquals( HttpStatus.SC_OK, httpResponseService05c.getStatusLine().getStatusCode() );
			String helloUser05c = EntityUtils.toString(httpResponseService05c.getEntity(), "UTF-8");
			System.out.println( " => '" + helloUser05c + "' <= " );
			Assertions.assertEquals("", helloUser05c);
			
			
			// TODO check ROLES
			HttpUriRequest requestService06a = new HttpGet( "http://localhost:9999/addBook" );
			requestService06a.addHeader( httpResponseAuth03.getHeaders("Authorization")[0] );
			requestService06a.setHeader("user-agent", "UA nonymous");
			requestService06a.setHeader("book", "NOUVEAU LIVRE");
			HttpResponse httpResponseService06a = HttpClientBuilder.create().build().execute( requestService06a );
			System.out.println( httpResponseService06a.toString() );
			Assertions.assertEquals( HttpStatus.SC_OK, httpResponseService06a.getStatusLine().getStatusCode() );
			String helloUser06a = EntityUtils.toString(httpResponseService06a.getEntity(), "UTF-8");
			System.out.println( " => '" + helloUser06a + "' <= " );
			Assertions.assertEquals("", helloUser06a);
			
			HttpUriRequest requestService06b = new HttpGet( "http://localhost:9999/viewBooks" );
			requestService06b.addHeader( httpResponseAuth03.getHeaders("Authorization")[0] );
			requestService06b.setHeader("user-agent", "UA nonymous");
			HttpResponse httpResponseService06b = HttpClientBuilder.create().build().execute( requestService06b );
			System.out.println( httpResponseService06b.toString() );
			Assertions.assertEquals( HttpStatus.SC_OK, httpResponseService06b.getStatusLine().getStatusCode() );
			String helloUser06b = EntityUtils.toString(httpResponseService06b.getEntity(), "UTF-8");
			System.out.println( " => '" + helloUser06b + "' <= " );
			Assertions.assertEquals("", helloUser06b);
			
			HttpUriRequest requestService06c = new HttpGet( "http://localhost:9999/book/" + "9781234567890" );
			requestService06c.addHeader( httpResponseAuth03.getHeaders("Authorization")[0] );
			requestService06c.setHeader("user-agent", "UA nonymous");
			// requestService06c.setHeader("isbn", "9781234567890");
			HttpResponse httpResponseService06c = HttpClientBuilder.create().build().execute( requestService06c );
			System.out.println( httpResponseService06c.toString() );
			Assertions.assertEquals( HttpStatus.SC_OK, httpResponseService06c.getStatusLine().getStatusCode() );
			String helloUser06c = EntityUtils.toString(httpResponseService06c.getEntity(), "UTF-8");
			System.out.println( " => '" + helloUser06c + "' <= " );
			Assertions.assertEquals("", helloUser06c);
			
			
		} catch (IOException e) { 
			e.printStackTrace();
			Assertions.fail( e.getMessage() );
		}
	}
	
}