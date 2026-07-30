package gabywald.terminal.commands.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gabywald.terminal.TerminalState;
import gabywald.terminal.commands.CdCommand;
import gabywald.terminal.filesystem.TerminalDirectory;

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
        Assertions.assertEquals(this.root, this.state.getCurrentDirectory());
    }
    
    @Test
    void testCdToHome() {
    	this.cdCommand.execute(this.state, new String[]{"home"});
        Assertions.assertEquals(this.home, this.state.getCurrentDirectory());
    }
    
    @Test
    void testCdToSubdirectory() {
    	this.cdCommand.execute(this.state, new String[]{"documents"});
        Assertions.assertEquals(this.documents, this.state.getCurrentDirectory());
    }
    
    @Test
    void testCdToParent() {
    	this.cdCommand.execute(this.state, new String[]{"documents"});
    	this.cdCommand.execute(this.state, new String[]{".."});
       Assertions.assertEquals(this.home, this.state.getCurrentDirectory());
    }
    
    @Test
    void testCdToCurrent() {
    	this.cdCommand.execute(this.state, new String[]{"."});
       Assertions.assertEquals(this.home, this.state.getCurrentDirectory());
    }
    
    @Test
    void testCdNoArgs() {
    	this.cdCommand.execute(this.state, new String[0]);
       Assertions.assertEquals(this.root, this.state.getCurrentDirectory());
    }
    
    @Test
    void testCdNonExistent() {
        String result = this.cdCommand.execute(this.state, new String[]{"nonexistent"});
       Assertions.assertTrue(result.contains("no such file or directory"));
       Assertions.assertEquals(this.home, this.state.getCurrentDirectory());
    }
    
    @Test
    void testCdAbsolutePath() {
    	this.cdCommand.execute(this.state, new String[]{"/home/documents"});
       Assertions.assertEquals(this.documents, this.state.getCurrentDirectory());
    }
    
}
