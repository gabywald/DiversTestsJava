package gabywald.terminal3.script.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gabywald.terminal3.script.ScriptEngine;
import gabywald.terminal3.serverside.filesystem.TerminalDirectory;
import gabywald.terminal3.serverside.filesystem.TerminalFile;
import gabywald.terminal3.serverside.filesystem.TerminalState;

/**
 * @author Gabriel Chandesris (2026)
 */
class ScriptEngineTest {
	private TerminalState state;
	private ScriptEngine engine;
	private TerminalDirectory root;
	private TerminalDirectory home;
	
	@BeforeEach
	void setUp() {
		this.state = new TerminalState();
		this.root = this.state.getRootDirectory();
		this.home = this.root.createDirectory("home");
		this.state.setCurrentDirectory(this.home);
		this.engine = new ScriptEngine(this.state);
	}
	
	@AfterEach
	void tearDown() {
		this.state = null; this.engine = null; this.root = null; this.home = null;
	}
	
	@Test
	void testEmptyScript() {
		String result = this.engine.execute("");
		Assertions.assertEquals("", result);
	}
	
	@Test
	void testNullScript() {
		String result = this.engine.execute(null);
		Assertions.assertEquals("", result);
	}
	
	@Test
	void testCommentOnly() {
		String result = this.engine.execute("# This is a comment");
		Assertions.assertEquals("", result);
	}
	
	@Test
	void testEchoCommand() {
		String result = this.engine.execute("echo Hello World");
		Assertions.assertEquals("Hello World", result.trim());
	}
	
	@Test
	void testVariableSet() {
		String script = "set name=John\necho Hello $name";
		String result = this.engine.execute(script);
		Assertions.assertEquals("Hello John", result.trim());
	}
	
	@Test
	void testVariableMultiple() {
		String script = "set first=Gabriel\nset last=Chandesris\necho $first $last";
		String result = this.engine.execute(script);
		Assertions.assertEquals("Gabriel Chandesris", result.trim());
	}
	
	@Test
	void testCdCommand() {
		this.home.createDirectory("documents");
		String result = this.engine.execute("cd documents");
		Assertions.assertEquals("", result);
		Assertions.assertEquals("/home/documents", this.state.getCurrentDirectory().getPath());
	}
	
	@Test
	void testPwdCommand() {
		String result = this.engine.execute("pwd");
		Assertions.assertEquals("/home", result.trim());
	}
	
	@Test
	void testLsCommand() {
		this.home.createFile("test.txt");
		String result = this.engine.execute("ls");
		Assertions.assertTrue(result.contains("test.txt"));
	}
	
	@Test
	void testScriptFromFile() {
		TerminalFile scriptFile = this.home.createFile("script.sh");
		scriptFile.setContent("echo Script executed\necho From file");
		String result = this.engine.executeFile(scriptFile);
		Assertions.assertTrue(result.contains("Script executed"));
		Assertions.assertTrue(result.contains("From file"));
	}
	
	@Test
	void testIfConditionTrue() {
		String script = "set var=test\nif [ \"$var\" = \"test\" ]\n  echo Condition true\nfi";
		String result = this.engine.execute(script);
		Assertions.assertTrue(result.contains("Condition true"));
	}
	
	@Test
	void testFileExistenceTest() {
		this.home.createFile("existing.txt");
		String script = "if [ -f \"existing.txt\" ]\n  echo File exists\nfi";
		String result = this.engine.execute(script);
		Assertions.assertTrue(result.contains("File exists"));
	}
}
