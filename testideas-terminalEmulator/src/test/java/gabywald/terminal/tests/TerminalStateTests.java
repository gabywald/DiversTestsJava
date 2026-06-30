package gabywald.terminal.tests;

import gabywald.terminal.TerminalState;
import gabywald.terminal.filesystem.Directory;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class TerminalStateTest {
    private TerminalState state;
    private Directory root;
    
    @BeforeEach
    void setUp() {
        state = new TerminalState();
        root = state.getRootDirectory();
    }
    
    @AfterEach
    void tearDown() {
        state = null; root = null;
    }
    
    @Test
    void testInitialState() {
        assertNotNull(state.getCurrentDirectory());
        assertNotNull(state.getRootDirectory());
        assertEquals(root, state.getCurrentDirectory());
        assertEquals("/", state.getCurrentDirectory().getPath());
    }
    
    @Test
    void testSetCurrentDirectory() {
        Directory home = root.createDirectory("home");
        state.setCurrentDirectory(home);
        assertEquals(home, state.getCurrentDirectory());
        assertTrue(state.getPrompt().contains("/home$"));
    }
    
    @Test
    void testCommandHistory() {
        assertTrue(state.getCommandHistory().isEmpty());
        state.addToHistory("ls");
        assertEquals(1, state.getCommandHistory().size());
        assertEquals("ls", state.getCommandHistory().get(0));
        state.addToHistory("cd home");
        assertEquals(2, state.getCommandHistory().size());
    }
    
    @Test
    void testHistoryNavigation() {
        state.addToHistory("ls");
        state.addToHistory("cd home");
        state.addToHistory("pwd");
        state.resetHistoryIndex();
        String prev = state.getPreviousCommand();
        assertEquals("pwd", prev);
        prev = state.getPreviousCommand();
        assertEquals("cd home", prev);
        String next = state.getNextCommand();
        assertEquals("pwd", next);
    }
    
    @Test
    void testPromptUpdate() {
        assertTrue(state.getPrompt().contains("~$"));
        Directory home = root.createDirectory("home");
        state.setCurrentDirectory(home);
        assertTrue(state.getPrompt().contains("/home$"));
    }
}