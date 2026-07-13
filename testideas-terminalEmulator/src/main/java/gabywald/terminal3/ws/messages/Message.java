package gabywald.terminal3.ws.messages;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Gabriel Chandesris (2024, 2026)
 */
public class Message {
	
	public static String[] fieldNames = { "from", "to", "content", "username", "message", 
										  "cmd", "prompt", "result", "login", "psswd", 
										  "token" };
	public enum MessageFields {
		FROM, TO, CONTENT, USERNAME, MESSAGE, 
		CMD, PROMPT, RESULT, LOGIN, PSSWD, 
		TOKEN;
	};
	private Map<String, String> mapOfFields = new HashMap<String, String>();
	boolean hasField(String name) { return this.mapOfFields.containsKey(name); }
	void setField(String name, String value) { this.mapOfFields.put(name, value); }
	String getField(String name) { return this.mapOfFields.get(name); }
	
    public String getFrom()		{ return this.getField("from"); }
    public String getTo()		{ return this.getField("to"); }
    public String getContent()	{ return this.getField("content"); }
    public String getUsername()	{ return this.getField("username"); }
    public String getMessage()	{ return this.getField("message"); }
    public String getCMD()		{ return this.getField("cmd"); }
    public String getPROMPT()	{ return this.getField("prompt"); }
    public String getRESULT()	{ return this.getField("result"); }
    public String getLOGIN()	{ return this.getField("login"); }
    public String getPSSWD()	{ return this.getField("psswd"); }
    public String getTOKEN()	{ return this.getField("token"); }
    
	public void setFrom(String from)			{ this.setField("from", from); }
	public void setTo(String to)				{ this.setField("to", to); }
	public void setContent(String content)		{ this.setField("content", content); }
	public void setUsername(String username)	{ this.setField("username", username); }
	public void setMessage(String message)		{ this.setField("message", message); }
	public void setCMD(String cmd)				{ this.setField("cmd", cmd); }
	public void setPROMPT(String prompt)		{ this.setField("prompt", prompt); }
	public void setRESULT(String result)		{ this.setField("result", result); }
	public void setLOGIN(String login)			{ this.setField("login", login); }
	public void setPSSWD(String psswd)			{ this.setField("psswd", psswd); }
	public void setTOKEN(String token)			{ this.setField("psswd", token); }
	
	public boolean isError()			{ return this.hasField("error"); }
	public void setError(String error)	{ this.setField("error", error); };
	
	public String toString() {
		StringBuilder sbToReturn = new StringBuilder();
		sbToReturn.append("Message:\n");
		for (String key : this.mapOfFields.keySet()) 
			{ sbToReturn.append("\t").append(key).append("=")
						.append(this.mapOfFields.get(key)).append("\n"); }
		return sbToReturn.toString();
	}
    
    // standard constructors, getters, setters
    
}
