package gabywald.terminal2.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gabywald.terminal2.FileSystem;
import gabywald.terminal2.commands.CommandParser;

class TerminalEmulatorIntegrationTest {
    private FileSystem fileSystem;
    private CommandParser commandParser;

    @BeforeEach
    void setUp() {
        fileSystem = new FileSystem();
        commandParser = new CommandParser(fileSystem);
    }

    @Test
    void testFullWorkflow() {
        commandParser.execute("mkdir test", "/");
        commandParser.execute("cd test", "/");
        commandParser.execute("touch file.txt", "/test");
        commandParser.execute("echo Hello > file.txt", "/test");

        String catOutput = commandParser.execute("cat file.txt", "/test");
        Assertions.assertEquals("Hello", catOutput);

        String pipeOutput = commandParser.execute("cat file.txt | grep Hello", "/test");
        Assertions.assertEquals("Hello", pipeOutput.trim());

        commandParser.execute("echo World >> file.txt", "/test");
        catOutput = commandParser.execute("cat file.txt", "/test");
        Assertions.assertEquals("Hello\nWorld", catOutput);

        commandParser.execute("cd ..", "/test");
        commandParser.execute("rm test", "/");
        Assertions.assertFalse(fileSystem.exists("test"));
    }

    @Test
    void testScriptExecution() {
        fileSystem.echo("script.txt", "echo Line1\necho Line2\necho Line3 | grep Line2");
        String output = commandParser.execute("run script.txt", "/");
        Assertions.assertTrue(output.contains("Line1"));
        Assertions.assertTrue(output.contains("Line2"));
        Assertions.assertTrue(output.contains("Line2"));
    }

    @Test
    void testComplexPipe() {
        fileSystem.echo("data.txt", "apple\nbanana\napple\ncherry");
        String output = commandParser.execute("cat data.txt | grep apple | wc", "/");
        Assertions.assertEquals("2 2 10", output.trim());
    }

    @Test
    void testRedirectionAndPipe() {
        fileSystem.echo("input.txt", "Hello\nWorld");
        String output = commandParser.execute("cat < input.txt | tr H h", "/");
        Assertions.assertEquals("hello\nWorld", output);
    }

    @Test
    void testErrorHandling() {
        String output = commandParser.execute("unknown", "/");
        Assertions.assertTrue(output.contains("Commande introuvable"));

        output = commandParser.execute("cd nonexistent", "/");
        Assertions.assertTrue(output.contains("Répertoire introuvable"));

        output = commandParser.execute("cat nonexistent.txt", "/");
        Assertions.assertTrue(output.contains("Fichier introuvable"));

        output = commandParser.execute("cat < nonexistent.txt", "/");
        Assertions.assertTrue(output.contains("Fichier introuvable pour l'entrée"));
    }
}