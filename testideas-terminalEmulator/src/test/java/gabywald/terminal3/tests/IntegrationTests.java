package gabywald.terminal3.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gabywald.terminal3.clientside.gui.TerminalHistory;
import gabywald.terminal3.serverside.filesystem.TerminalDirectory;
import gabywald.terminal3.serverside.filesystem.TerminalFile;
import gabywald.terminal3.serverside.filesystem.TerminalState;
import gabywald.terminal3.serverside.shell.CommandFactory;
import gabywald.terminal3.serverside.shell.ICommand;

/**
 * Integration tests for the complete terminal emulator
 * @author Gabriel Chandesris (2026)
 */
class IntegrationTest {
    private TerminalState state;
    private TerminalHistory history;
    private TerminalDirectory root;
    private TerminalDirectory home;
    
    @BeforeEach
    void setUp() {
        this.state = new TerminalState();
        this.history = new TerminalHistory();
        this.root = this.state.getRootDirectory();
        this.home = root.createDirectory("home");
        this.state.setCurrentDirectory(this.home);
        
        TerminalDirectory docs = this.home.createDirectory("documents");
        TerminalDirectory images = this.home.createDirectory("images");
        Assertions.assertNotNull(images);
        
        TerminalFile readme = this.home.createFile("README.md");
        readme.setContent("# Welcome to the Terminal Emulator");
        
        TerminalFile file1 = docs.createFile("file1.txt");
        file1.setContent("Content of file 1");
        
        TerminalFile file2 = docs.createFile("file2.txt");
        file2.setContent("Content of file 2");
    }
    
    @AfterEach
    void tearDown() {
    	this.state = null; this.root = null; this.home = null;
    }
    
    @Test
    void testCompleteWorkflow() {
        ICommand ls = CommandFactory.getCommand("ls");
        String result = ls.execute(this.state, new String[0]);
        Assertions.assertTrue(result.contains("README.md"));
        Assertions.assertTrue(result.contains("documents"));
        Assertions.assertTrue(result.contains("images"));
        
        ICommand cd = CommandFactory.getCommand("cd");
        result = cd.execute(this.state, new String[]{"documents"});
        Assertions.assertEquals("", result);
        Assertions.assertEquals("/home/documents", this.state.getCurrentDirectory().getPath());
        
        result = ls.execute(this.state, new String[0]);
        Assertions.assertTrue(result.contains("file1.txt"));
        Assertions.assertTrue(result.contains("file2.txt"));
        
        ICommand cat = CommandFactory.getCommand("cat");
        result = cat.execute(this.state, new String[]{"file1.txt"});
        Assertions.assertEquals("Content of file 1", result);
        
        result = cd.execute(this.state, new String[]{".."});
        Assertions.assertEquals("", result);
        Assertions.assertEquals("/home", this.state.getCurrentDirectory().getPath());
    }
    
    @Test
    void testFileOperationsWorkflow() {
        ICommand touch = CommandFactory.getCommand("touch");
        String result = touch.execute(this.state, new String[]{"newfile.txt"});
        Assertions.assertEquals("", result);
        TerminalFile newFile = (TerminalFile) home.getChild("newfile.txt");
        Assertions.assertNotNull(newFile);
        
        ICommand cp = CommandFactory.getCommand("cp");
        result = cp.execute(this.state, new String[]{"newfile.txt", "newfile_copy.txt"});
        Assertions.assertEquals("", result);
        TerminalFile copy = (TerminalFile) home.getChild("newfile_copy.txt");
        Assertions.assertNotNull(copy);
        
        ICommand mv = CommandFactory.getCommand("mv");
        result = mv.execute(this.state, new String[]{"newfile.txt", "moved.txt"});
        Assertions.assertEquals("", result);
        Assertions.assertNull(this.home.getChild("newfile.txt"));
        Assertions.assertNotNull(this.home.getChild("moved.txt"));
        
        ICommand rm = CommandFactory.getCommand("rm");
        result = rm.execute(state, new String[]{"moved.txt"});
        Assertions.assertEquals("", result);
        Assertions.assertNull(this.home.getChild("moved.txt"));
    }
    
    @Test
    void testDirectoryOperationsWorkflow() {
        ICommand mkdir = CommandFactory.getCommand("mkdir");
        String result = mkdir.execute(state, new String[]{"newdir"});
        Assertions.assertEquals("", result);
        TerminalDirectory newDir = (TerminalDirectory) this.home.getChild("newdir");
        Assertions.assertNotNull(newDir);
        
        ICommand cd = CommandFactory.getCommand("cd");
        result = cd.execute(state, new String[]{"newdir"});
        Assertions.assertEquals("", result);
        Assertions.assertEquals("/home/newdir", this.state.getCurrentDirectory().getPath());
        
        ICommand touch = CommandFactory.getCommand("touch");
        result = touch.execute(this.state, new String[]{"test.txt"});
        Assertions.assertEquals("", result);
        
        result = cd.execute(this.state, new String[]{".."});
        Assertions.assertEquals("", result);
        Assertions.assertEquals("/home", this.state.getCurrentDirectory().getPath());
        
        ICommand rmdir = CommandFactory.getCommand("rmdir");
        result = rmdir.execute(this.state, new String[]{"newdir"});
        // Assertions.assertEquals("", result);
        // rmdir: failed to remove 'newdir': Directory not empty
        Assertions.assertEquals("rmdir: failed to remove 'newdir': Directory not empty\n", result);
        Assertions.assertNotNull(this.home.getChild("newdir"));
    }
    
    @Test
    void testCommandHistoryIntegration() {
        ICommand ls = CommandFactory.getCommand("ls");
        ls.execute(this.state, new String[0]);
        this.history.addToHistory("ls");
        ICommand cd = CommandFactory.getCommand("cd");
        cd.execute(this.state, new String[]{"documents"});
        this.history.addToHistory("cd documents");
        ICommand pwd = CommandFactory.getCommand("pwd");
        pwd.execute(this.state, new String[0]);
        this.history.addToHistory("pwd");
        this.history.resetHistoryIndex();
        // NOTE : here state about history of commands is note 'manually', because strongly linked to GUI. 
        
        Assertions.assertEquals(3, this.history.getCommandHistory().size());
        Assertions.assertEquals("ls", this.history.getCommandHistory().get(0));
        Assertions.assertEquals("cd documents", this.history.getCommandHistory().get(1));
        Assertions.assertEquals("pwd", this.history.getCommandHistory().get(2));
        
        this.history.resetHistoryIndex();
        String prev = this.history.getPreviousCommand();
        Assertions.assertEquals("pwd", prev);
        prev = this.history.getPreviousCommand();
        Assertions.assertEquals("cd documents", prev);
    }
    
    @Test
    void testErrorHandling() {
        ICommand ls = CommandFactory.getCommand("ls");
        String result = ls.execute(this.state, new String[]{"nonexistent"});
        Assertions.assertTrue(result.contains("No such file or directory"));
        
        ICommand cd = CommandFactory.getCommand("cd");
        result = cd.execute(this.state, new String[]{"nonexistent"});
        Assertions.assertTrue(result.contains("no such file or directory"));
        
        ICommand rm = CommandFactory.getCommand("rm");
        result = rm.execute(this.state, new String[]{"nonexistent.txt"});
        Assertions.assertTrue(result.contains("No such file or directory"));
    }
    
    @Test
    void testPathResolution() {
        ICommand cd = CommandFactory.getCommand("cd");
        String result = cd.execute(this.state, new String[]{"/home/documents"});
        Assertions.assertEquals("", result);
        Assertions.assertEquals("/home/documents", this.state.getCurrentDirectory().getPath());
        
        result = cd.execute(this.state, new String[]{".."});
        Assertions.assertEquals("", result);
        Assertions.assertEquals("/home", this.state.getCurrentDirectory().getPath());
        
        result = cd.execute(this.state, new String[]{"documents/../images"});
        Assertions.assertEquals("", result);
        Assertions.assertEquals("/home/images", this.state.getCurrentDirectory().getPath());
    }
}
