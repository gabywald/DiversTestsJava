package gabywald.terminal3.serverside.tests;

import org.apache.commons.codec.binary.Base64;
import org.glassfish.tyrus.client.ClientManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import gabywald.terminal3.clientside.TerminalClient;
import gabywald.terminal3.clientside.ws.client.TerminalClientAuthClientEndpoint;
import gabywald.terminal3.clientside.ws.client.TerminalClientBasicClientEndpoint;
import gabywald.terminal3.clientside.ws.client.TerminalClientCommandClientEndpoint;
import gabywald.terminal3.serverside.TerminalServer;
import gabywald.terminal3.serverside.TerminalServerWebSocket;
import gabywald.terminal3.serverside.wsservers.TerminalServerAuthServer;
import gabywald.terminal3.serverside.wsservers.TerminalServerBasicServer;
import gabywald.terminal3.serverside.wsservers.TerminalServerCommandServer;
import gabywald.utilities.logger.Logger;
import gabywald.utilities.logger.Logger.LoggerLevel;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.websocket.DeploymentException;
import javax.websocket.Session;

/**
 * @author Gabriel Chandesris (2026)
 */
public class TerminalServerWebSocketTests {

	@Test
	public void testClientConnectionAndTokenRetrieval01() {
		
		TerminalServerAuthServer authServer;
		TerminalServerCommandServer commandServer;
		ClientManager clientManager;
		
		// Démarrer les serveurs
		authServer		= new TerminalServerAuthServer(8080);
		commandServer	= new TerminalServerCommandServer(8081);
		authServer.start();
		commandServer.start();

		// Initialiser le gestionnaire de clients
		clientManager = ClientManager.createClient();
		
		// Client 1 : Obtenir un token
		TerminalClientAuthClientEndpoint authClient1 = new TerminalClientAuthClientEndpoint();
		CountDownLatch latch1 = new CountDownLatch(1);
	    
		try {
			Session test1 = clientManager.connectToServer(authClient1, URI.create("ws://localhost:8080/authenticate"));
			Assertions.assertNotNull(test1);
	
			latch1.await(5, TimeUnit.SECONDS); // Attendre la réception du token
			String token1 = authClient1.getToken();
			Assertions.assertNull(token1, "Client 1 should NOT receive a token (or empty / null). ");
	
			// Client 1 : Se connecter au serveur de commandes avec le token
			TerminalClientCommandClientEndpoint commandClient1 = new TerminalClientCommandClientEndpoint();
			Session test2 = clientManager.connectToServer(commandClient1, URI.create("ws://localhost:8081/terminalemulator?token=" + token1));
			Assertions.assertNotNull(test2);
			commandClient1.sendCommand("test_command_1");
			Thread.sleep(1000); // Attendre pour simuler une interaction
	
			// Client 2 : Obtenir un token
			TerminalClientAuthClientEndpoint authClient2 = new TerminalClientAuthClientEndpoint();
			CountDownLatch latch2 = new CountDownLatch(1);
			Session test3 = clientManager.connectToServer(authClient2, URI.create("ws://localhost:8080/authenticate"));
			Assertions.assertNotNull(test3);
			latch2.await(5, TimeUnit.SECONDS);
			String token2 = authClient2.getToken();
			Assertions.assertNull(token2, "Client 2 should NOT receive a token (or empty / null). ");
	
			// Client 2 : Se connecter au serveur de commandes avec le token
			TerminalClientCommandClientEndpoint commandClient2 = new TerminalClientCommandClientEndpoint();
			Session test4 = clientManager.connectToServer(commandClient2, URI.create("ws://localhost:8081/terminalemulator?token=" + token2));
			Assertions.assertNotNull(test4);
			commandClient2.sendCommand("test_command_2");
			Thread.sleep(1000);
	
			// Fermer les connexions
			authClient1.close();
			commandClient1.close();
			authClient2.close();
			commandClient2.close();
		} 
	    catch (DeploymentException e)	{ e.printStackTrace();Assertions.fail(e.getMessage()); } 
	    catch (IOException e)			{ e.printStackTrace();Assertions.fail(e.getMessage()); }
		catch (InterruptedException e)	{ e.printStackTrace();Assertions.fail(e.getMessage()); }
		
		// Arrêter les serveurs
		if (authServer != null)		{ authServer.stop(); }
		if (commandServer != null)	{ commandServer.stop(); }
	}
	
	@Test
	public void testClientConnectionAndTokenRetrieval02() {
		
		TerminalServerBasicServer basicServer;
		TerminalServerAuthServer authServer;
		TerminalServerCommandServer commandServer;
		ClientManager clientManager;

			String serverName		= TerminalServer.getProperty( "gabywald.terminal.server.servername" );
			String serverAUTH 		= TerminalServer.getProperty( "gabywald.terminal.server.authentication" );
			String serverMAIN 		= TerminalServer.getProperty( "gabywald.terminal.server.mainservice" );
			
			Logger.printlnLog(LoggerLevel.LL_DEBUG, "serverName: {" + serverName + "}");
			Logger.printlnLog(LoggerLevel.LL_DEBUG, "serverAUTH: {" + serverAUTH + "}");
			Logger.printlnLog(LoggerLevel.LL_DEBUG, "serverMAIN: {" + serverMAIN + "}");
			
			int portAuthenti	= Integer.parseInt(TerminalServer.getProperty("gabywald.terminal.server.port.authentication"));
			int portServices	= Integer.parseInt(TerminalServer.getProperty("gabywald.terminal.server.port.services"));
			
			Logger.printlnLog(LoggerLevel.LL_DEBUG, "portAuthenti: [" + portAuthenti + "]");
			Logger.printlnLog(LoggerLevel.LL_DEBUG, "portServices: [" + portServices + "]");
			
			// Démarrer les serveurs
			basicServer		= new TerminalServerBasicServer(portServices-1);
			authServer		= new TerminalServerAuthServer(portAuthenti);
			commandServer	= new TerminalServerCommandServer(portServices);
			
			basicServer.start();
			authServer.start();
			commandServer.start();

			// Initialiser le gestionnaire de clients
			clientManager = ClientManager.createClient();
		
		String serverNameCLI		= TerminalClient.getProperty( "gabywald.terminal.server.servername" );
		String serverAUTHCLI 		= TerminalClient.getProperty( "gabywald.terminal.server.authentication" );
		String serverMAINCLI 		= TerminalClient.getProperty( "gabywald.terminal.server.mainservice" );
		
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "serverNameCLI: {" + serverNameCLI + "}");
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "serverAUTHCLI: {" + serverAUTHCLI + "}");
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "serverMAINCLI: {" + serverMAINCLI + "}");
		
		int portAuthentiCLI	= Integer.parseInt(TerminalClient.getProperty("gabywald.terminal.server.port.authentication"));
		int portServicesCLI	= Integer.parseInt(TerminalClient.getProperty("gabywald.terminal.server.port.services"));
		
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "portAuthentiCLI: [" + portAuthentiCLI + "]");
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "portServicesCLI: [" + portServicesCLI + "]");
	    
		try {
			
			// Client 1 : Se connecter au serveur Basic
			TerminalClientBasicClientEndpoint basicClient1 = new TerminalClientBasicClientEndpoint();
			Session testBasic1 = clientManager.connectToServer(basicClient1, URI.create("ws://localhost:" + (portServicesCLI-1) + "/"));
			Assertions.assertNotNull(testBasic1);
			Thread.sleep(1000); // Attendre pour simuler une interaction
			
			// Client 1 : Obtenir un token
			TerminalClientAuthClientEndpoint authClient1 = new TerminalClientAuthClientEndpoint();
			CountDownLatch latch1 = new CountDownLatch(1);
			Session testBasic2 = clientManager.connectToServer(authClient1, 
					URI.create("ws://localhost:" + portAuthentiCLI + "/authenticate"));
			Assertions.assertNotNull(testBasic2);
			latch1.await(5, TimeUnit.SECONDS); // Attendre la réception du token
			String token1 = authClient1.getToken();
			Assertions.assertNull(token1, "Client 1 should NOT receive a token (or empty / null). ");
	
			// Client 1 : Se connecter au serveur de commandes avec le token
			TerminalClientCommandClientEndpoint commandClient1 = new TerminalClientCommandClientEndpoint();
			Session testBasic3 = clientManager.connectToServer(commandClient1, 
					URI.create("ws://localhost:" + portServicesCLI + "/terminalemulator?token=" + token1));
			Assertions.assertNotNull(testBasic3);
			commandClient1.sendCommand("test_command_1");
			Thread.sleep(1000); // Attendre pour simuler une interaction
	
			// Client 2 : Obtenir un token
			TerminalClientAuthClientEndpoint authClient2 = new TerminalClientAuthClientEndpoint();
			CountDownLatch latch2 = new CountDownLatch(1);
			Session testBasic4 = clientManager.connectToServer(authClient2, 
					URI.create("ws://localhost:" + portAuthentiCLI + "/authenticate"));
			Assertions.assertNotNull(testBasic4);
			latch2.await(5, TimeUnit.SECONDS);
			String token2 = authClient2.getToken();
			Assertions.assertNull(token2, "Client 2 should NOT receive a token (or empty / null). ");
	
			// Client 2 : Se connecter au serveur de commandes avec le token
			TerminalClientCommandClientEndpoint commandClient2 = new TerminalClientCommandClientEndpoint();
			Session testBasic5 = clientManager.connectToServer(commandClient2, 
					URI.create("ws://localhost:" + portServicesCLI + "/terminalemulator?token=" + token2));
			Assertions.assertNotNull(testBasic5);
			commandClient2.sendCommand("test_command_2");
			Thread.sleep(1000);
	
			// Fermer les connexions
			authClient1.close();
			commandClient1.close();
			authClient2.close();
			commandClient2.close();
		} 
	    catch (DeploymentException e)	{ e.printStackTrace();Assertions.fail(e.getMessage()); } 
	    catch (IOException e)			{ e.printStackTrace();Assertions.fail(e.getMessage()); }
		catch (InterruptedException e)	{ e.printStackTrace();Assertions.fail(e.getMessage()); }
		
		// Arrêter les serveurs
		if (authServer != null)		{ authServer.stop(); }
		if (commandServer != null)	{ commandServer.stop(); }
	}
	
	@Test
	public void testClientConnectionAndTokenRetrieval03() {
		
		TerminalServerWebSocket tServer = null;
		ClientManager clientManager;
		
		// java.util.logging.Logger.getLogger(Server.class.getClass().getName()).setLevel(Level.ALL);
		TerminalServer tmpServer = new TerminalServerWebSocket(); // TerminalServer.builder( false );
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "tmpServer: [" + tmpServer + "}");
		Assertions.assertNotNull(tmpServer);
		Assertions.assertTrue(tmpServer instanceof TerminalServerWebSocket);
		tServer = (TerminalServerWebSocket) tmpServer;
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "tServer: [" + tServer + "}");
		Assertions.assertNotNull(tServer);
		tServer.start();
		
		// Initialiser le gestionnaire de clients
		clientManager = ClientManager.createClient();

		String serverNameCLI		= TerminalClient.getProperty( "gabywald.terminal.server.servername" );
		String serverAUTHCLI 		= TerminalClient.getProperty( "gabywald.terminal.server.authentication" );
		String serverMAINCLI 		= TerminalClient.getProperty( "gabywald.terminal.server.mainservice" );
		
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "serverNameCLI: {" + serverNameCLI + "}");
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "serverAUTHCLI: {" + serverAUTHCLI + "}");
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "serverMAINCLI: {" + serverMAINCLI + "}");
		
		int portAuthentiCLI	= Integer.parseInt(TerminalClient.getProperty("gabywald.terminal.server.port.authentication"));
		int portServicesCLI	= Integer.parseInt(TerminalClient.getProperty("gabywald.terminal.server.port.services"));
		
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "portAuthentiCLI: [" + portAuthentiCLI + "]");
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "portServicesCLI: [" + portServicesCLI + "]");
	    
		try {
			
			// Client 1 : Se connecter au serveur Basic
			TerminalClientBasicClientEndpoint basicClient1 = new TerminalClientBasicClientEndpoint();
			Session testBasic1 = clientManager.connectToServer(basicClient1, 
					URI.create("ws://" + serverNameCLI + ":" + (portServicesCLI-1) + "/"));
			Assertions.assertNotNull(testBasic1);
			Thread.sleep(1000); // Attendre pour simuler une interaction
			
			// Client 1 : Obtenir un token
			TerminalClientAuthClientEndpoint authClient1 = new TerminalClientAuthClientEndpoint();
			CountDownLatch latch1 = new CountDownLatch(1);
			Session testBasic2 = clientManager.connectToServer(authClient1, 
					URI.create("ws://" + serverNameCLI + ":" + portAuthentiCLI + "/" + serverAUTHCLI + ""));
			Assertions.assertNotNull(testBasic2);
			latch1.await(5, TimeUnit.SECONDS); // Attendre la réception du token
			String token1 = authClient1.getToken();
			Assertions.assertNull(token1, "Client 1 should NOT receive a token (or empty / null). ");
	
			// Client 1 : Se connecter au serveur de commandes avec le token
			TerminalClientCommandClientEndpoint commandClient1 = new TerminalClientCommandClientEndpoint();
			Session testBasic3 = clientManager.connectToServer(commandClient1, 
					URI.create("ws://" + serverNameCLI + ":" + portServicesCLI + "/" + serverMAINCLI + "?token=" + token1));
			Assertions.assertNotNull(testBasic3);
			commandClient1.sendCommand("test_command_1");
			Thread.sleep(1000); // Attendre pour simuler une interaction
	
			// Client 2 : Obtenir un token
			TerminalClientAuthClientEndpoint authClient2 = new TerminalClientAuthClientEndpoint();
			CountDownLatch latch2 = new CountDownLatch(1);
			Session testBasic4 = clientManager.connectToServer(authClient2, 
					URI.create("ws://" + serverNameCLI + ":" + portAuthentiCLI + "/" + serverAUTHCLI + ""));
			Assertions.assertNotNull(testBasic4);
			latch2.await(5, TimeUnit.SECONDS);
			String token2 = authClient2.getToken();
			Assertions.assertNull(token2, "Client 1 should NOT receive a token (or empty / null). ");
	
			// Client 2 : Se connecter au serveur de commandes avec le token
			TerminalClientCommandClientEndpoint commandClient2 = new TerminalClientCommandClientEndpoint();
			Session testBasic5 = clientManager.connectToServer(commandClient2, 
					URI.create("ws://" + serverNameCLI + ":" + portServicesCLI + "/" + serverMAINCLI + "?token=" + token2));
			Assertions.assertNotNull(testBasic5);
			commandClient2.sendCommand("test_command_2");
			Thread.sleep(1000);
			
			
		    String  login = TerminalClient.getProperty( "gabywald.terminal.user.username" ), 
		    		psswd = TerminalClient.getProperty( "gabywald.terminal.user.password" );
			
			// Client 3 : Obtenir un token
			TerminalClientAuthClientEndpoint authClient3 = new TerminalClientAuthClientEndpoint();
			CountDownLatch latch3 = new CountDownLatch(1);
			Session testBasic6 = clientManager.connectToServer(authClient3, 
					URI.create("ws://" + serverNameCLI + ":" + portAuthentiCLI + "/" + serverAUTHCLI + "?login=" + login + "&psswd=" + Base64.encodeBase64String( psswd.getBytes() )));
			Assertions.assertNotNull(testBasic6);
			latch3.await(5, TimeUnit.SECONDS);
			String token3 = authClient3.getToken();
			Assertions.assertNotNull(token3, "Client 3 should receive a token");
	
			// Client 3 : Se connecter au serveur de commandes avec le token
			TerminalClientCommandClientEndpoint commandClient3 = new TerminalClientCommandClientEndpoint();
			Session testBasic7 = clientManager.connectToServer(commandClient3, 
						URI.create("ws://" + serverNameCLI + ":" + portServicesCLI + "/" + serverMAINCLI + "?token=" + token3));
			Assertions.assertNotNull(testBasic7);
			commandClient3.sendCommand("help");
			latch3.await(5, TimeUnit.SECONDS);
			Assertions.assertTrue(commandClient3.getResponse().startsWith("Available commands:"));
			Assertions.assertEquals("user@terminal:~$", commandClient3.getPrompt());
	
			// Fermer les connexions
			basicClient1.close();
			authClient1.close();
			commandClient1.close();
			authClient2.close();
			commandClient2.close();
			authClient3.close();
			commandClient3.close();
		} 
	    catch (DeploymentException e)	{ e.printStackTrace();Assertions.fail(e.getMessage()); } 
	    catch (IOException e)			{ e.printStackTrace();Assertions.fail(e.getMessage()); }
		catch (InterruptedException e)	{ e.printStackTrace();Assertions.fail(e.getMessage()); }
		
		// Arrêter les serveurs
		try { Thread.sleep(10000); } 
		catch (InterruptedException e) { e.printStackTrace(); }
		tServer.shutdownNow();
		tServer = null;
		Assertions.assertNull(tServer);
	}
	
//	@Test
//	public void testClientConnectionAndTokenRetrieval04() {
//		
//		TerminalServerWebSocket tServer = null;
//		ClientManager clientManager;
//		
//		// java.util.logging.Logger.getLogger(Server.class.getClass().getName()).setLevel(Level.ALL);
//		TerminalServer tmpServer = TerminalServer.builder( false );
//		Logger.printlnLog(LoggerLevel.LL_DEBUG, "tmpServer: [" + tmpServer + "}");
//		Assertions.assertNotNull(tmpServer);
//		Assertions.assertTrue(tmpServer instanceof TerminalServerWebSocket);
//		tServer = (TerminalServerWebSocket) tmpServer;
//		Logger.printlnLog(LoggerLevel.LL_DEBUG, "tServer: [" + tServer + "}");
//		Assertions.assertNotNull(tServer);
//		tServer.start();
//		
//		// Initialiser le gestionnaire de clients
//		clientManager = ClientManager.createClient();
//
//		String serverNameCLI		= TerminalClient.getProperty( "gabywald.terminal.server.servername" );
//		String serverAUTHCLI 		= TerminalClient.getProperty( "gabywald.terminal.server.authentication" );
//		String serverMAINCLI 		= TerminalClient.getProperty( "gabywald.terminal.server.mainservice" );
//		
//		Logger.printlnLog(LoggerLevel.LL_DEBUG, "serverNameCLI: {" + serverNameCLI + "}");
//		Logger.printlnLog(LoggerLevel.LL_DEBUG, "serverAUTHCLI: {" + serverAUTHCLI + "}");
//		Logger.printlnLog(LoggerLevel.LL_DEBUG, "serverMAINCLI: {" + serverMAINCLI + "}");
//		
//		int portAuthentiCLI	= Integer.parseInt(TerminalClient.getProperty("gabywald.terminal.server.port.authentication"));
//		int portServicesCLI	= Integer.parseInt(TerminalClient.getProperty("gabywald.terminal.server.port.services"));
//		
//		Logger.printlnLog(LoggerLevel.LL_DEBUG, "portAuthentiCLI: [" + portAuthentiCLI + "]");
//		Logger.printlnLog(LoggerLevel.LL_DEBUG, "portServicesCLI: [" + portServicesCLI + "]");
//		
//	    String  login = TerminalClient.getProperty( "gabywald.terminal.user.username" ), 
//	    		psswd = TerminalClient.getProperty( "gabywald.terminal.user.password" );
//	    
//		try {
//			
//			// Client 1 : Se connecter au serveur Basic
//			TerminalClientBasicClientEndpoint basicClient1 = new TerminalClientBasicClientEndpoint();
//			Session testBasic1 = clientManager.connectToServer(basicClient1, 
//					URI.create("ws://" + serverNameCLI + ":" + (portServicesCLI-1) + "/"));
//			Assertions.assertNotNull(testBasic1);
//			Thread.sleep(1000); // Attendre pour simuler une interaction
//			basicClient1.close();
//			
//			// Client 3 : Obtenir un token
//			TerminalClientAuthClientEndpoint authClient3 = new TerminalClientAuthClientEndpoint();
//			CountDownLatch latch3 = new CountDownLatch(1);
//			Session testBasic6 = clientManager.connectToServer(authClient3, 
//					URI.create("ws://" + serverNameCLI + ":" + portAuthentiCLI + "/" + serverAUTHCLI + "?login=" + login + "&psswd=" + Base64.encodeBase64String( psswd.getBytes() )));
//			Assertions.assertNotNull(testBasic6);
//			latch3.await(5, TimeUnit.SECONDS);
//			String token3 = authClient3.getToken();
//			Assertions.assertNotNull(token3, "Client 3 should receive a token");
//	
//			// Client 3 : Se connecter au serveur de commandes avec le token
//			TerminalClientCommandClientEndpoint commandClient3 = new TerminalClientCommandClientEndpoint();
//			Session testBasic7 = clientManager.connectToServer(commandClient3, 
//						URI.create("ws://" + serverNameCLI + ":" + portServicesCLI + "/" + serverMAINCLI + "?token=" + token3));
//			Assertions.assertNotNull(testBasic7);
//			commandClient3.sendCommand("help");
//			latch3.await(5, TimeUnit.SECONDS);
//			Assertions.assertTrue(commandClient3.getResponse().startsWith("Available commands:"));
//			Assertions.assertEquals("user@terminal:~$", commandClient3.getPrompt());
//	
//			// Fermer les connexions
//			authClient3.close();
//			commandClient3.close();
//		} 
//	    catch (DeploymentException e)	{ e.printStackTrace();Assertions.fail(e.getMessage()); } 
//	    catch (IOException e)			{ e.printStackTrace();Assertions.fail(e.getMessage()); }
//		catch (InterruptedException e)	{ e.printStackTrace();Assertions.fail(e.getMessage()); }
//		
//		// Arrêter les serveurs
//		try { Thread.sleep(10000); } 
//		catch (InterruptedException e) { e.printStackTrace(); }
//		tServer.shutdownNow();
//		tServer = null;
//		Assertions.assertNull(tServer);
//	}
	
}
