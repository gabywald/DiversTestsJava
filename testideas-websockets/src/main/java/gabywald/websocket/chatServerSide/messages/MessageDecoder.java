package gabywald.websocket.chatServerSide.messages;

import java.util.Arrays;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author Gabriel Chandesris (2024)
 */
public class MessageDecoder implements Decoder.Text<Message> {

    @Override
    public Message decode(String s) throws DecodeException {
    	
    	System.out.println("DE-CODING MESSAGE !!");
    	
    	try {
	    	JSONObject jsonobj = new JSONObject( s );
	    	Message msg = new Message();
	    	Arrays.asList( Message.fieldNames ).forEach( nameOfField -> {
	    		msg.setField(nameOfField, jsonobj.get( nameOfField ).toString());
	    	});
	        return msg;
    	} catch (JSONException je) {
    		Message mje = new Message();
    		mje.setContent("[JSONException]: \"" + je.getMessage() + "\" ; /" + s + "/");
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
