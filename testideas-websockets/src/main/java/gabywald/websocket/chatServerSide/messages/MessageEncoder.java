package gabywald.websocket.chatServerSide.messages;

import java.util.Arrays;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import org.json.JSONObject;

/**
 * 
 * @author Gabriel Chandesris (2024)
 */
public class MessageEncoder implements Encoder.Text<Message> {

    @Override
    public String encode(Message message) throws EncodeException {
    	
    	System.out.println("EN-CODING MESSAGE !!");
    	
    	JSONObject jsonobj = new JSONObject();
    	Arrays.asList( Message.fieldNames ).forEach( nameOfField -> {
    		jsonobj.append(nameOfField, message.getField(nameOfField) );
    	});
        return jsonobj.toString();
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
        // Custom initialization logic
    }

    @Override
    public void destroy() {
        // Close resources
    }
}
