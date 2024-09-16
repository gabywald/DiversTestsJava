package gabywald.websocket.chatServerSide.messages;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Gabriel Chandesris (2024)
 */
public class Message {
	
	static String[] fieldNames = { "from", "to", "content", "username", "message" };
	private Map<String, String> mapOfFields = new HashMap<String, String>();
	void setField(String name, String value) { this.mapOfFields.put(name, value); }
	String getField(String name) { return this.mapOfFields.get(name); }
	
    public String getFrom()		{ return this.getField("from"); }
    public String getTo()		{ return this.getField("to"); }
    public String getContent()	{ return this.getField("content"); }
    public String getUsername()	{ return this.getField("username"); }
    public String getMessage()	{ return this.getField("message"); }
    
	public void setFrom(String from)			{ this.setField("from", from); }
	public void setTo(String to)				{ this.setField("to", to); }
	public void setContent(String content)		{ this.setField("content", content); }
	public void setUsername(String username)	{ this.setField("username", username); }
	public void setMessage(String message)		{ this.setField("message", message); }
    
    // standard constructors, getters, setters
    
}
