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

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import gabywald.rest.authentication.BasicAuthFilter;
import gabywald.rest.authentication.TokenGenerator;
import gabywald.rest.authentication.TokenUseCase;
import gabywald.rest.authentication.BearerAuthFilter;

/**
 * 
 * 
 * TODO make it with httpS / SSL !! 
 * 
 * @author Gabriel Chandesris (2024)
 */
class WSAuthenticationTests5 {

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
        this.serverAuthentication = GrizzlyHttpServerFactory.createHttpServer(WSAuthenticationTests5.BASE_URI_TOKENGENERATOR, rcAuthentication);
        this.serverAuthentication.start();
        
        ResourceConfig rcServices = new ResourceConfig();
        rcServices.registerClasses(BearerAuthFilter.class);
        rcServices.registerClasses(TokenUseCase.class);
        rcServices.property(LoggingFeature.LOGGING_FEATURE_LOGGER_LEVEL_SERVER, Level.WARNING.getName());
        this.serverOfServices = GrizzlyHttpServerFactory.createHttpServer(WSAuthenticationTests5.BASE_URI_TOKENUSERUSAGE, rcServices);
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
    public static final URI BASE_URI_TOKENGENERATOR = UriBuilder.fromUri( WSAuthenticationTests5.BASE_PATH ).port(9991).build();
    public static final URI BASE_URI_TOKENUSERUSAGE = UriBuilder.fromUri( WSAuthenticationTests5.BASE_PATH ).port(9999).build();
    private HttpServer serverAuthentication = null;
    private HttpServer serverOfServices = null;

	@Test
	void testBASIC01() {
	    HttpUriRequest requestAuth = new HttpGet( "http://localhost:9991/authenticate" );
	    String login = "user", psswd = "psswd";
	    String toBasic = login + ":" + Base64.encodeBase64String( psswd.getBytes() );
	    requestAuth.setHeader("Authorization", "Basic " + Base64.encodeBase64String( toBasic.getBytes() ));
	    // requestAuth.setHeader("Authorization", "Basic ZGVtbzpwQDU1dzByZA==");
	    // requestAuth.setHeader("Authorization", "Basic dXNlcm5hbWU6cGFzc3dvcmQ=");
	    // requestAuth.setHeader("Authorization", "Basic SGVsbG8gV29ybGQhCg==");
	    requestAuth.setHeader("user-agent", "UA nonymous");
	    try {
			HttpResponse httpResponseAuth = HttpClientBuilder.create().build().execute( requestAuth );
			System.out.println( httpResponseAuth.toString() );
			Assertions.assertEquals( HttpStatus.SC_OK, httpResponseAuth.getStatusLine().getStatusCode() );
			
			String responseString = EntityUtils.toString(httpResponseAuth.getEntity(), "UTF-8");
			System.out.println( " => '" + responseString + "' <= " );
			
			JSONObject jsonObject = new JSONObject( responseString );
			Assertions.assertTrue( jsonObject.has( "token" ) );
			System.out.println( "TOKEN : " + jsonObject.get("token").toString() + " //" );

			JWTVerifier verifier = JWT.require( Algorithm.HMAC256( "gabywald" ) ).withIssuer( "gabywald" ).build();
			
			Assertions.assertTrue( httpResponseAuth.containsHeader("Authorization") );
			Assertions.assertNotNull( httpResponseAuth.getHeaders("Authorization") );
			System.out.println( httpResponseAuth.getHeaders("Authorization").length );
			Assertions.assertEquals( 1, httpResponseAuth.getHeaders("Authorization").length );
			
			try {
				@SuppressWarnings("unused")
				DecodedJWT decodedJWT = verifier.verify( httpResponseAuth.getHeaders("Authorization")[0].getValue().substring("Bearer ".length()) );
			} catch (JWTVerificationException e) { 
				System.out.println(e.getMessage()); 
				Assertions.fail(e.getMessage());
			}
			
			HttpUriRequest requestService = new HttpGet( "http://localhost:9999/protected" );
			requestService.addHeader( httpResponseAuth.getHeaders("Authorization")[0] );
			requestService.setHeader("user-agent", "UA nonymous");
			HttpResponse httpResponseService = HttpClientBuilder.create().build().execute( requestService );
			System.out.println( httpResponseService.toString() );
			Assertions.assertEquals( HttpStatus.SC_OK, httpResponseService.getStatusLine().getStatusCode() );
			
			String helloUser = EntityUtils.toString(httpResponseService.getEntity(), "UTF-8");
			System.out.println( " => '" + helloUser + "' <= " );
			Assertions.assertEquals("Hello, 'Test User'", helloUser);
			
		} catch (IOException e) { 
			e.printStackTrace();
			Assertions.fail( e.getMessage() );
		}
	}
	
}