package gabywald.terminal.tests;

import gabywald.terminal.TerminalState;
import gabywald.terminal.commands.*;
import gabywald.terminal.filesystem.Directory;
import gabywald.terminal.filesystem.TerminalFile;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the complete terminal emulator
 */
class IntegrationTest {
    private TerminalState state;
    private Directory root;
    private Directory home;
    
    @BeforeEach
    void setUp() {
        state = new TerminalState();
        root = state.getRootDirectory();
        home = root.createDirectory("home");
        state.setCurrentDirectory(home);
        
        Directory docs = home.createDirectory("documents");
        Directory images = home.createDirectory("images");
        
        TerminalFile readme = home.createFile("README.md");
        readme.setContent("# Welcome to the Terminal Emulator");
        
        TerminalFile file1 = docs.createFile("file1.txt");
        file1.setContent("Content of file 1");
        
        TerminalFile file2 = docs.createFile("file2.txt");
        file2.setContent("Content of file 2");
    }
    
    @AfterEach
    void tearDown() {
        state = null; root = null; home = null;
    }
    
    @Test
    void testCompleteWorkflow() {
        Command ls = CommandFactory.getCommand("ls");
        String result = ls.execute(state, new String[0]);
        assertTrue(result.contains("README.md"));
        assertTrue(result.contains("documents"));
        assertTrue(result.contains("images"));
        
        Command cd = CommandFactory.getCommand("cd");
        result = cd.execute(state, new String[]{"documents"});
        assertEquals("", result);
        assertEquals("/home/documents", state.getCurrentDirectory().getPath());
        
        result = ls.execute(state, new String[0]);
        assertTrue(result.contains("file1.txt"));
        assertTrue(result.contains("file2.txt"));
        
        Command cat = CommandFactory.getCommand("cat");
        result = cat.execute(state, new String[]{"file1.txt"});
        assertEquals("Content of file 1", result);
        
        result = cd.execute(state, new String[]{".."});
        assertEquals("", result);
        assertEquals("/home", state.getCurrentDirectory().getPath());
    }
    
    @Test
    void testFileOperationsWorkflow() {
        Command touch = CommandFactory.getCommand("touch");
        String result = touch.execute(state, new String[]{"newfile.txt"});
        assertEquals("", result);
        TerminalFile newFile = (TerminalFile) home.getChild("newfile.txt");
        assertNotNull(newFile);
        
        Command cp = CommandFactory.getCommand("cp");
        result = cp.execute(state, new String[]{"newfile.txt", "newfile_copy.txt"});
        assertEquals("", result);
        TerminalFile copy = (TerminalFile) home.getChild("newfile_copy.txt");
        assertNotNull(copy);
        
        Command mv = CommandFactory.getCommand("mv");
        result = mv.execute(state, new String[]{"newfile.txt", "moved.txt"});
        assertEquals("", result);
        assertNull(home.getChild("newfile.txt"));
        assertNotNull(home.getChild("moved.txt"));
        
        Command rm = CommandFactory.getCommand("rm");
        result = rm.execute(state, new String[]{"moved.txt"});
        assertEquals("", result);
        assertNull(home.getChild("moved.txt"));
    }
    
    @Test
    void testDirectoryOperationsWorkflow() {
        Command mkdir = CommandFactory.getCommand("mkdir");
        String result = mkdir.execute(state, new String[]{"newdir"});
        assertEquals("", result);
        Directory newDir = (Directory) home.getChild("newdir");
        assertNotNull(newDir);
        
        Command cd = CommandFactory.getCommand("cd");
        result = cd.execute(state, new String[]{"newdir"});
        assertEquals("", result);
        assertEquals("/home/newdir", state.getCurrentDirectory().getPath());
        
        Command touch = CommandFactory.getCommand("touch");
        result = touch.execute(state, new String[]{"test.txt"});
        assertEquals("", result);
        
        result = cd.execute(state, new String[]{".."});
        assertEquals("", result);
        assertEquals("/home", state.getCurrentDirectory().getPath());
        
        Command rmdir = CommandFactory.getCommand("rmdir");
        result = rmdir.execute(state, new String[]{"newdir"});
        assertEquals("", result);
        assertNull(home.getChild("newdir"));
    }
    
    @Test
    void testCommandHistoryIntegration() {
        Command ls = CommandFactory.getCommand("ls");
        ls.execute(state, new String[0]);
        Command cd = CommandFactory.getCommand("cd");
        cd.execute(state, new String[]{"documents"});
        Command pwd = CommandFactory.getCommand("pwd");
        pwd.execute(state, new String[0]);
        
        assertEquals(3, state.getCommandHistory().size());
        assertEquals("ls", state.getCommandHistory().get(0));
        assertEquals("cd documents", state.getCommandHistory().get(1));
        assertEquals("pwd", state.getCommandHistory().get(2));
        
        state.resetHistoryIndex();
        String prev = state.getPreviousCommand();
        assertEquals("pwd", prev);
        prev = state.getPreviousCommand();
        assertEquals("cd documents", prev);
    }
    
    @Test
    void testErrorHandling() {
        Command ls = CommandFactory.getCommand("ls");
        String result = ls.execute(state, new String[]{"nonexistent"});
        assertTrue(result.contains("No such file or directory"));
        
        Command cd = CommandFactory.getCommand("cd");
        result = cd.execute(state, new String[]{"nonexistent"});
        assertTrue(result.contains("no such file or directory"));
        
        Command rm = CommandFactory.getCommand("rm");
        result = rm.execute(state, new String[]{"nonexistent.txt"});
        assertTrue(result.contains("No such file or directory"));
    }
    
    @Test
    void testPathResolution() {
        Command cd = CommandFactory.getCommand("cd");
        String result = cd.execute(state, new String[]{"/home/documents"});
        assertEquals("", result);
        assertEquals("/home/documents", state.getCurrentDirectory().getPath());
        
        result = cd.execute(state, new String[]{".."});
        assertEquals("", result);
        assertEquals("/home", state.getCurrentDirectory().getPath());
        
        result = cd.execute(state, new String[]{"documents/../images"});
        assertEquals("", result);
        assertEquals("/home/images", state.getCurrentDirectory().getPath());
    }
}
