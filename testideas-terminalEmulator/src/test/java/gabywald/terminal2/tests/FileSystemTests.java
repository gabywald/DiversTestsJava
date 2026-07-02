package gabywald.terminal2.tests;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gabywald.terminal2.FileSystem;

class FileSystemTest {
    private FileSystem fileSystem;

    @BeforeEach
    void setUp() {
        fileSystem = new FileSystem();
    }

    @Test
    void testGetRoot() {
        Assertions.assertEquals("/", fileSystem.getRoot());
    }

    @Test
    void testGetCurrentDirectory() {
    	Assertions.assertEquals("/", fileSystem.getCurrentDirectory());
    }

    @Test
    void testMkdir() {
    	Assertions.assertTrue(fileSystem.mkdir("test"));
    	Assertions.assertFalse(fileSystem.mkdir("test"));
    }

    @Test
    void testTouch() {
    	Assertions.assertTrue(fileSystem.touch("file.txt"));
    	Assertions.assertFalse(fileSystem.touch("file.txt"));
    }

    @Test
    void testLs() {
        fileSystem.mkdir("dir1");
        fileSystem.touch("file1.txt");
        List<String> contents = fileSystem.ls();
        Assertions.assertTrue(contents.contains("dir1/"));
        Assertions.assertTrue(contents.contains("file1.txt"));
    }

    @Test
    void testCd() {
        fileSystem.mkdir("test");
        fileSystem.setCurrentDirectory("/test");
        Assertions.assertEquals("/test", fileSystem.getCurrentDirectory());
    }

    @Test
    void testRm() {
        fileSystem.touch("file.txt");
        Assertions.assertTrue(fileSystem.rm("file.txt"));
        Assertions.assertFalse(fileSystem.exists("file.txt"));
    }

    @Test
    void testCat() {
        fileSystem.touch("file.txt");
        fileSystem.echo("file.txt", "Hello, World!");
        Assertions.assertEquals("Hello, World!", fileSystem.cat("file.txt"));
    }

    @Test
    void testEcho() {
        Assertions.assertTrue(fileSystem.echo("file.txt", "Test content"));
        Assertions.assertEquals("Test content", fileSystem.cat("file.txt"));
    }

    @Test
    void testAppend() {
        fileSystem.echo("file.txt", "Line1\n");
        Assertions.assertTrue(fileSystem.append("file.txt", "Line2\n"));
        Assertions.assertEquals("Line1\nLine2\n", fileSystem.cat("file.txt"));
    }

    @Test
    void testExists() {
        fileSystem.touch("file.txt");
        Assertions.assertTrue(fileSystem.exists("file.txt"));
        Assertions.assertFalse(fileSystem.exists("nonexistent.txt"));
    }

    @Test
    void testGetParentDirectory() {
        Assertions.assertEquals("/", fileSystem.getParentDirectory("/test"));
        Assertions.assertEquals("/", fileSystem.getParentDirectory("/"));
        Assertions.assertEquals("/test", fileSystem.getParentDirectory("/test/subdir"));
    }

    @Test
    void testResolvePath() {
        fileSystem.mkdir("test");
        fileSystem.setCurrentDirectory("/test");
        Assertions.assertEquals("/test/file.txt", fileSystem.resolvePath("file.txt"));
        Assertions.assertEquals("/images", fileSystem.resolvePath("/images"));
    }

    @Test
    void testNormalizePath() {
        Assertions.assertEquals("/a/c", fileSystem.normalizePath("/a/./b/../c"));
        Assertions.assertEquals("/c", fileSystem.normalizePath("/a/b/../../c"));
        Assertions.assertEquals("/", fileSystem.normalizePath("/../.."));
    }

    @Test
    void testRmdir() {
        fileSystem.mkdir("dir1");
        fileSystem.setCurrentDirectory("/dir1");
        fileSystem.mkdir("dir2");
        fileSystem.touch("file.txt");
        fileSystem.setCurrentDirectory("/");
        Assertions.assertTrue(fileSystem.rmdir("dir1"));
        Assertions.assertFalse(fileSystem.exists("dir1"));
    }

    @Test
    void testIsDirectory() {
        fileSystem.mkdir("test");
        Assertions.assertTrue(fileSystem.isDirectory("/test"));
        Assertions.assertFalse(fileSystem.isDirectory("/nonexistent"));
    }

    @Test
    void testIsFile() {
        fileSystem.touch("file.txt");
        Assertions.assertTrue(fileSystem.isFile("/file.txt"));
        Assertions.assertFalse(fileSystem.isFile("/nonexistent"));
    }

    @Test
    void testLsWithPath() {
        fileSystem.mkdir("test");
        fileSystem.touch("file.txt");
        List<String> contents = fileSystem.ls("/");
        Assertions.assertTrue(contents.contains("test/"));
        Assertions.assertTrue(contents.contains("file.txt"));
    }

    @Test
    void testSetCurrentDirectoryInvalid() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            fileSystem.setCurrentDirectory("/nonexistent");
        });
    }

    @Test
    void testMkdirInvalidName() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            fileSystem.mkdir("");
        });
    }

    @Test
    void testTouchInvalidName() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            fileSystem.touch("");
        });
    }
}
