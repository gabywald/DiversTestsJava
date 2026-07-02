package gabywald.terminal2.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gabywald.terminal2.FileSystem;
import gabywald.terminal2.commands.CommandParser;

class CommandParserTest {
    private FileSystem fileSystem;
    private CommandParser commandParser;

    @BeforeEach
    void setUp() {
        fileSystem = new FileSystem();
        commandParser = new CommandParser(fileSystem);
    }

    @Test
    void testExecuteLs() {
        fileSystem.touch("file.txt");
        String output = commandParser.execute("ls", "/");
        Assertions.assertTrue(output.contains("file.txt"));
    }

    @Test
    void testExecuteCd() {
        fileSystem.mkdir("test");
        commandParser.execute("cd test", "/");
        Assertions.assertEquals("/test", fileSystem.getCurrentDirectory());
    }

    @Test
    void testExecuteCat() {
        fileSystem.touch("file.txt");
        fileSystem.echo("file.txt", "Hello");
        String output = commandParser.execute("cat file.txt", "/");
        Assertions.assertEquals("Hello", output);
    }

    @Test
    void testExecuteEcho() {
        String output = commandParser.execute("echo Hello", "/");
        Assertions.assertEquals("Hello", output);
    }

    @Test
    void testExecuteMkdir() {
        String output = commandParser.execute("mkdir test", "/");
        Assertions.assertEquals("", output);
        Assertions.assertTrue(fileSystem.exists("test"));
    }

    @Test
    void testExecuteTouch() {
        String output = commandParser.execute("touch file.txt", "/");
        Assertions.assertEquals("", output);
        Assertions.assertTrue(fileSystem.exists("file.txt"));
    }

    @Test
    void testExecuteRm() {
        fileSystem.touch("file.txt");
        String output = commandParser.execute("rm file.txt", "/");
        Assertions.assertEquals("", output);
        Assertions.assertFalse(fileSystem.exists("file.txt"));
    }

    @Test
    void testExecutePwd() {
        String output = commandParser.execute("pwd", "/");
        Assertions.assertEquals("/", output);
    }

    @Test
    void testExecuteHelp() {
        String output = commandParser.execute("help", "/");
        Assertions.assertTrue(output.contains("ls"));
        Assertions.assertTrue(output.contains("cd"));
    }

    @Test
    void testExecuteUnknownCommand() {
        String output = commandParser.execute("unknown", "/");
        Assertions.assertTrue(output.contains("Commande introuvable"));
    }

    @Test
    void testRedirectionOutput() {
        fileSystem.touch("output.txt");
        String output = commandParser.execute("echo Hello > output.txt", "/");
        Assertions.assertEquals("", output);
        Assertions.assertEquals("Hello", fileSystem.cat("output.txt"));
    }

    @Test
    void testRedirectionAppend() {
        fileSystem.echo("output.txt", "Line1\n");
        String output = commandParser.execute("echo Line2 >> output.txt", "/");
        Assertions.assertEquals("", output);
        Assertions.assertEquals("Line1\nLine2", fileSystem.cat("output.txt"));
    }

    @Test
    void testRedirectionInput() {
        fileSystem.echo("input.txt", "Hello from file");
        String output = commandParser.execute("cat < input.txt", "/");
        Assertions.assertEquals("Hello from file", output);
    }

    @Test
    void testPipe() {
        fileSystem.echo("test.txt", "line1\nline2\nline3");
        String output = commandParser.execute("cat test.txt | grep line2", "/");
        Assertions.assertEquals("line2", output.trim());
    }

    @Test
    void testMultiplePipes() {
        fileSystem.echo("test.txt", "apple\nbanana\napple");
        String output = commandParser.execute("cat test.txt | grep apple | wc", "/");
        Assertions.assertEquals("2 2 10", output.trim());
    }
}
