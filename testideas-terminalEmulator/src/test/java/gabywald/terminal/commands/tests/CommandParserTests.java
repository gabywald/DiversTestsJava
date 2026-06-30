package gabywald.terminal.commands.tests;

import gabywald.terminal.commands.CommandParser;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class CommandParserTest {
    @Test
    void testParseEmpty() {
        String[] result = CommandParser.parse("");
        assertEquals(0, result.length);
    }
    
    @Test
    void testParseNull() {
        String[] result = CommandParser.parse(null);
        assertEquals(0, result.length);
    }
    
    @Test
    void testParseSimpleCommand() {
        String[] result = CommandParser.parse("ls");
        assertEquals(1, result.length);
        assertEquals("ls", result[0]);
    }
    
    @Test
    void testParseCommandWithArgs() {
        String[] result = CommandParser.parse("ls -l -a");
        assertEquals(3, result.length);
        assertEquals("ls", result[0]);
        assertEquals("-l", result[1]);
        assertEquals("-a", result[2]);
    }
    
    @Test
    void testParseCommandWithSpacesInArgs() {
        String[] result = CommandParser.parse("echo \"Hello World\"");
        assertEquals(2, result.length);
        assertEquals("echo", result[0]);
        assertEquals("Hello World", result[1]);
    }
    
    @Test
    void testGetCommandName() {
        assertEquals("ls", CommandParser.getCommandName("ls -l"));
        assertEquals("echo", CommandParser.getCommandName("echo Hello"));
        assertEquals("", CommandParser.getCommandName(""));
    }
    
    @Test
    void testGetArguments() {
        String[] args = CommandParser.getArguments("ls -l -a");
        assertEquals(2, args.length);
        assertEquals("-l", args[0]);
        assertEquals("-a", args[1]);
    }
    
    @Test
    void testHasArguments() {
        assertTrue(CommandParser.hasArguments("ls -l"));
        assertFalse(CommandParser.hasArguments("ls"));
    }
    
    @Test
    void testCleanArgument() {
        assertEquals("test", CommandParser.cleanArgument("\"test\""));
        assertEquals("hello world", CommandParser.cleanArgument("\"hello world\""));
        assertEquals("test", CommandParser.cleanArgument("test"));
    }
}
