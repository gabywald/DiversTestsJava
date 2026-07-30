package gabywald.websocket.chatServerSide.messages;

import java.util.Arrays;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import org.json.JSONObject;

import gabywald.utilities.logger.Logger;
import gabywald.utilities.logger.Logger.LoggerLevel;

/**
 * 
 * @author Gabriel Chandesris (2024, 2026)
 */
public class MessageEncoder implements Encoder.Text<Message> {

    @Override
    public String encode(Message message) throws EncodeException {
    	
    	Logger.printlnLog(LoggerLevel.LL_DEBUG, "EN-CODING MESSAGE !!");
    	
    	JSONObject jsonobj = new JSONObject();
    	Arrays.asList( Message.fieldNames ).forEach( nameOfField -> {
    		jsonobj.put(nameOfField, message.hasField(nameOfField) ? message.getField(nameOfField) : "(null)" );
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
