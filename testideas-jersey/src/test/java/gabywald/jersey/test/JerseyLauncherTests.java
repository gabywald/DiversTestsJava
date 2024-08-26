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
import gabywald.utilities.logger.Logger;
import gabywald.utilities.logger.Logger.LoggerLevel;

/**
 * 
 * @author Gabriel Chandesris (2022)
 */
public class JerseyLauncherTests {

	@BeforeEach
	public void setUp() 
			throws Exception { ; }

	@AfterEach
	public void tearDown() 
			throws Exception { ; }

	@Test
	public void testJerseyLauncher() {
		
		int port				= 8080;
		String url 				= "localhost";
		String packages1		= "gabywald.jersey.commonresources";
		String packages2		= "gabywald.jersey.resources";
		
		JerseyLauncherBuilder jlb = new JerseyLauncherBuilder();
		Assertions.assertNotNull( jlb );
		
		JerseyLauncher jl = jlb.addPort(port).addURL(url)
				.addPackages(packages1).addPackages(packages2)
				.addStart(true).build();
		Assertions.assertNotNull( jl );
		
		Logger.setLogLevel(LoggerLevel.LL_DEBUG);
		
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "hc1");
		HttpConnection hc1 = new HttpConnection(true, "http://localhost:8080/hello");
		hc1.setRequestProperty(HttpHeaders.ACCEPT, ContentType.WILDCARD.getMimeType());
		hc1.sendRequest( null, null );
		Logger.printlnLog(LoggerLevel.LL_DEBUG, hc1.getResponse());
		// TODO Assertions++
		
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "hc login");
		HttpConnection hcLogin = new HttpConnection(false, "http://localhost:8080/login");
		hcLogin.setUserAgent("SandBender");
		hcLogin.setContentType(ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
		hcLogin.setRequestProperty(HttpHeaders.ACCEPT, ContentType.WILDCARD.getMimeType());
		Map<String, String> params = new HashMap<String, String>();
		params.put("login", "user");
		params.put("password", "rest");
		int iHClogin = hcLogin.sendRequest( null, params );
		Logger.printlnLog(LoggerLevel.LL_DEBUG, iHClogin + " : " + hcLogin.getResponse());
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "" + hcLogin.getResponseProperty(HttpHeaders.AUTHORIZATION));
		
//		try {
//			Logger.printlnLog(LoggerLevel.LL_DEBUG, HttpConnection.getFullResponse(hcLogin));
//		} catch (IOException e) {
//			// e.printStackTrace();
//			Logger.printlnLog(LoggerLevel.LL_DEBUG, "HttpConnection.getFullResponse(hcLogin) : IOException !" );
//		}
		
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "\n hc resources no login");
		HttpConnection hcRSCnoLogin = new HttpConnection(true, "http://localhost:8080/ressources/42");
		hcRSCnoLogin.setRequestProperty(HttpHeaders.ACCEPT, ContentType.WILDCARD.getMimeType());
		hcRSCnoLogin.sendRequest( null, null );
		Logger.printlnLog(LoggerLevel.LL_DEBUG, hcRSCnoLogin.getResponse());
		
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "\n hc resources with login");
		HttpConnection hcRSCwithLogin = new HttpConnection(false, "http://localhost:8080/ressources/42");
		hcRSCwithLogin.setRequestProperty(HttpHeaders.ACCEPT, ContentType.WILDCARD.getMimeType());
		String bearerCodeAuth = hcLogin.getResponseProperty(HttpHeaders.AUTHORIZATION).get(0);
		hcRSCwithLogin.setRequestProperty(HttpHeaders.AUTHORIZATION, bearerCodeAuth);
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(HttpHeaders.AUTHORIZATION, bearerCodeAuth);
		int ihcRSCwithLogin = hcRSCwithLogin.sendRequest( null, parameters );
		Logger.printlnLog(LoggerLevel.LL_DEBUG, ihcRSCwithLogin + " : " + hcRSCwithLogin.getResponse());
		
		jl.stop();
	}

//	@Test
//	public void testStart() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testStop() {
//		fail("Not yet implemented");
//	}

}
