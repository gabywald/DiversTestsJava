package gabywald.terminal.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gabywald.terminal.TerminalState;
import gabywald.terminal.filesystem.TerminalDirectory;

/**
 * @author Gabriel Chandesris (2026)
 */
class TerminalStateTest {
    private TerminalState state;
    private TerminalDirectory root;
    
    @BeforeEach
    void setUp() {
        this.state = new TerminalState();
        this.root = state.getRootDirectory();
    }
    
    @AfterEach
    void tearDown() {
        this.state = null; root = null;
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
    	Assertions.assertTrue(this.state.getCommandHistory().isEmpty());
        this.state.addToHistory("ls");
        Assertions.assertEquals(1, this.state.getCommandHistory().size());
        Assertions.assertEquals("ls", this.state.getCommandHistory().get(0));
        this.state.addToHistory("cd home");
        Assertions.assertEquals(2, this.state.getCommandHistory().size());
    }
    
    @Test
    void testHistoryNavigation() {
    	this.state.addToHistory("ls");
    	this.state.addToHistory("cd home");
    	this.state.addToHistory("pwd");
    	this.state.resetHistoryIndex();
        String prev = this.state.getPreviousCommand();
        Assertions.assertEquals("pwd", prev);
        prev = this.state.getPreviousCommand();
        Assertions.assertEquals("cd home", prev);
        String next = this.state.getNextCommand();
        Assertions.assertEquals("pwd", next);
    }
    
    @Test
    void testPromptUpdate() {
    	Assertions.assertTrue(state.getPrompt().contains("~$"));
        TerminalDirectory home = root.createDirectory("home");
        state.setCurrentDirectory(home);
        Assertions.assertTrue(state.getPrompt().contains("/home$"));
    }
    
}