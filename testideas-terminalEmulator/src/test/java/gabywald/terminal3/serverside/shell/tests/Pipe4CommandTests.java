package gabywald.terminal3.serverside.shell.tests;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import gabywald.terminal3.serverside.shell.Pipe4Command;

/**
 * @author Gabriel Chandesris (2026)
 */
class PipeTest {
    @Test
    void testParsePipeSingle() {
        String input = "ls";
        Pipe4Command pipe = Pipe4Command.parsePipe(input);
        List<String[]> commands = pipe.getCommands();
        Assertions.assertEquals(1, commands.size());
        Assertions.assertArrayEquals(new String[]{"ls"}, commands.get(0));
    }

    @Test
    void testParsePipeDouble() {
        String input = "ls | grep txt";
        Pipe4Command pipe = Pipe4Command.parsePipe(input);
        List<String[]> commands = pipe.getCommands();
        Assertions.assertEquals(2, commands.size());
        Assertions.assertArrayEquals(new String[]{"ls"}, commands.get(0));
        Assertions.assertArrayEquals(new String[]{"grep", "txt"}, commands.get(1));
    }

    @Test
    void testParsePipeMultiple() {
        String input = "cat file.txt | grep pattern | wc";
        Pipe4Command pipe = Pipe4Command.parsePipe(input);
        List<String[]> commands = pipe.getCommands();
        Assertions.assertEquals(3, commands.size());
        Assertions.assertArrayEquals(new String[]{"cat", "file.txt"}, commands.get(0));
        Assertions.assertArrayEquals(new String[]{"grep", "pattern"}, commands.get(1));
        Assertions.assertArrayEquals(new String[]{"wc"}, commands.get(2));
    }

    @Test
    void testParsePipeWithSpaces() {
        String input = "  ls  |   grep txt  ";
        Pipe4Command pipe = Pipe4Command.parsePipe(input);
        List<String[]> commands = pipe.getCommands();
        Assertions.assertEquals(2, commands.size());
        Assertions.assertArrayEquals(new String[]{"ls"}, commands.get(0));
        Assertions.assertArrayEquals(new String[]{"grep", "txt"}, commands.get(1));
    }
}
