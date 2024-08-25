package gabywald.websocket.chatServerSide.other;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.OnMessage;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/echo")
public class EchoEndpoint { // extends javax.websocket.Endpoint {

    private static final Logger LOGGER = Logger.getLogger(EchoEndpoint.class.getName());
    
    public EchoEndpoint() {
        super();
        LOGGER.info("invocation constructeur EchoEndpoint");
    }
    
//    @Override
//    public void onOpen(Session session, EndpointConfig config) {
//        final RemoteEndpoint.Basic remote = session.getBasicRemote();
//        session.addMessageHandler(new MessageHandler.Whole<String>() {
//            @Override
//            public void onMessage(String text) {
//                try {
//                    remote.sendText(ThreadSafeFormatter.getDateFormatter().format(new Date())+ " (EchoEndpoint) " + text);
//                } catch (IOException ioe) {
//                    LOGGER.severe("Could not send the message" + ioe);
//                }
//            }
//        });
//    }
//
//    @Override
//    public void onClose(Session session, CloseReason closeReason) {
//        LOGGER.info("onClose : "+closeReason);
//    }
//
//    @Override
//    public void onError(Session session, Throwable throwable) {
//        LOGGER.severe("onError" + throwable);
//    }
    
    @OnMessage
    public String echo(String message) {
        return ThreadSafeFormatter.getDateFormatter().format(new Date()) + " "+  message;
    }

}

class ThreadSafeFormatter {
    private static final ThreadLocal<SimpleDateFormat> formatter = 
        new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        }
    };

    public static DateFormat getDateFormatter() {
        return formatter.get();
    }
}
