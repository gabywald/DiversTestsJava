package gabywald.terminal.commands.tests;

import gabywald.terminal.TerminalState;
import gabywald.terminal.commands.CdCommand;
import gabywald.terminal.filesystem.TerminalDirectory;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Gabriel Chandesris (2026)
 */
class CdCommandTest {
    private TerminalState state;
    private CdCommand cdCommand;
    private TerminalDirectory root;
    private TerminalDirectory home;
    private TerminalDirectory documents;
    
    @BeforeEach
    void setUp() {
        this.state = new TerminalState();
        this.root = this.state.getRootDirectory();
        this.home = this.root.createDirectory("home");
        this.documents = this.home.createDirectory("documents");
        this.state.setCurrentDirectory(this.home);
        this.cdCommand = new CdCommand();
    }
    
    @AfterEach
    void tearDown() {
    	this.state = null; this.cdCommand = null; this.root = null; this.home = null; this.documents = null;
    }
    
    @Test
    void testCdToRoot() {
    	this.cdCommand.execute(this.state, new String[]{"/"});
        assertEquals(this.root, this.state.getCurrentDirectory());
    }
    
    @Test
    void testCdToHome() {
    	this.cdCommand.execute(this.state, new String[]{"home"});
        assertEquals(this.home, this.state.getCurrentDirectory());
    }
    
    @Test
    void testCdToSubdirectory() {
    	this.cdCommand.execute(this.state, new String[]{"documents"});
        assertEquals(this.documents, this.state.getCurrentDirectory());
    }
    
    @Test
    void testCdToParent() {
    	this.cdCommand.execute(this.state, new String[]{"documents"});
    	this.cdCommand.execute(this.state, new String[]{".."});
        assertEquals(this.home, this.state.getCurrentDirectory());
    }
    
    @Test
    void testCdToCurrent() {
    	this.cdCommand.execute(this.state, new String[]{"."});
        assertEquals(this.home, this.state.getCurrentDirectory());
    }
    
    @Test
    void testCdNoArgs() {
    	this.cdCommand.execute(this.state, new String[0]);
        assertEquals(this.root, this.state.getCurrentDirectory());
    }
    
    @Test
    void testCdNonExistent() {
        String result = this.cdCommand.execute(this.state, new String[]{"nonexistent"});
        assertTrue(result.contains("no such file or directory"));
        assertEquals(this.home, this.state.getCurrentDirectory());
    }
    
    @Test
    void testCdAbsolutePath() {
    	this.cdCommand.execute(this.state, new String[]{"/home/documents"});
        assertEquals(this.documents, this.state.getCurrentDirectory());
    }
    
}
