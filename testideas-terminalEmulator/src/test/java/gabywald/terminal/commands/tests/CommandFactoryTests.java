package gabywald.terminal.commands.tests;

import gabywald.terminal.commands.Command;
import gabywald.terminal.commands.CommandFactory;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class CommandFactoryTest {
    @Test
    void testGetCommand() {
        Command ls = CommandFactory.getCommand("ls");
        assertNotNull(ls);
        assertEquals("ls", ls.getName());
        Command cd = CommandFactory.getCommand("cd");
        assertNotNull(cd);
        assertEquals("cd", cd.getName());
    }
    
    @Test
    void testGetCommandCaseInsensitive() {
        Command ls = CommandFactory.getCommand("LS");
        assertNotNull(ls);
        assertEquals("ls", ls.getName());
    }
    
    @Test
    void testHasCommand() {
        assertTrue(CommandFactory.hasCommand("ls"));
        assertTrue(CommandFactory.hasCommand("cd"));
        assertFalse(CommandFactory.hasCommand("nonexistent"));
    }
    
    @Test
    void testGetAllCommands() {
        assertFalse(CommandFactory.getAllCommands().isEmpty());
        assertTrue(CommandFactory.getAllCommands().size() >= 10);
    }
    
    @Test
    void testGetCommandNames() {
        String[] names = CommandFactory.getCommandNames();
        assertNotNull(names);
        assertTrue(names.length >= 10);
        boolean hasLs = false, hasCd = false;
        for (String name : names) {
            if ("ls".equals(name)) hasLs = true;
            if ("cd".equals(name)) hasCd = true;
        }
        assertTrue(hasLs);
        assertTrue(hasCd);
    }
}
