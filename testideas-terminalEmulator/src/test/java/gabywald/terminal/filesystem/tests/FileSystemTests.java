package gabywald.terminal.filesystem.tests;

import gabywald.terminal.filesystem.Directory;
import gabywald.terminal.filesystem.FileNode;
import gabywald.terminal.filesystem.TerminalFile;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class FileSystemTest {
    private Directory root;
    private Directory home;
    private Directory documents;
    private TerminalFile file1;
    private TerminalFile file2;
    
    @BeforeEach
    void setUp() {
        root = new Directory("/", null);
        home = root.createDirectory("home");
        documents = home.createDirectory("documents");
        file1 = home.createFile("file1.txt");
        file2 = documents.createFile("file2.txt");
        file1.setContent("Hello World");
        file2.setContent("Test content");
    }
    
    @AfterEach
    void tearDown() {
        root = null; home = null; documents = null; file1 = null; file2 = null;
    }
    
    @Test
    void testDirectoryCreation() {
        assertNotNull(root);
        assertEquals("/", root.getName());
        assertNull(root.getParent());
        assertNotNull(home);
        assertEquals("home", home.getName());
        assertEquals(root, home.getParent());
    }
    
    @Test
    void testFileCreation() {
        assertNotNull(file1);
        assertEquals("file1.txt", file1.getName());
        assertEquals(home, file1.getParent());
        assertTrue(file1.isFile());
        assertFalse(file1.isDirectory());
    }
    
    @Test
    void testDirectoryPath() {
        assertEquals("/", root.getPath());
        assertEquals("/home", home.getPath());
        assertEquals("/home/documents", documents.getPath());
    }
    
    @Test
    void testFilePath() {
        assertEquals("/home/file1.txt", file1.getPath());
        assertEquals("/home/documents/file2.txt", file2.getPath());
    }
    
    @Test
    void testDirectoryChildren() {
        assertEquals(2, home.getChildCount());
        assertTrue(home.hasChild("documents"));
        assertTrue(home.hasChild("file1.txt"));
        assertFalse(home.hasChild("nonexistent"));
    }
    
    @Test
    void testGetChild() {
        FileNode child = home.getChild("file1.txt");
        assertNotNull(child);
        assertEquals("file1.txt", child.getName());
        FileNode nonexistent = home.getChild("nonexistent");
        assertNull(nonexistent);
    }
    
    @Test
    void testAddAndRemoveChild() {
        TerminalFile newFile = new TerminalFile("newfile.txt", home);
        assertTrue(home.addChild(newFile));
        assertEquals(3, home.getChildCount());
        assertTrue(home.removeChild(newFile));
        assertEquals(2, home.getChildCount());
    }
    
    @Test
    void testFileContent() {
        assertEquals("Hello World", file1.getContent());
        assertEquals(11, file1.getSize());
        file1.appendContent("!");
        assertEquals("Hello World!", file1.getContent());
        assertEquals(12, file1.getSize());
    }
    
    @Test
    void testFileOperations() {
        assertFalse(file1.isEmpty());
        file1.clear();
        assertTrue(file1.isEmpty());
        assertEquals(0, file1.getSize());
    }
    
    @Test
    void testDeleteFile() {
        assertTrue(file1.delete());
        assertEquals(1, home.getChildCount());
        assertNull(home.getChild("file1.txt"));
    }
    
    @Test
    void testDeleteDirectory() {
        assertTrue(documents.delete());
        assertEquals(1, home.getChildCount());
        assertNull(home.getChild("documents"));
    }
    
    @Test
    void testSubdirectories() {
        assertEquals(1, home.getSubdirectories().size());
        assertEquals("documents", home.getSubdirectories().get(0).getName());
    }
    
    @Test
    void testFiles() {
        assertEquals(1, home.getFiles().size());
        assertEquals("file1.txt", home.getFiles().get(0).getName());
    }
    
    @Test
    void testDirectoryComparison() {
        Directory dir1 = new Directory("test1", root);
        Directory dir2 = new Directory("test2", root);
        assertNotEquals(dir1, dir2);
    }
}
