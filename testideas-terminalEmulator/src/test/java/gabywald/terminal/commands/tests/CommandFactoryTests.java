package gabywald.terminal.commands.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import gabywald.terminal.commands.Command;
import gabywald.terminal.commands.CommandFactory;

/**
 * @author Gabriel Chandesris (2026)
 */
class CommandFactoryTest {
    @Test
    void testGetCommand() {
        Command ls = CommandFactory.getCommand("ls");
        Assertions.assertNotNull(ls);
        Assertions.assertEquals("ls", ls.getName());
        Command cd = CommandFactory.getCommand("cd");
        Assertions.assertNotNull(cd);
        Assertions.assertEquals("cd", cd.getName());
    }
    
    @Test
    void testGetCommandCaseInsensitive() {
        Command ls = CommandFactory.getCommand("LS");
        Assertions.assertNotNull(ls);
        Assertions.assertEquals("ls", ls.getName());
    }
    
    @Test
    void testHasCommand() {
    	Assertions.assertTrue(CommandFactory.hasCommand("ls"));
    	Assertions.assertTrue(CommandFactory.hasCommand("cd"));
    	Assertions.assertFalse(CommandFactory.hasCommand("nonexistent"));
    }
    
    @Test
    void testGetAllCommands() {
    	Assertions.assertFalse(CommandFactory.getAllCommands().isEmpty());
    	Assertions.assertTrue(CommandFactory.getAllCommands().size() >= 10);
    }
    
    @Test
    void testGetCommandNames() {
        String[] names = CommandFactory.getCommandNames();
        Assertions.assertNotNull(names);
        Assertions.assertTrue(names.length >= 10);
        boolean hasLs = false, hasCd = false;
        for (String name : names) {
            if ("ls".equals(name)) hasLs = true;
            if ("cd".equals(name)) hasCd = true;
        }
        Assertions.assertTrue(hasLs);
        Assertions.assertTrue(hasCd);
    }
}
