package gabywald.websocket.onemoretime;

import gabywald.websocket.onemoretime.client.AuthClientEndpoint;
import gabywald.websocket.onemoretime.client.CommandClientEndpoint;
import org.glassfish.tyrus.client.ClientManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gabywald.websocket.onemoretime.server.AuthServer;
import gabywald.websocket.onemoretime.server.CommandServer;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Gabriel Chandesris (2026)
 */
public class WebSocketIntegrationTest {
	private AuthServer authServer;
	private CommandServer commandServer;
	private ClientManager clientManager;

	@BeforeEach
	public void setUp() throws Exception {
		// Démarrer les serveurs
		this.authServer = new AuthServer(8080);
		this.commandServer = new CommandServer(8081);
		this.authServer.start();
		this.commandServer.start();

		// Initialiser le gestionnaire de clients
		this.clientManager = ClientManager.createClient();
	}

	@AfterEach
	public void tearDown() throws Exception {
		// Arrêter les serveurs
		if (this.authServer != null) {
			this.authServer.stop();
		}
		if (this.commandServer != null) {
			this.commandServer.stop();
		}
	}

	@Test
	public void testClientConnectionAndTokenRetrieval() throws Exception {
		// Client 1 : Obtenir un token
		AuthClientEndpoint authClient1 = new AuthClientEndpoint();
		CountDownLatch latch1 = new CountDownLatch(1);
	    this.clientManager.connectToServer(authClient1, URI.create("ws://localhost:8080/auth"));
		latch1.await(5, TimeUnit.SECONDS); // Attendre la réception du token
		String token1 = authClient1.getToken();
		Assertions.assertNotNull(token1, "Client 1 should receive a token");

		// Client 1 : Se connecter au serveur de commandes avec le token
		CommandClientEndpoint commandClient1 = new CommandClientEndpoint();
		this.clientManager.connectToServer(commandClient1, URI.create("ws://localhost:8081/command?token=" + token1));
		commandClient1.sendCommand("test_command_1");
		Thread.sleep(1000); // Attendre pour simuler une interaction

		// Client 2 : Obtenir un token
		AuthClientEndpoint authClient2 = new AuthClientEndpoint();
		CountDownLatch latch2 = new CountDownLatch(1);
		this.clientManager.connectToServer(authClient2, URI.create("ws://localhost:8080/auth"));
		latch2.await(5, TimeUnit.SECONDS);
		String token2 = authClient2.getToken();
		Assertions.assertNotNull(token2, "Client 2 should receive a token");

		// Client 2 : Se connecter au serveur de commandes avec le token
		CommandClientEndpoint commandClient2 = new CommandClientEndpoint();
		this.clientManager.connectToServer(commandClient2, URI.create("ws://localhost:8081/command?token=" + token2));
		commandClient2.sendCommand("test_command_2");
		Thread.sleep(1000);

		// Fermer les connexions
		authClient1.close();
		commandClient1.close();
		authClient2.close();
		commandClient2.close();
	}
}
