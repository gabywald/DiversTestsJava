package gabywald.terminal3.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gabywald.terminal3.serverside.filesystem.TerminalState;
import gabywald.terminal3.serverside.shell.CommandParser;

/**
 * @author Gabriel Chandesris (2026)
 */
class TerminalEmulatorIntegrationTest {
	private TerminalState state = null;

	@BeforeEach
	void setUp() { this.state = new TerminalState(); }

	@Test
	void testFullWorkflow() {
		Assertions.assertEquals("", CommandParser.execute(this.state, "mkdir test") );
		Assertions.assertEquals("", CommandParser.execute(this.state, "cd test") );
		Assertions.assertEquals("", CommandParser.execute(this.state, "touch file.txt") );
		Assertions.assertEquals("", CommandParser.execute(this.state, "echo Hello > file.txt") );

		Assertions.assertEquals("Hello", CommandParser.execute(this.state, "cat file.txt") );

		Assertions.assertEquals("Hello", CommandParser.execute(this.state, "cat file.txt | grep Hello") );

		Assertions.assertEquals("", CommandParser.execute(this.state, "echo World >> file.txt") );
		Assertions.assertEquals("Hello\nWorld", CommandParser.execute(this.state, "cat file.txt") );

		Assertions.assertEquals("", CommandParser.execute(this.state, "cd ..") );
		Assertions.assertEquals("rm: failed to remove 'test': Directory not empty", CommandParser.execute(this.state, "rm test") );
		Assertions.assertNotNull(this.state.getCurrentDirectory().getChild("test"));
		Assertions.assertTrue(this.state.getCurrentDirectory().getChild("test").isDirectory());
		Assertions.assertFalse(this.state.getCurrentDirectory().getChild("test").isFile());
		
		Assertions.assertEquals("", CommandParser.execute(this.state, "cd test") );
		Assertions.assertEquals("", CommandParser.execute(this.state, "rm file.txt") );
		Assertions.assertNull(this.state.getCurrentDirectory().getChild("file.txt"));
		
		Assertions.assertEquals("", CommandParser.execute(this.state, "cd ..") );
		Assertions.assertEquals("", CommandParser.execute(this.state, "rm test") );
		Assertions.assertNull(this.state.getCurrentDirectory().getChild("test"));
	}

//	@Test
//	void testScriptExecution() {
//		this.fileSystem.echo("script.txt", "echo Line1\necho Line2\necho Line3 | grep Line2");
//		Assertions.assertEquals("", CommandParser.execute(this.state, "run script.txt") );
//		Assertions.assertTrue(output.contains("Line1"));
//		Assertions.assertTrue(output.contains("Line2"));
//		Assertions.assertTrue(output.contains("Line2"));
//	}

	@Test
	void testComplexPipe() {
		Assertions.assertEquals("", CommandParser.execute(this.state, "touch data.txt") );
		// Assertions.assertEquals("", CommandParser.execute(this.state, "echo apple\nbanana\napple\ncherry > data.txt") );
		Assertions.assertEquals("", CommandParser.execute(this.state, "echo apple > data.txt") );
		Assertions.assertEquals("", CommandParser.execute(this.state, "echo banana >> data.txt") );
		Assertions.assertEquals("", CommandParser.execute(this.state, "echo apple >> data.txt") );
		Assertions.assertEquals("", CommandParser.execute(this.state, "echo cherry >> data.txt") );
		Assertions.assertEquals("apple\nbanana\napple\ncherry", CommandParser.execute(this.state, "cat data.txt") );
		Assertions.assertEquals("apple\napple", CommandParser.execute(this.state, "cat data.txt | grep apple") );
		Assertions.assertEquals("2 2 11", CommandParser.execute(this.state, "cat data.txt | grep apple | wc") );
	}

	@Test
	void testRedirectionAndPipe() {
		Assertions.assertEquals("", CommandParser.execute(this.state, "touch input.txt") );
		Assertions.assertEquals("", CommandParser.execute(this.state, "echo Hello\\nWorld > input.txt") );
		Assertions.assertEquals("hello\\nWorld", CommandParser.execute(this.state, "cat input.txt | tr H h") );
	}

	@Test
	void testErrorHandling() {
		Assertions.assertEquals("UNKNOWN COMMAND: unknown", CommandParser.execute(this.state, "unknown") );
		Assertions.assertEquals("cd: no such directory: nonexistent", CommandParser.execute(this.state, "cd nonexistent") );
		Assertions.assertEquals("cat: No such file: nonexistent.txt", CommandParser.execute(this.state, "cat nonexistent.txt") );
		Assertions.assertEquals("Unknown File for input: nonexistent.txt", CommandParser.execute(this.state, "cat < nonexistent.txt") );
	}
}