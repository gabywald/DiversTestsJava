package gabywald.terminal2.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gabywald.terminal2.FileSystem;
import gabywald.terminal2.commands.CommandParser;

/**
 * @author Gabriel Chandesris (2026)
 */
class CommandParserTest {
    private FileSystem fileSystem;
    private CommandParser commandParser;

    @BeforeEach
    void setUp() {
    	this.fileSystem = new FileSystem();
    	this.commandParser = new CommandParser(this.fileSystem);
    }

    @Test
    void testExecuteLs() {
    	this.fileSystem.touch("file.txt");
        String output = commandParser.execute("ls", "/");
        Assertions.assertTrue(output.contains("file.txt"));
    }

    @Test
    void testExecuteCd() {
    	Assertions.assertTrue(this.fileSystem.mkdir("test"));
    	Assertions.assertEquals("", this.commandParser.execute("cd test", "/"));
        Assertions.assertEquals("/test", this.fileSystem.getCurrentDirectory());
    }

    @Test
    void testExecuteCat() {
    	Assertions.assertTrue(this.fileSystem.touch("file.txt"));
    	Assertions.assertTrue(this.fileSystem.echo("file.txt", "Hello"));
        String output = this.commandParser.execute("cat file.txt", "/");
        Assertions.assertEquals("Hello", output);
    }

    @Test
    void testExecuteEcho() {
        String output = this.commandParser.execute("echo Hello", "/");
        Assertions.assertEquals("Hello", output);
    }

    @Test
    void testExecuteMkdir() {
        String output = this.commandParser.execute("mkdir test", "/");
        Assertions.assertEquals("", output);
        Assertions.assertTrue(fileSystem.exists("test"));
    }

    @Test
    void testExecuteTouch() {
        String output = this.commandParser.execute("touch file.txt", "/");
        Assertions.assertEquals("", output);
        Assertions.assertTrue(fileSystem.exists("file.txt"));
    }

    @Test
    void testExecuteRm() {
        fileSystem.touch("file.txt");
        String output = this.commandParser.execute("rm file.txt", "/");
        Assertions.assertEquals("", output);
        Assertions.assertFalse(this.fileSystem.exists("file.txt"));
    }

    @Test
    void testExecutePwd() {
        String output = this.commandParser.execute("pwd", "/");
        Assertions.assertEquals("/", output);
    }

    @Test
    void testExecuteHelp() {
        String output = this.commandParser.execute("help", "/");
        Assertions.assertTrue(output.contains("ls"));
        Assertions.assertTrue(output.contains("cd"));
    }
    
    @Test
    void testExecuteGrep() {
    	Assertions.assertTrue(this.fileSystem.echo("test.txt", "line1\nline2\nline3") );
    	Assertions.assertEquals("line1\nline2\nline3", this.commandParser.execute("cat test.txt", "/") );
    	String output = this.commandParser.execute("grep line2 test.txt", "/");
        Assertions.assertEquals("line2", output.trim());
    }

    @Test
    void testExecuteUnknownCommand() {
        String output = this.commandParser.execute("unknown", "/");
        Assertions.assertTrue(output.contains("Commande introuvable"));
    }

    @Test
    void testRedirectionOutput() {
    	Assertions.assertTrue(this.fileSystem.touch("output.txt"));
        String output = this.commandParser.execute("echo Hello > output.txt", "/");
        Assertions.assertEquals("", output);
        Assertions.assertEquals("Hello", this.fileSystem.cat("output.txt"));
    }

    @Test
    void testRedirectionAppend() {
    	Assertions.assertTrue(this.fileSystem.echo("output.txt", "Line1\n"));
        String output = this.commandParser.execute("echo Line2 >> output.txt", "/");
        Assertions.assertEquals("", output);
        Assertions.assertEquals("Line1\nLine2", this.fileSystem.cat("output.txt"));
    }

    @Test
    void testRedirectionInput() {
    	Assertions.assertTrue(this.fileSystem.echo("input.txt", "Hello from file"));
        String output = this.commandParser.execute("cat < input.txt", "/");
        Assertions.assertEquals("Hello from file", output);
    }

    @Test
    void testPipe() {
    	Assertions.assertTrue(this.fileSystem.echo("test.txt", "line1\nline2\nline3"));
        String output = this.commandParser.execute("cat test.txt | grep line2", "/");
        Assertions.assertEquals("line2", output.trim());
    }

    @Test
    void testMultiplePipes() {
    	Assertions.assertTrue(this.fileSystem.echo("test.txt", "apple\nbanana\napple"));
        String output = this.commandParser.execute("cat test.txt | grep apple | wc", "/");
        Assertions.assertEquals("2 2 12", output.trim()); // NOTE count '\n'
    }
}
