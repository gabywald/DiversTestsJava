package gabywald.terminal3.serverside.shell.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gabywald.terminal3.serverside.filesystem.TerminalState;
import gabywald.terminal3.serverside.shell.CommandParser;

/**
 * @author Gabriel Chandesris (2026)
 */
class CommandParserTest {
	private TerminalState state = null;

	@BeforeEach
	void setUp() { this.state = new TerminalState(); }
	
    @Test
    void testParseEmpty() {
        String[] result = CommandParser.parse("");
        Assertions.assertEquals(0, result.length);
    }
    
    @Test
    void testParseNull() {
        String[] result = CommandParser.parse(null);
        Assertions.assertEquals(0, result.length);
    }
    
    @Test
    void testParseSimpleCommand() {
        String[] result = CommandParser.parse("ls");
        Assertions.assertEquals(1, result.length);
        Assertions.assertEquals("ls", result[0]);
    }
    
    @Test
    void testParseCommandWithArgs() {
        String[] result = CommandParser.parse("ls -l -a");
        Assertions.assertEquals(3, result.length);
        Assertions.assertEquals("ls", result[0]);
        Assertions.assertEquals("-l", result[1]);
        Assertions.assertEquals("-a", result[2]);
    }
    
    @Test
    void testParseCommandWithSpacesInArgs() {
        String[] result = CommandParser.parse("echo \"Hello World\"");
        Assertions.assertEquals(2, result.length);
        Assertions.assertEquals("echo", result[0]);
        Assertions.assertEquals("Hello World", result[1]);
    }
    
    @Test
    void testGetCommandName() {
    	Assertions.assertEquals("ls", CommandParser.getCommandName("ls -l"));
    	Assertions.assertEquals("echo", CommandParser.getCommandName("echo Hello"));
    	Assertions.assertEquals("", CommandParser.getCommandName(""));
    }
    
    @Test
    void testGetArguments() {
        String[] args = CommandParser.getArguments("ls -l -a");
        Assertions.assertEquals(2, args.length);
        Assertions.assertEquals("-l", args[0]);
        Assertions.assertEquals("-a", args[1]);
    }
    
    @Test
    void testHasArguments() {
    	Assertions.assertTrue(CommandParser.hasArguments("ls -l"));
    	Assertions.assertFalse(CommandParser.hasArguments("ls"));
    }
    
    @Test
    void testCleanArgument() {
    	Assertions.assertEquals("test", CommandParser.cleanArgument("\"test\""));
    	Assertions.assertEquals("hello world", CommandParser.cleanArgument("\"hello world\""));
    	Assertions.assertEquals("test", CommandParser.cleanArgument("test"));
    }

	@Test
	void testExecuteLs() {
		Assertions.assertEquals("", CommandParser.execute(this.state, "touch file.txt") );
		Assertions.assertEquals("file.txt\n", CommandParser.execute(this.state, "ls"));
	}

	@Test
	void testExecuteCd() {
		Assertions.assertEquals("", CommandParser.execute(this.state, "mkdir test") );
		Assertions.assertEquals("", CommandParser.execute(this.state, "cd test") );
		Assertions.assertEquals("/test", this.state.getCurrentDirectory().getPath());
	}

	@Test
	void testExecuteCat01() {
		Assertions.assertEquals("", CommandParser.execute(this.state, "touch file.txt") );
		Assertions.assertEquals("", CommandParser.execute(this.state, "cat file.txt") );
		Assertions.assertEquals("", CommandParser.execute(this.state, "echo Hello >> file.txt") );
		Assertions.assertEquals("\nHello", CommandParser.execute(this.state, "cat file.txt") );
	}
	
	@Test
	void testExecuteCat02() {
		Assertions.assertEquals("", CommandParser.execute(this.state, "touch file.txt") );
		Assertions.assertEquals("", CommandParser.execute(this.state, "cat file.txt") );
		Assertions.assertEquals("", CommandParser.execute(this.state, "echo Hello > file.txt") );
		Assertions.assertEquals("Hello", CommandParser.execute(this.state, "cat file.txt") );
	}

	@Test
	void testExecuteEcho() {
		Assertions.assertEquals("Hello", CommandParser.execute(this.state, "echo Hello") );
	}

	@Test
	void testExecuteMkdir() {
		Assertions.assertEquals("", CommandParser.execute(this.state, "mkdir test") );
		Assertions.assertNotNull(this.state.getCurrentDirectory().getChild("test"));
		Assertions.assertTrue(this.state.getCurrentDirectory().getChild("test").isDirectory());
		Assertions.assertFalse(this.state.getCurrentDirectory().getChild("test").isFile());
	}

	@Test
	void testExecuteTouch() {
		Assertions.assertEquals("", CommandParser.execute(this.state, "touch file.txt") );
		Assertions.assertNotNull(this.state.getCurrentDirectory().getChild("file.txt"));
		Assertions.assertTrue(this.state.getCurrentDirectory().getChild("file.txt").isFile());
		Assertions.assertFalse(this.state.getCurrentDirectory().getChild("file.txt").isDirectory());
	}

	@Test
	void testExecuteRm() {
		Assertions.assertEquals("", CommandParser.execute(this.state, "touch file.txt") );
		Assertions.assertEquals("", CommandParser.execute(this.state, "rm file.txt") );
		Assertions.assertNull(this.state.getCurrentDirectory().getChild("file.txt"));
	}

	@Test
	void testExecutePwd() {
		Assertions.assertEquals("/", CommandParser.execute(this.state, "pwd") );
	}

	@Test
	void testExecuteHelp() {
		String output = CommandParser.execute(this.state, "help");
		Assertions.assertTrue(output.contains("ls"));
		Assertions.assertTrue(output.contains("cd"));
		Assertions.assertTrue(output.contains("help"));
		Assertions.assertTrue(output.contains("cat"));
		Assertions.assertTrue(output.contains("pwd"));
	}
	
	@Test
	void testExecuteGrep() {
		Assertions.assertEquals("", CommandParser.execute(this.state, "touch test.txt") );
		// Assertions.assertEquals("", CommandParser.execute(this.state, "echo line1\\nline2\\nline3 > test.txt") );
		Assertions.assertEquals("", CommandParser.execute(this.state, "echo line1 > test.txt") );
		Assertions.assertEquals("", CommandParser.execute(this.state, "echo line2 >> test.txt") );
		Assertions.assertEquals("", CommandParser.execute(this.state, "echo line3 >> test.txt") );
		Assertions.assertEquals("line1\nline2\nline3", CommandParser.execute(this.state, "cat test.txt") );
		Assertions.assertEquals("line2", CommandParser.execute(this.state, "grep line2 test.txt") );
	}

	@Test
	void testExecuteUnknownCommand() {
		Assertions.assertEquals("UNKNOWN COMMAND: unknown", CommandParser.execute(this.state, "unknown") );
	}

	@Test
	void testRedirectionOutput() {
		Assertions.assertEquals("", CommandParser.execute(this.state, "touch output.txt") );
		Assertions.assertEquals("", CommandParser.execute(this.state, "echo Hello > output.txt") );
		Assertions.assertEquals("Hello", CommandParser.execute(this.state, "cat output.txt") );
	}

	@Test
	void testRedirectionAppend() {
		Assertions.assertEquals("", CommandParser.execute(this.state, "touch output.txt") );
		Assertions.assertEquals("", CommandParser.execute(this.state, "echo Line1 > output.txt") );
		Assertions.assertEquals("Line1", CommandParser.execute(this.state, "cat output.txt") );
		Assertions.assertEquals("", CommandParser.execute(this.state, "echo Line2 >> output.txt") );
		Assertions.assertEquals("Line1\nLine2", CommandParser.execute(this.state, "cat output.txt") );
	}
	
	@Test
	void testRedirectionInput() {
		Assertions.assertEquals("", CommandParser.execute(this.state, "touch input.txt") );
		Assertions.assertEquals("", CommandParser.execute(this.state, "echo Hello from file > input.txt") );
		Assertions.assertEquals("Hello from file", CommandParser.execute(this.state, "cat < input.txt") );
	}

	@Test
	void testPipe() {
		Assertions.assertEquals("", CommandParser.execute(this.state, "touch test.txt") );
		// Assertions.assertEquals("", CommandParser.execute(this.state, "echo line1\\nline2\\nline3 > test.txt") );
		Assertions.assertEquals("", CommandParser.execute(this.state, "echo line1 > test.txt") );
		Assertions.assertEquals("", CommandParser.execute(this.state, "echo line2 >> test.txt") );
		Assertions.assertEquals("", CommandParser.execute(this.state, "echo line3 >> test.txt") );
		Assertions.assertEquals("line1\nline2\nline3", CommandParser.execute(this.state, "cat test.txt") );
		Assertions.assertEquals("line2", CommandParser.execute(this.state, "cat test.txt | grep line2") );
	}
	
	@Test
	void testPipeNext() {
		Assertions.assertEquals("", CommandParser.execute(this.state, "touch test.txt") );
		Assertions.assertEquals("", CommandParser.execute(this.state, "echo line1/nline2/nline3 > test.txt") );
		Assertions.assertEquals("line1\nline2\nline3", CommandParser.execute(this.state, "cat test.txt") );
		Assertions.assertEquals("line2", CommandParser.execute(this.state, "cat test.txt | grep line2") );
	}

	@Test
	void testMultiplePipes() {
		Assertions.assertEquals("", CommandParser.execute(this.state, "touch test.txt") );
		// Assertions.assertEquals("", CommandParser.execute(this.state, "echo apple\\nbanana\\napple > test.txt") );
		Assertions.assertEquals("", CommandParser.execute(this.state, "echo apple > test.txt") );
		Assertions.assertEquals("", CommandParser.execute(this.state, "echo banana >> test.txt") );
		Assertions.assertEquals("", CommandParser.execute(this.state, "echo apple >> test.txt") );
		Assertions.assertEquals("apple\nbanana\napple", CommandParser.execute(this.state, "cat test.txt") );
		Assertions.assertEquals("apple\napple", CommandParser.execute(this.state, "cat test.txt | grep apple") );
		Assertions.assertEquals("2 2 11", CommandParser.execute(this.state, "cat test.txt | grep apple | wc") );
	}
	
	@Test
	void testMultiplePipesNext() {
		Assertions.assertEquals("", CommandParser.execute(this.state, "touch test.txt") );
		Assertions.assertEquals("", CommandParser.execute(this.state, "echo apple/nbanana/napple > test.txt") );
		Assertions.assertEquals("apple\nbanana\napple", CommandParser.execute(this.state, "cat test.txt") );
		Assertions.assertEquals("apple\napple", CommandParser.execute(this.state, "cat test.txt | grep apple") );
		Assertions.assertEquals("2 2 11", CommandParser.execute(this.state, "cat test.txt | grep apple | wc") );
	}
}
