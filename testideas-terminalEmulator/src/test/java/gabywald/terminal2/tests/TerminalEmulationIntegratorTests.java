package gabywald.terminal2.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gabywald.terminal2.FileSystem;
import gabywald.terminal2.commands.CommandParser;

/**
 * @author Gabriel Chandesris (2026)
 */
class TerminalEmulatorIntegrationTest {
    private FileSystem fileSystem;
    private CommandParser commandParser;

    @BeforeEach
    void setUp() {
        this.fileSystem = new FileSystem();
        this.commandParser = new CommandParser(this.fileSystem);
    }

    @Test
    void testFullWorkflow() {
    	this.commandParser.execute("mkdir test", "/");
    	this.commandParser.execute("cd test", "/");
    	this.commandParser.execute("touch file.txt", "/test");
    	this.commandParser.execute("echo Hello > file.txt", "/test");

        String catOutput = this.commandParser.execute("cat file.txt", "/test");
        Assertions.assertEquals("Hello", catOutput);

        String pipeOutput = this.commandParser.execute("cat file.txt | grep Hello", "/test");
        Assertions.assertEquals("Hello", pipeOutput.trim());

        this.commandParser.execute("echo World >> file.txt", "/test");
        catOutput = this.commandParser.execute("cat file.txt", "/test");
        Assertions.assertEquals("HelloWorld", catOutput); // NOTE : no '\n'between the two (2) 'echo'

        this.commandParser.execute("cd ..", "/test");
        this.commandParser.execute("rm test", "/");
        Assertions.assertTrue(this.fileSystem.exists("test")); // NOTE : not removed because not empty
        
        this.commandParser.execute("cd test", "/");
        this.commandParser.execute("rm file.txt", "/");
        Assertions.assertFalse(this.fileSystem.exists("file.txt"));
        this.commandParser.execute("cd ..", "/test");
        this.commandParser.execute("rm test", "/");
        Assertions.assertFalse(this.fileSystem.exists("test"));
    }

    @Test
    void testScriptExecution() {
    	this.fileSystem.echo("script.txt", "echo Line1\necho Line2\necho Line3 | grep Line2");
        String output = this.commandParser.execute("run script.txt", "/");
        Assertions.assertTrue(output.contains("Line1"));
        Assertions.assertTrue(output.contains("Line2"));
        Assertions.assertTrue(output.contains("Line2"));
    }

    @Test
    void testComplexPipe() {
    	this.fileSystem.echo("data.txt", "apple\nbanana\napple\ncherry");
        String output = this.commandParser.execute("cat data.txt | grep apple | wc", "/");
        Assertions.assertEquals("2 2 12", output.trim()); // NOTE count '\n'
    }

    @Test
    void testRedirectionAndPipe() {
    	this.fileSystem.echo("input.txt", "Hello\nWorld");
        String output = this.commandParser.execute("cat input.txt | tr H h", "/");
        Assertions.assertEquals("hello\nWorld", output);
    }

    @Test
    void testErrorHandling() {
        String output = this.commandParser.execute("unknown", "/");
        Assertions.assertTrue(output.contains("Commande introuvable"));

        output = this.commandParser.execute("cd nonexistent", "/");
        Assertions.assertTrue(output.contains("Répertoire introuvable"));

        output = this.commandParser.execute("cat nonexistent.txt", "/");
        Assertions.assertTrue(output.contains("Fichier introuvable"));

        output = this.commandParser.execute("cat < nonexistent.txt", "/");
        Assertions.assertTrue(output.contains("Fichier introuvable pour l'entrée"));
    }
}