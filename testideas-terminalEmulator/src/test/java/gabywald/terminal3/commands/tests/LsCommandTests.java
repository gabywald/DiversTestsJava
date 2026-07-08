package gabywald.terminal3.commands.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gabywald.terminal3.serverside.filesystem.TerminalDirectory;
import gabywald.terminal3.serverside.filesystem.TerminalState;
import gabywald.terminal3.serverside.shell.commands.LsCommand;

/**
 * @author Gabriel Chandesris (2026)
 */
class LsCommandTest {
    private TerminalState state;
    private LsCommand lsCommand;
    private TerminalDirectory root;
    private TerminalDirectory home;
    
    @BeforeEach
    void setUp() {
        this.state = new TerminalState();
        this.root = this.state.getRootDirectory();
        this.home = this.root.createDirectory("home");
        this.state.setCurrentDirectory(this.home);
        this.home.createFile("file1.txt");
        this.home.createFile("file2.txt");
        this.home.createDirectory("docs");
        this.lsCommand = new LsCommand();
    }
    
    @AfterEach
    void tearDown() {
    	this.state = null; this.lsCommand = null; this.root = null; this.home = null;
    }
    
    @Test
    void testBasicLs() {
        String result = this.lsCommand.execute(this.state, new String[0]);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("docs"));
        Assertions.assertTrue(result.contains("file1.txt"));
        Assertions.assertTrue(result.contains("file2.txt"));
    }
    
    @Test
    void testLsWithHiddenFiles() {
    	this.home.createFile(".hidden");
        String result = this.lsCommand.execute(this.state, new String[0]);
        Assertions.assertFalse(result.contains(".hidden"));
        result = this.lsCommand.execute(this.state, new String[]{"-a"});
        Assertions.assertTrue(result.contains(".hidden"));
    }
    
    @Test
    void testLsLongFormat() {
        String result = this.lsCommand.execute(this.state, new String[]{"-l"});
        Assertions.assertTrue(result.contains("total"));
        Assertions.assertTrue(result.contains("drwx"));
        Assertions.assertTrue(result.contains("-rw-"));
    }
    
    @Test
    void testLsReverseOrder() {
        String resultNormal = this.lsCommand.execute(this.state, new String[0]);
        String resultReverse = this.lsCommand.execute(this.state, new String[]{"-r"});
        Assertions.assertNotEquals(resultNormal, resultReverse);
    }
    
    @Test
    void testLsSpecificDirectory() {
        TerminalDirectory docs = this.home.createDirectory("docs");
        Assertions.assertNull(docs); // not created and null returned because already exists !
        docs = (TerminalDirectory) this.home.getChild("docs");
        docs.createFile("readme.txt");
        String result = this.lsCommand.execute(this.state, new String[]{"docs"});
        Assertions.assertTrue(result.contains("readme.txt"));
        Assertions.assertFalse(result.contains("file1.txt"));
    }
    
    @Test
    void testLsNonExistentDirectory() {
        String result = this.lsCommand.execute(this.state, new String[]{"nonexistent"});
        Assertions.assertTrue(result.contains("No such file or directory"));
    }
    
    @Test
    void testLsRootDirectory() {
        String result = this.lsCommand.execute(this.state, new String[]{"/"});
        Assertions.assertTrue(result.contains("home"));
    }
}
