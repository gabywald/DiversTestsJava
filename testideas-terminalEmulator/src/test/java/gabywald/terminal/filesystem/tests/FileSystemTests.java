package gabywald.terminal.filesystem.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gabywald.terminal.filesystem.TerminalDirectory;
import gabywald.terminal.filesystem.TerminalNode;
import gabywald.terminal.filesystem.TerminalFile;

/**
 * @author Gabriel Chandesris (2026)
 */
class FileSystemTest {
    private TerminalDirectory root;
    private TerminalDirectory home;
    private TerminalDirectory documents;
    private TerminalFile file1;
    private TerminalFile file2;
    
    @BeforeEach
    void setUp() {
        this.root = new TerminalDirectory("/", null);
        this.home = this.root.createDirectory("home");
        this.documents = home.createDirectory("documents");
        this.file1 = this.home.createFile("file1.txt");
        this.file2 = this.documents.createFile("file2.txt");
        this.file1.setContent("Hello World");
        this.file2.setContent("Test content");
    }
    
    @AfterEach
    void tearDown() {
    	this.root = null; this.home = null; this.documents = null; this.file1 = null; this.file2 = null;
    }
    
    @Test
    void testDirectoryCreation() {
        Assertions.assertNotNull(this.root);
        Assertions.assertEquals("/", this.root.getName());
        Assertions.assertNull(this.root.getParent());
        Assertions.assertNotNull(this.home);
        Assertions.assertEquals("home", this.home.getName());
        Assertions.assertEquals(this.root, this.home.getParent());
    }
    
    @Test
    void testFileCreation() {
        Assertions.assertNotNull(this.file1);
        Assertions.assertEquals("file1.txt", this.file1.getName());
        Assertions.assertEquals(this.home, this.file1.getParent());
        Assertions.assertTrue(this.file1.isFile());
        Assertions.assertFalse(this.file1.isDirectory());
    }
    
    @Test
    void testDirectoryPath() {
        Assertions.assertEquals("/", this.root.getPath());
        Assertions.assertEquals("/home", this.home.getPath());
        Assertions.assertEquals("/home/documents", this.documents.getPath());
    }
    
    @Test
    void testFilePath() {
        Assertions.assertEquals("/home/file1.txt", this.file1.getPath());
        Assertions.assertEquals("/home/documents/file2.txt", this.file2.getPath());
    }
    
    @Test
    void testDirectoryChildren() {
        Assertions.assertEquals(2, this.home.getChildCount());
        Assertions.assertTrue(this.home.hasChild("documents"));
        Assertions.assertTrue(this.home.hasChild("file1.txt"));
        Assertions.assertFalse(this.home.hasChild("nonexistent"));
    }
    
    @Test
    void testGetChild() {
        TerminalNode child = this.home.getChild("file1.txt");
        Assertions.assertNotNull(child);
        Assertions.assertEquals("file1.txt", child.getName());
        TerminalNode nonexistent = this.home.getChild("nonexistent");
        Assertions.assertNull(nonexistent);
    }
    
    @Test
    void testAddAndRemoveChild() {
        TerminalFile newFile = new TerminalFile("newfile.txt", this.home);
        Assertions.assertTrue(this.home.addChild(newFile));
        Assertions.assertEquals(3, this.home.getChildCount());
        Assertions.assertTrue(this.home.removeChild(newFile));
        Assertions.assertEquals(2, this.home.getChildCount());
    }
    
    @Test
    void testFileContent() {
        Assertions.assertEquals("Hello World", this.file1.getContent());
        Assertions.assertEquals(11, this.file1.getSize());
        this.file1.appendContent("!");
        Assertions.assertEquals("Hello World!", this.file1.getContent());
        Assertions.assertEquals(12, this.file1.getSize());
    }
    
    @Test
    void testFileOperations() {
        Assertions.assertFalse(this.file1.isEmpty());
        this.file1.clear();
        Assertions.assertTrue(this.file1.isEmpty());
        Assertions.assertEquals(0, this.file1.getSize());
    }
    
    @Test
    void testDeleteFile() {
        Assertions.assertTrue(this.file1.delete());
        Assertions.assertEquals(1, this.home.getChildCount());
        Assertions.assertNull(this.home.getChild("file1.txt"));
    }
    
    @Test
    void testDeleteDirectory() {
        Assertions.assertTrue(documents.delete());
        Assertions.assertEquals(1, this.home.getChildCount());
        Assertions.assertNull(this.home.getChild("documents"));
    }
    
    @Test
    void testSubdirectories() {
        Assertions.assertEquals(1, this.home.getSubdirectories().size());
        Assertions.assertEquals("documents", this.home.getSubdirectories().get(0).getName());
    }
    
    @Test
    void testFiles() {
        Assertions.assertEquals(1, this.home.getFiles().size());
        Assertions.assertEquals("file1.txt", this.home.getFiles().get(0).getName());
    }
    
    @Test
    void testDirectoryComparison() {
        TerminalDirectory dir1 = new TerminalDirectory("test1", this.root);
        TerminalDirectory dir2 = new TerminalDirectory("test2", this.root);
        Assertions.assertNotEquals(dir1, dir2);
    }
}
