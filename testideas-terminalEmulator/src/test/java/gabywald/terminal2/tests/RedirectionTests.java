package gabywald.terminal2.tests;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import gabywald.terminal2.Redirection;

class RedirectionTest {
    @Test
    void testParseRedirectionsOutput() {
        String[] args = {"echo", "Hello", ">", "output.txt"};
        List<Redirection> redirections = Redirection.parseRedirections(args);
        Assertions.assertEquals(1, redirections.size());
        Assertions.assertEquals(Redirection.Type.OUTPUT, redirections.get(0).getType());
        Assertions.assertEquals("output.txt", redirections.get(0).getFileName());
        Assertions.assertArrayEquals(new String[]{"echo", "Hello"}, redirections.get(0).cleanedArgs);
    }

    @Test
    void testParseRedirectionsAppend() {
        String[] args = {"echo", "Hello", ">>", "output.txt"};
        List<Redirection> redirections = Redirection.parseRedirections(args);
        Assertions.assertEquals(1, redirections.size());
        Assertions.assertEquals(Redirection.Type.APPEND, redirections.get(0).getType());
        Assertions.assertEquals("output.txt", redirections.get(0).getFileName());
    }

    @Test
    void testParseRedirectionsInput() {
        String[] args = {"cat", "<", "input.txt"};
        List<Redirection> redirections = Redirection.parseRedirections(args);
        Assertions.assertEquals(1, redirections.size());
        Assertions.assertEquals(Redirection.Type.INPUT, redirections.get(0).getType());
        Assertions.assertEquals("input.txt", redirections.get(0).getFileName());
    }

    @Test
    void testParseRedirectionsMultiple() {
        String[] args = {"cat", "<", "input.txt", ">", "output.txt"};
        List<Redirection> redirections = Redirection.parseRedirections(args);
        Assertions.assertEquals(2, redirections.size());
        Assertions.assertEquals(Redirection.Type.INPUT, redirections.get(0).getType());
        Assertions.assertEquals(Redirection.Type.OUTPUT, redirections.get(1).getType());
    }

    @Test
    void testParseRedirectionsNone() {
        String[] args = {"ls", "-l"};
        List<Redirection> redirections = Redirection.parseRedirections(args);
        Assertions.assertEquals(0, redirections.size());
    }
}
