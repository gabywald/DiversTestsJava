package gabywald.terminal3.serverside.users;

import java.util.HashMap;
import java.util.Map;

import gabywald.terminal3.serverside.users.User.UserS;

/**
 * @author Gabriel Chandesris (2026)
 */
public class UserDB {
	private Map<String, User> bufferOfUsers = new HashMap<>();
	
	private static UserDB instance;
	
	public static UserDB getInstance() {
		if (UserDB.instance == null) 
			{ UserDB.instance = new UserDB(); }
		return UserDB.instance;
	}
	
	private UserDB() { ; }
	
    public User getUser(String login, String psswd) {
    	if (this.bufferOfUsers.containsKey(login)) {
    		User toReturn = this.bufferOfUsers.get(login);
    		if (toReturn.getLogin().equals(login)) 
    			{ return toReturn; }
    	} else {
    		if (UserS.has(login)) { 
            	User toReturn = UserS.getUser(login, psswd);
            	this.bufferOfUsers.put(login, toReturn);
            	return toReturn; 
            } else { return null; }
    	}
    	return null;
    }
    
    public User getUserWithName(String login, String name) {
    	if (this.bufferOfUsers.containsKey(login)) {
    		User toReturn = this.bufferOfUsers.get(login);
    		if (toReturn.getLogin().equals(login) && toReturn.getUsername().equals(name)) 
    			{ return toReturn; }
    	} else { 
    		if (UserS.has(login)) { 
	        	User toReturn = UserS.getUserWithName(login, name);
	        	this.bufferOfUsers.put(login, toReturn);
	        	return toReturn; 
	        } else { return null; }
    	}
    	return null;
    }
    
}
