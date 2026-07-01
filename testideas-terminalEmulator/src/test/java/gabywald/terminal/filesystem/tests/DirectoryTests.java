package gabywald.terminal.filesystem.tests;

import gabywald.terminal.filesystem.TerminalDirectory;
import gabywald.terminal.filesystem.TerminalFile;
import org.junit.jupiter.api.*;

/**
 * @author Gabriel Chandesris (2026)
 */
class DirectoryTest {
    private TerminalDirectory root;
    private TerminalDirectory dir1;
    private TerminalDirectory dir2;
    
    @BeforeEach
    void setUp() {
        this.root = new TerminalDirectory("/", null);
        this.dir1 = this.root.createDirectory("dir1");
        this.dir2 = this.dir1.createDirectory("dir2");
    }
    
    @AfterEach
    void tearDown() {
    	this.root = null; this.dir1 = null; this.dir2 = null;
    }
    
    @Test
    void testCreateDirectory() {
        TerminalDirectory newDir = this.root.createDirectory("newdir");
        Assertions.assertNotNull(newDir);
        Assertions.assertEquals("newdir", newDir.getName());
        Assertions.assertEquals(this.root, newDir.getParent());
        Assertions.assertTrue(this.root.hasChild("newdir"));
    }
    
    @Test
    void testCreateDuplicateDirectory() {
        TerminalDirectory first = this.root.createDirectory("test");
        Assertions.assertNotNull(first);
        TerminalDirectory second = this.root.createDirectory("test");
        Assertions.assertNull(second);
    }
    
    @Test
    void testCreateFile() {
        TerminalFile file = this.root.createFile("test.txt");
        Assertions.assertNotNull(file);
        Assertions.assertEquals("test.txt", file.getName());
        Assertions.assertEquals(this.root, file.getParent());
        Assertions.assertTrue(this.root.hasChild("test.txt"));
    }
    
    @Test
    void testIsEmpty() {
        TerminalDirectory emptyDir = this.root.createDirectory("empty");
        Assertions.assertTrue(emptyDir.isEmpty());
        emptyDir.createFile("file.txt");
        Assertions.assertFalse(emptyDir.isEmpty());
    }
    
    @Test
    void testClear() {
    	this.dir1.createFile("file1.txt");
    	this.dir1.createFile("file2.txt");
    	this.dir1.createDirectory("subdir");
    	// A dir is already present !
        Assertions.assertEquals(4, this.dir1.getChildCount());
        this.dir1.clear();
        Assertions.assertEquals(0, this.dir1.getChildCount());
        Assertions.assertTrue(this.dir1.isEmpty());
    }
    
    @Test
    void testRemoveChildByName() {
    	this.dir1.createFile("toRemove.txt");
        Assertions.assertTrue(this.dir1.hasChild("toRemove.txt"));
        Assertions.assertTrue(this.dir1.removeChildByName("toRemove.txt"));
        Assertions.assertFalse(this.dir1.hasChild("toRemove.txt"));
    }
    
    @Test
    void testPathNavigation() {
    	Assertions.assertEquals("/", this.root.getPath());
    	Assertions.assertEquals("/dir1", this.dir1.getPath());
    	Assertions.assertEquals("/dir1/dir2", this.dir2.getPath());
    }
}
