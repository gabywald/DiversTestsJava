package gabywald.terminal3.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gabywald.terminal3.clientside.gui.TerminalHistory;
import gabywald.terminal3.serverside.filesystem.TerminalDirectory;
import gabywald.terminal3.serverside.filesystem.TerminalState;

/**
 * @author Gabriel Chandesris (2026)
 */
class TerminalStateTest {
    private TerminalState state;
    private TerminalHistory history;
    private TerminalDirectory root;
    
    @BeforeEach
    void setUp() {
        this.state = new TerminalState();
        this.root = this.state.getRootDirectory();
        this.history = new TerminalHistory();
    }
    
    @AfterEach
    void tearDown() {
        this.state = null; this.root = null; this.history = null;
    }
    
    @Test
    void testInitialState() {
        Assertions.assertNotNull(this.state.getCurrentDirectory());
        Assertions.assertNotNull(this.state.getRootDirectory());
        Assertions.assertEquals(root, this.state.getCurrentDirectory());
        Assertions.assertEquals("/", this.state.getCurrentDirectory().getPath());
    }
    
    @Test
    void testSetCurrentDirectory() {
        TerminalDirectory home = root.createDirectory("home");
        this.state.setCurrentDirectory(home);
        Assertions.assertEquals(home, this.state.getCurrentDirectory());
        Assertions.assertTrue(this.state.getPrompt().contains("/home$"));
    }
    
    @Test
    void testCommandHistory() {
    	Assertions.assertTrue(this.history.getCommandHistory().isEmpty());
        this.history.addToHistory("ls");
        Assertions.assertEquals(1, this.history.getCommandHistory().size());
        Assertions.assertEquals("ls", this.history.getCommandHistory().get(0));
        this.history.addToHistory("cd home");
        Assertions.assertEquals(2, this.history.getCommandHistory().size());
    }
    
    @Test
    void testHistoryNavigation() {
    	this.history.addToHistory("ls");
    	this.history.addToHistory("cd home");
    	this.history.addToHistory("pwd");
    	this.history.resetHistoryIndex();
        String prev = this.history.getPreviousCommand();
        Assertions.assertEquals("pwd", prev);
        prev = this.history.getPreviousCommand();
        Assertions.assertEquals("cd home", prev);
        String next = this.history.getNextCommand();
        Assertions.assertEquals("pwd", next);
    }
    
    @Test
    void testPromptUpdate() {
    	Assertions.assertTrue(this.state.getPrompt().contains("~$"));
        TerminalDirectory home = this.root.createDirectory("home");
        this.state.setCurrentDirectory(home);
        Assertions.assertTrue(this.state.getPrompt().contains("/home$"));
    }
    
}