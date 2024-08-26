package gabywald.websocket.chatServerSide.messages;

public class Message {
    private String from;
    private String to;
    private String content;
    
    public String getFrom()		{ return this.from; }
    public String getTo()		{ return this.to; }
    public String getContent()	{ return this.content; }
    
	public void setFrom(String from)			{ this.from = from; }
	public void setTo(String to)				{ this.to = to; }
	public void setContent(String content)		{ this.content = content; }
	public void setUsername(String username) 	{
		// TODO Auto-generated method stub
		
	}
	public void setMessage(String string)		{
		// TODO Auto-generated method stub
		
	}
    
    // standard constructors, getters, setters
    
}
