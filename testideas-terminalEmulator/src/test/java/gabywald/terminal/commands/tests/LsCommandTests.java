package gabywald.terminal.commands.tests;

import gabywald.terminal.TerminalState;
import gabywald.terminal.commands.LsCommand;
import gabywald.terminal.filesystem.Directory;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class LsCommandTest {
    private TerminalState state;
    private LsCommand lsCommand;
    private Directory root;
    private Directory home;
    
    @BeforeEach
    void setUp() {
        state = new TerminalState();
        root = state.getRootDirectory();
        home = root.createDirectory("home");
        state.setCurrentDirectory(home);
        home.createFile("file1.txt");
        home.createFile("file2.txt");
        home.createDirectory("docs");
        lsCommand = new LsCommand();
    }
    
    @AfterEach
    void tearDown() {
        state = null; lsCommand = null; root = null; home = null;
    }
    
    @Test
    void testBasicLs() {
        String result = lsCommand.execute(state, new String[0]);
        assertNotNull(result);
        assertTrue(result.contains("docs"));
        assertTrue(result.contains("file1.txt"));
        assertTrue(result.contains("file2.txt"));
    }
    
    @Test
    void testLsWithHiddenFiles() {
        home.createFile(".hidden");
        String result = lsCommand.execute(state, new String[0]);
        assertFalse(result.contains(".hidden"));
        result = lsCommand.execute(state, new String[]{"-a"});
        assertTrue(result.contains(".hidden"));
    }
    
    @Test
    void testLsLongFormat() {
        String result = lsCommand.execute(state, new String[]{"-l"});
        assertTrue(result.contains("total"));
        assertTrue(result.contains("drwx"));
        assertTrue(result.contains("-rw-"));
    }
    
    @Test
    void testLsReverseOrder() {
        String resultNormal = lsCommand.execute(state, new String[0]);
        String resultReverse = lsCommand.execute(state, new String[]{"-r"});
        assertNotEquals(resultNormal, resultReverse);
    }
    
    @Test
    void testLsSpecificDirectory() {
        Directory docs = home.createDirectory("docs");
        docs.createFile("readme.txt");
        String result = lsCommand.execute(state, new String[]{"docs"});
        assertTrue(result.contains("readme.txt"));
        assertFalse(result.contains("file1.txt"));
    }
    
    @Test
    void testLsNonExistentDirectory() {
        String result = lsCommand.execute(state, new String[]{"nonexistent"});
        assertTrue(result.contains("No such file or directory"));
    }
    
    @Test
    void testLsRootDirectory() {
        String result = lsCommand.execute(state, new String[]{"/"});
        assertTrue(result.contains("home"));
    }
}
