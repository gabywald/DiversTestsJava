package gabywald.rest.authentication.tests;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
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

import gabywald.rest.authentication.ElaborateAuthFilter;
import gabywald.rest.authentication.TokenGenerator;

/**
 * Tests with ElaborateAuthFilter + TokenGenerator.
 * 
 * TODO make it with httpS / SSL !! 
 * 
 * @author Gabriel Chandesris (2024)
 */
class WSAuthenticationTests4 {

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
        rc.registerClasses(ElaborateAuthFilter.class);
        rc.registerClasses(TokenGenerator.class);
        rc.property(LoggingFeature.LOGGING_FEATURE_LOGGER_LEVEL_SERVER, Level.WARNING.getName());
        
        this.server = GrizzlyHttpServerFactory.createHttpServer(WSAuthenticationTests4.BASE_URI, rc);
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
    public static final URI BASE_URI = UriBuilder.fromUri( WSAuthenticationTests4.BASE_PATH ).port(9991).build();
    private HttpServer server = null;

	@Test
	void testBASIC01() {
	    HttpUriRequest request = new HttpPost( "http://localhost:9991/authenticate" );
	    String login = "user", psswd = "psswd";
	    String toBasic = login + ":" + Base64.encodeBase64String( psswd.getBytes() );
	    request.addHeader("Authorization", "Basic " + Base64.encodeBase64String( toBasic.getBytes() ));
	    // request.addHeader("Authorization", "Basic ZGVtbzpwQDU1dzByZA==");
	    // request.addHeader("Authorization", "Basic dXNlcm5hbWU6cGFzc3dvcmQ=");
	    // request.addHeader("Authorization", "Basic SGVsbG8gV29ybGQhCg==");
	    request.setHeader("user-agent", "UA nonymous");
	    request.setHeader("login", login);
	    request.setHeader("password", Base64.encodeBase64String( psswd.getBytes() ));
	    try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );
			System.out.println( httpResponse.toString() );
			Assertions.assertEquals( HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode() );
			
			String responseString = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
			System.out.println( " => '" + responseString + "' <= " );
			
			JSONObject jsonObject = new JSONObject( responseString );
			Assertions.assertTrue( jsonObject.has( "token" ) );
			System.out.println( "TOKEN : " + jsonObject.get("token").toString() + " //" );
			
			JWTVerifier verifier = JWT.require( Algorithm.HMAC256( "gabywald" ) ).withIssuer( "gabywald" ).build();
			
			try {
				@SuppressWarnings("unused")
			    DecodedJWT decodedJWT = verifier.verify( jsonObject.get("token").toString() );
			    
			    String token = jsonObject.get("token").toString();
			    Assertions.assertEquals( 3, token.split("\\.").length );
			    // System.out.println( "*****" + token.split("\\.")[0] + "*****" );
			    System.out.println( new String( Base64.decodeBase64( token.split("\\.")[0] ) ) );
			    System.out.println( new String( Base64.decodeBase64( token.split("\\.")[1] ) ) );
			    System.out.println( new String( Base64.decodeBase64( token.split("\\.")[2] ) ) );
			} catch (JWTVerificationException e) { 
				System.out.println(e.getMessage()); 
				Assertions.fail(e.getMessage());
			}
			
			Assertions.assertTrue( httpResponse.containsHeader("Authorization") );
			Assertions.assertNotNull( httpResponse.getHeaders("Authorization") );
			System.out.println( httpResponse.getHeaders("Authorization").length );
			Assertions.assertEquals( 1, httpResponse.getHeaders("Authorization").length );
			System.out.println( httpResponse.getHeaders("Authorization")[0] );
			System.out.println( httpResponse.getHeaders("Authorization")[0].getValue() );
			System.out.println( httpResponse.getHeaders("Authorization")[0].getValue().substring("Bearer ".length()) );
			
			try {
				@SuppressWarnings("unused")
				DecodedJWT decodedJWT = verifier.verify( httpResponse.getHeaders("Authorization")[0].getValue().substring("Bearer ".length()) );
			} catch (JWTVerificationException e) { 
				System.out.println(e.getMessage()); 
				Assertions.fail(e.getMessage());
			}
			
		} catch (IOException e) { 
			e.printStackTrace();
			Assertions.fail( e.getMessage() );
		}
	}
	
	@Test
	void testBASIC02() {
	    HttpUriRequest request = new HttpPost( "http://localhost:9991/authenticate" );
	    request.addHeader("Authorization", "Basic ZGVtbzpwQDU1dzByZA==");
	    // request.addHeader("Authorization", "Basic dXNlcm5hbWU6cGFzc3dvcmQ=");
	    // request.addHeader("Authorization", "Basic SGVsbG8gV29ybGQhCg==");
	    request.setHeader("user-agent", "UA nonymous");
	    try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );
			System.out.println( httpResponse.toString() );
			Assertions.assertEquals( HttpStatus.SC_NOT_FOUND, httpResponse.getStatusLine().getStatusCode() );
			
			String responseString = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
			System.out.println( " => '" + responseString + "' <= " );
			
//			JSONObject jsonObject = new JSONObject( responseString );
//			Assertions.assertFalse( jsonObject.has( "token" ) );
//			Assertions.assertTrue( jsonObject.has( "message" ) );
//			System.out.println( "MESSAGE : " + jsonObject.get("message").toString() + " //" );
			
		} catch (IOException e) { 
			e.printStackTrace();
			Assertions.fail( e.getMessage() );
		}
	}
	
	@Test
	void testBASIC03() {
	    HttpUriRequest request = new HttpPost( "http://localhost:9991/authenticate" );
	    // NO authentication header !!
	    request.setHeader("user-agent", "UA nonymous");
	    try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );
			System.out.println( httpResponse.toString() );
			Assertions.assertEquals( HttpStatus.SC_NOT_FOUND, httpResponse.getStatusLine().getStatusCode() );
			
			String responseString = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
			System.out.println( " => '" + responseString + "' <= " );
			
		} catch (IOException e) { 
			e.printStackTrace();
			Assertions.fail( e.getMessage() );
		}
	}

}
