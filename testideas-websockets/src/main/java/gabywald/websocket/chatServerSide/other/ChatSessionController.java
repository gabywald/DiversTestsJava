package gabywald.websocket.chatServerSide.other;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

/**
 * 
 * @author Gabriel Chandesris (2024)
 */
@SuppressWarnings("serial")
@Named
@ApplicationScoped
public class ChatSessionController implements java.io.Serializable {
    
    private Map<String, String> users = null;
    
    public ChatSessionController(){}
    
    @PostConstruct
    public void init(){
         users = new HashMap<>();
    }

    /**
     * @return the users
     */
    public Map<String, String> getUsers() {
        return users;
    }

    /**
     * @param for the users
     */
    public void setUsers(Map<String, String> users) {
        this.users = users;
    }
    
}
