package gabywald.rest.users;

/**
 * 
 * @author Gabriel Chandesris (2024)
 */
public class User {
    
    private String username = null, login = null;
    private Role role = null;

    public enum Role {
        LIBRARIAN, MEMBER
    }

    public enum Permission {
        ADD_BOOK, REMOVE_BOOK, VIEW_BOOKS
    }
    
    public User(String login, String ident, Role rolez) {
        this.login = login;
        this.username = ident;
        this.role = rolez;
    }

    public String getUsername() { return this.username; }
    public String getLogin()    { return this.login; }
    public Role getRole()        { return this.role; }
    public String getRoleSTR()    { return (this.role != null)?this.role.toString():new String(""); }

    /* ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** */
    /* ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** */
    /* ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** */
    
    public enum UserS {
        DEFAULT_TEST("user", "psswd", "Test User", null), 
        LIBRARIAN("johnsmith", "12345", "John Smith", User.Role.LIBRARIAN), 
        MEMBER("neo", "54321", "Ralph Anderson", User.Role.MEMBER);

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
        
        public static User getUser(String login, String psswd) {
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
    
    }

    public static User getUser(String login, String psswd) {
        if (UserS.has(login)) { return UserS.getUser(login, psswd); } 
        else { return null; }
    }
    
}
