package gabywald.terminal3.serverside.users;

import gabywald.terminal3.serverside.filesystem.TerminalState;

/**
 * 
 * @author Gabriel Chandesris (2026)
 */
public class User {
    
    private String username = null, login = null;
    private Role role = null;
    private TerminalState state = null;

    public enum Role {
        BASIC_PLAYER, CREATOR, ADMIN
    }

    public enum Permission {
        PLAYING, CREATION, ADMINISTRATION
    }
    
    public User(String login, String ident, Role rolez) {
        this.login = login;
        this.username = ident;
        this.role = rolez;
        // TODO best load from DB of users !!
        this.state = new TerminalState(); 
    }

    public String getUsername() { return this.username; }
    public String getLogin()	{ return this.login; }
    public Role getRole()		{ return this.role; }
    public String getRoleSTR()	{ return (this.role != null)?this.role.toString():new String(""); }
    
    public TerminalState getState() { return this.state; }

    /* ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** */
    /* ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** */
    /* ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** */
    
    // TODO load a local DB of users !!
    
    public enum UserS {
        DEFAULT_TEST("user", "psswd", "Test User", null), 
        CREATOR("johnsmith", "12345", "John Smith", User.Role.CREATOR), 
        MEMBER("neo", "54321", "Ralph Anderson", User.Role.BASIC_PLAYER);

        private String login = null, psswd = null, ident = null;
        private Role rolez = null;
        
        UserS(String login, String psswd, String ident, Role rolez) {
            this.login = login; 
            this.psswd = psswd;
            this.ident = ident;
            this.rolez = rolez;
        }
        
        public static boolean has(String login) { 
            for (int i = 0 ; i < UserS.values().length ; i++) 
                { if (UserS.values()[i].login.equals(login)) { return true; } }
            return false;
        }
        
        static User getUser(String login, String psswd) {
            // "Library User" including default user
            for (int i = 0 ; i < UserS.values().length ; i++) { 
                if (UserS.values()[i].login.equals(login)) { 
                    if (UserS.values()[i].psswd.equals(psswd))
                        { return new User(UserS.values()[i].login, UserS.values()[i].ident, UserS.values()[i].rolez); }
                    else { return null; }
                } 
            }
            return null;
        }
        
        static User getUserWithName(String login, String name) {
            // "Library User" including default user
            for (int i = 0 ; i < UserS.values().length ; i++) { 
                if (UserS.values()[i].login.equals(login)) { 
                    if (UserS.values()[i].ident.equals(name))
                        { return new User(UserS.values()[i].login, UserS.values()[i].ident, UserS.values()[i].rolez); }
                    else { return null; }
                } 
            }
            return null;
        }
    
    }

    static User getUser(String login, String psswd) {
        if (UserS.has(login)) { return UserS.getUser(login, psswd); } 
        else { return null; }
    }
    
    static User getUserWithName(String login, String name) {
        if (UserS.has(login)) { return UserS.getUserWithName(login, name); } 
        else { return null; }
    }
    
    // TODO temporary load of User's (easy to get and synchronise with DB)
    
}
