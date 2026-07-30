package gabywald.terminal2.tests;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gabywald.terminal2.FileSystem;

/**
 * @author Gabriel Chandesris (2026)
 */
class FileSystemTest {
    private FileSystem fileSystem;

    @BeforeEach
    void setUp() {
    	this.fileSystem = new FileSystem();
    }

    @Test
    void testGetRoot() {
        Assertions.assertEquals("/", this.fileSystem.getRoot());
    }

    @Test
    void testGetCurrentDirectory() {
    	Assertions.assertEquals("/", this.fileSystem.getCurrentDirectory());
    }

    @Test
    void testMkdir() {
    	Assertions.assertTrue(this.fileSystem.mkdir("test"));
    	Assertions.assertFalse(this.fileSystem.mkdir("test"));
    }

    @Test
    void testTouch() {
    	Assertions.assertTrue(this.fileSystem.touch("file.txt"));
    	Assertions.assertFalse(this.fileSystem.touch("file.txt"));
    }

    @Test
    void testLs() {
    	this.fileSystem.mkdir("dir1");
    	this.fileSystem.touch("file1.txt");
        List<String> contents = this.fileSystem.ls();
        Assertions.assertTrue(contents.contains("dir1/"));
        Assertions.assertTrue(contents.contains("file1.txt"));
    }

    @Test
    void testCd() {
    	this.fileSystem.mkdir("test");
    	this.fileSystem.setCurrentDirectory("/test");
        Assertions.assertEquals("/test", this.fileSystem.getCurrentDirectory());
    }

    @Test
    void testRm() {
    	this.fileSystem.touch("file.txt");
        Assertions.assertTrue(this.fileSystem.rm("file.txt"));
        Assertions.assertFalse(this.fileSystem.exists("file.txt"));
    }

    @Test
    void testCat() {
    	this.fileSystem.touch("file.txt");
    	this.fileSystem.echo("file.txt", "Hello, World!");
        Assertions.assertEquals("Hello, World!", this.fileSystem.cat("file.txt"));
    }

    @Test
    void testEcho() {
        Assertions.assertTrue(this.fileSystem.echo("file.txt", "Test content"));
        Assertions.assertEquals("Test content", this.fileSystem.cat("file.txt"));
    }

    @Test
    void testAppend() {
    	this.fileSystem.echo("file.txt", "Line1\n");
        Assertions.assertTrue(this.fileSystem.append("file.txt", "Line2\n"));
        Assertions.assertEquals("Line1\nLine2\n", this.fileSystem.cat("file.txt"));
    }

    @Test
    void testExists() {
    	this.fileSystem.touch("file.txt");
        Assertions.assertTrue(this.fileSystem.exists("file.txt"));
        Assertions.assertFalse(this.fileSystem.exists("nonexistent.txt"));
    }

    @Test
    void testGetParentDirectory() {
        Assertions.assertEquals("/", this.fileSystem.getParentDirectory("/test"));
        Assertions.assertEquals("/", this.fileSystem.getParentDirectory("/"));
        Assertions.assertEquals("/test", this.fileSystem.getParentDirectory("/test/subdir"));
    }

    @Test
    void testResolvePath() {
    	this.fileSystem.mkdir("test");
    	this.fileSystem.setCurrentDirectory("/test");
        Assertions.assertEquals("/test/file.txt", this.fileSystem.resolvePath("file.txt"));
        Assertions.assertEquals("/images", this.fileSystem.resolvePath("/images"));
    }

    @Test
    void testNormalizePath() {
        Assertions.assertEquals("/a/c", this.fileSystem.normalizePath("/a/./b/../c"));
        Assertions.assertEquals("/c", this.fileSystem.normalizePath("/a/b/../../c"));
        Assertions.assertEquals("/", this.fileSystem.normalizePath("/../.."));
    }

    @Test
    void testRmdir() {
    	this.fileSystem.mkdir("dir1");
    	this.fileSystem.setCurrentDirectory("/dir1");
    	this.fileSystem.mkdir("dir2");
    	this.fileSystem.touch("file.txt");
    	this.fileSystem.setCurrentDirectory("/");
        Assertions.assertTrue(this.fileSystem.rmdir("dir1"));
        Assertions.assertFalse(this.fileSystem.exists("dir1"));
    }

    @Test
    void testIsDirectory() {
    	this.fileSystem.mkdir("test");
        Assertions.assertTrue(this.fileSystem.isDirectory("/test"));
        Assertions.assertFalse(this.fileSystem.isDirectory("/nonexistent"));
    }

    @Test
    void testIsFile() {
    	this.fileSystem.touch("file.txt");
        Assertions.assertTrue(this.fileSystem.isFile("/file.txt"));
        Assertions.assertFalse(this.fileSystem.isFile("/nonexistent"));
    }

    @Test
    void testLsWithPath() {
    	this.fileSystem.mkdir("test");
    	this.fileSystem.touch("file.txt");
        List<String> contents = this.fileSystem.ls("/");
        Assertions.assertTrue(contents.contains("test/"));
        Assertions.assertTrue(contents.contains("file.txt"));
    }

    @Test
    void testSetCurrentDirectoryInvalid() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
        	this.fileSystem.setCurrentDirectory("/nonexistent");
        });
    }

    @Test
    void testMkdirInvalidName() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
        	this.fileSystem.mkdir("");
        });
    }

    @Test
    void testTouchInvalidName() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
        	this.fileSystem.touch("");
        });
    }
}
