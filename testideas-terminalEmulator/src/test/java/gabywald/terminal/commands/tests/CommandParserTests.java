package gabywald.terminal.commands.tests;

import gabywald.terminal.commands.CommandParser;
import org.junit.jupiter.api.*;

/**
 * @author Gabriel Chandesris (2026)
 */
class CommandParserTest {
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
}
