package gabywald.websocket.chatServerSide.other3.tests;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.glassfish.tyrus.server.Server;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import gabywald.websocket.chatServerSide.other3.explore.BasicClient3;
import gabywald.websocket.chatServerSide.other3.explore.WebSocketServer2;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;

class WebSocketServer2EndpointTest {
	
	private Server server = null;
	
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
        
		// try {
        
			server = new Server("localhost", 8080, "", null, WebSocketServer2.class);
			
			server.start();
			
			// BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			// System.out.println("Please press a key to stop the server.");
			// reader.readLine();
		// } catch (IOException e) { e.printStackTrace(); }
			
		System.out.println( "@BeforeEach END " );
    }
    
    @AfterEach
    void tearDown() throws Exception {
        System.out.println( "@AfterEach" );
		if (server != null) { server.stop(); }
		System.out.println( "@AfterEach END" );
    }

	@Test
	void testBASIC() {
		// ServerEndpointConfig sec = ServerEndpointConfig.Builder.create(EchoEndpoint.class, "/echo").build();
		
		System.out.println("TEST 01");
		
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        
        System.out.println("TEST 02");

        try {
			// Session currentSession = container.connectToServer(	BasicClient.class, new URI("ws://localhost:8080/websocket/server"));
        	
        	BasicClient3 bc3 = new BasicClient3(new URI("ws://localhost:8080/websocket/server"));
			
			System.out.println("TEST 03");
			
			bc3.sendMessage("message");
			
			// bc3.processMessage("pong ?");
			
			// bc3.sendMessage("message1");
			// bc3.sendMessage("message2");
			// bc3.sendMessage("message3");
			
			bc3.closeConnection();
			
			// currentSession.getBasicRemote().sendText( "test" );
			// currentSession.getAsyncRemote().sendText("txt");
			
			System.out.println("TEST 04");
			
		} catch (IOException | URISyntaxException e) { System.out.println("Exception");e.printStackTrace(); }
        
        System.out.println("TEST 05");
		
		// fail("Not yet implemented");
	}

}
