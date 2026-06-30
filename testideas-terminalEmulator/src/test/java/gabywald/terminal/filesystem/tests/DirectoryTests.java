package gabywald.terminal.filesystem.tests;

import gabywald.terminal.filesystem.Directory;
import gabywald.terminal.filesystem.TerminalFile;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class DirectoryTest {
    private Directory root;
    private Directory dir1;
    private Directory dir2;
    
    @BeforeEach
    void setUp() {
        root = new Directory("/", null);
        dir1 = root.createDirectory("dir1");
        dir2 = dir1.createDirectory("dir2");
    }
    
    @AfterEach
    void tearDown() {
        root = null; dir1 = null; dir2 = null;
    }
    
    @Test
    void testCreateDirectory() {
        Directory newDir = root.createDirectory("newdir");
        assertNotNull(newDir);
        assertEquals("newdir", newDir.getName());
        assertEquals(root, newDir.getParent());
        assertTrue(root.hasChild("newdir"));
    }
    
    @Test
    void testCreateDuplicateDirectory() {
        Directory first = root.createDirectory("test");
        assertNotNull(first);
        Directory second = root.createDirectory("test");
        assertNull(second);
    }
    
    @Test
    void testCreateFile() {
        TerminalFile file = root.createFile("test.txt");
        assertNotNull(file);
        assertEquals("test.txt", file.getName());
        assertEquals(root, file.getParent());
        assertTrue(root.hasChild("test.txt"));
    }
    
    @Test
    void testIsEmpty() {
        Directory emptyDir = root.createDirectory("empty");
        assertTrue(emptyDir.isEmpty());
        emptyDir.createFile("file.txt");
        assertFalse(emptyDir.isEmpty());
    }
    
    @Test
    void testClear() {
        dir1.createFile("file1.txt");
        dir1.createFile("file2.txt");
        dir1.createDirectory("subdir");
        assertEquals(3, dir1.getChildCount());
        dir1.clear();
        assertEquals(0, dir1.getChildCount());
        assertTrue(dir1.isEmpty());
    }
    
    @Test
    void testRemoveChildByName() {
        dir1.createFile("toRemove.txt");
        assertTrue(dir1.hasChild("toRemove.txt"));
        assertTrue(dir1.removeChildByName("toRemove.txt"));
        assertFalse(dir1.hasChild("toRemove.txt"));
    }
    
    @Test
    void testPathNavigation() {
        assertEquals("/", root.getPath());
        assertEquals("/dir1", dir1.getPath());
        assertEquals("/dir1/dir2", dir2.getPath());
    }
}