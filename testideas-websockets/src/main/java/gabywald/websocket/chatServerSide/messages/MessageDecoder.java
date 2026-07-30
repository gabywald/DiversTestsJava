package gabywald.websocket.chatServerSide.messages;

import java.util.Arrays;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import org.json.JSONException;
import org.json.JSONObject;

import gabywald.utilities.logger.Logger;
import gabywald.utilities.logger.Logger.LoggerLevel;

/**
 * 
 * @author Gabriel Chandesris (2024, 2026)
 */
public class MessageDecoder implements Decoder.Text<Message> {

    @Override
    public Message decode(String s) throws DecodeException {
    	
    	Logger.printlnLog(LoggerLevel.LL_DEBUG, "DE-CODING MESSAGE !!");
    	
    	try {
	    	JSONObject jsonobj = new JSONObject( s );
	    	Message msg = new Message();
	    	Arrays.asList( Message.fieldNames ).forEach( nameOfField -> {
	    		msg.setField(nameOfField, jsonobj.getString( nameOfField ) );
	    	});
	        return msg;
    	} catch (JSONException je) {
    		String toTransmit = "[JSONException]: \"" + je.getMessage() + "\" ; /" + s + "/";
    		Message mje = new Message();
    		mje.setContent( "ERROR:" + toTransmit );
    		// mje.setError( toTransmit );
    		return mje;
    	}
    }

    @Override
    public boolean willDecode(String s) {
        return (s != null);
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
