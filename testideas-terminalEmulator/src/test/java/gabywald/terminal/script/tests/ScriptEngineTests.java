package gabywald.terminal.script.tests;

import gabywald.terminal.TerminalState;
import gabywald.terminal.filesystem.Directory;
import gabywald.terminal.filesystem.TerminalFile;
import gabywald.terminal.script.ScriptEngine;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class ScriptEngineTest {
    private TerminalState state;
    private ScriptEngine engine;
    private Directory root;
    private Directory home;
    
    @BeforeEach
    void setUp() {
        state = new TerminalState();
        root = state.getRootDirectory();
        home = root.createDirectory("home");
        state.setCurrentDirectory(home);
        engine = new ScriptEngine(state);
    }
    
    @AfterEach
    void tearDown() {
        state = null; engine = null; root = null; home = null;
    }
    
    @Test
    void testEmptyScript() {
        String result = engine.execute("");
        assertEquals("", result);
    }
    
    @Test
    void testNullScript() {
        String result = engine.execute(null);
        assertEquals("", result);
    }
    
    @Test
    void testCommentOnly() {
        String result = engine.execute("# This is a comment");
        assertEquals("", result);
    }
    
    @Test
    void testEchoCommand() {
        String result = engine.execute("echo Hello World");
        assertEquals("Hello World", result.trim());
    }
    
    @Test
    void testVariableSet() {
        String script = "set name=John\necho Hello $name";
        String result = engine.execute(script);
        assertEquals("Hello John", result.trim());
    }
    
    @Test
    void testVariableMultiple() {
        String script = "set first=Gabriel\nset last=Chandesris\necho $first $last";
        String result = engine.execute(script);
        assertEquals("Gabriel Chandesris", result.trim());
    }
    
    @Test
    void testCdCommand() {
        home.createDirectory("documents");
        String result = engine.execute("cd documents");
        assertEquals("", result);
        assertEquals("/home/documents", state.getCurrentDirectory().getPath());
    }
    
    @Test
    void testPwdCommand() {
        String result = engine.execute("pwd");
        assertEquals("/home", result.trim());
    }
    
    @Test
    void testLsCommand() {
        home.createFile("test.txt");
        String result = engine.execute("ls");
        assertTrue(result.contains("test.txt"));
    }
    
    @Test
    void testScriptFromFile() {
        TerminalFile scriptFile = home.createFile("script.sh");
        scriptFile.setContent("echo Script executed\necho From file");
        String result = engine.executeFile(scriptFile);
        assertTrue(result.contains("Script executed"));
        assertTrue(result.contains("From file"));
    }
    
    @Test
    void testIfConditionTrue() {
        String script = "set var=test\nif [ \"$var\" = \"test\" ]\n  echo Condition true\nfi";
        String result = engine.execute(script);
        assertTrue(result.contains("Condition true"));
    }
    
    @Test
    void testFileExistenceTest() {
        home.createFile("existing.txt");
        String script = "if [ -f \"existing.txt\" ]\n  echo File exists\nfi";
        String result = engine.execute(script);
        assertTrue(result.contains("File exists"));
    }
}
