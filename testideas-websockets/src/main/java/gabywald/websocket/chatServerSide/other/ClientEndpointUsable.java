package gabywald.websocket.chatServerSide.other;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.ClientEndpoint;
import javax.websocket.DeploymentException;
import javax.websocket.OnMessage;
import javax.websocket.Session;

@ClientEndpoint
public class ClientEndpointUsable {
    private static final Logger LOGGER = Logger.getLogger(ClientEndpointUsable.class.getName());
    
    @OnMessage
    public void onMessage(String message, Session session) {
       LOGGER.log(Level.INFO, message);
    }
    
    public static void main(String[] args) {
        LOGGER.log(Level.INFO, "Lancement client");
        javax.websocket.WebSocketContainer container =
            javax.websocket.ContainerProvider.getWebSocketContainer();
        try {
            container.connectToServer(    ClientEndpointUsable.class, 
                                        new URI("ws://localhost:8080/websocket/server"));
                                        // new URI("ws://localhost:8887/"));
                                        // new URI("ws://localhost:8887/chat/usertest"));
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        
       while(true) { ; }
    }
}
