package gabywald.terminal.commands.tests;

import gabywald.terminal.TerminalState;
import gabywald.terminal.commands.CdCommand;
import gabywald.terminal.filesystem.Directory;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class CdCommandTest {
    private TerminalState state;
    private CdCommand cdCommand;
    private Directory root;
    private Directory home;
    private Directory documents;
    
    @BeforeEach
    void setUp() {
        state = new TerminalState();
        root = state.getRootDirectory();
        home = root.createDirectory("home");
        documents = home.createDirectory("documents");
        state.setCurrentDirectory(home);
        cdCommand = new CdCommand();
    }
    
    @AfterEach
    void tearDown() {
        state = null; cdCommand = null; root = null; home = null; documents = null;
    }
    
    @Test
    void testCdToRoot() {
        cdCommand.execute(state, new String[]{"/"});
        assertEquals(root, state.getCurrentDirectory());
    }
    
    @Test
    void testCdToHome() {
        cdCommand.execute(state, new String[]{"home"});
        assertEquals(home, state.getCurrentDirectory());
    }
    
    @Test
    void testCdToSubdirectory() {
        cdCommand.execute(state, new String[]{"documents"});
        assertEquals(documents, state.getCurrentDirectory());
    }
    
    @Test
    void testCdToParent() {
        cdCommand.execute(state, new String[]{"documents"});
        cdCommand.execute(state, new String[]{".."});
        assertEquals(home, state.getCurrentDirectory());
    }
    
    @Test
    void testCdToCurrent() {
        cdCommand.execute(state, new String[]{"."});
        assertEquals(home, state.getCurrentDirectory());
    }
    
    @Test
    void testCdNoArgs() {
        cdCommand.execute(state, new String[0]);
        assertEquals(root, state.getCurrentDirectory());
    }
    
    @Test
    void testCdNonExistent() {
        String result = cdCommand.execute(state, new String[]{"nonexistent"});
        assertTrue(result.contains("no such file or directory"));
        assertEquals(home, state.getCurrentDirectory());
    }
    
    @Test
    void testCdAbsolutePath() {
        cdCommand.execute(state, new String[]{"/home/documents"});
        assertEquals(documents, state.getCurrentDirectory());
    }
}
