package gabywald.terminal2.commands.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gabywald.terminal2.FileSystem;
import gabywald.terminal2.commands.*;

class CommandsTest {
    private FileSystem fileSystem;

    @BeforeEach
    void setUp() {
        this.fileSystem = new FileSystem();
    }

    @Test
    void testCatCommandWithFile() {
    	this.fileSystem.echo("test.txt", "Hello");
        CatCommand cat = new CatCommand(this.fileSystem);
        Assertions.assertEquals("Hello", cat.execute(new String[]{"test.txt"}, null));
    }

    @Test
    void testCatCommandWithStdin() {
        CatCommand cat = new CatCommand(this.fileSystem);
        Assertions.assertEquals("Input text", cat.execute(new String[]{}, "Input text"));
    }

    @Test
    void testEchoCommand() {
        EchoCommand echo = new EchoCommand(this.fileSystem);
        Assertions.assertEquals("Hello World", echo.execute(new String[]{"Hello", "World"}, null));
    }

    @Test
    void testGrepCommandWithFile() {
    	this.fileSystem.echo("test.txt", "line1\nline2\nline3");
        GrepCommand grep = new GrepCommand(this.fileSystem);
        Assertions.assertEquals("line2\n", grep.execute(new String[]{"line2", "test.txt"}, null));
    }

    @Test
    void testGrepCommandWithStdin() {
        GrepCommand grep = new GrepCommand(this.fileSystem);
        Assertions.assertEquals("line2\n", grep.execute(new String[]{"line2"}, "line1\nline2\nline3"));
    }

    @Test
    void testWcCommandWithFile() {
    	this.fileSystem.echo("test.txt", "Hello World");
        WcCommand wc = new WcCommand(this.fileSystem);
        Assertions.assertEquals("1 2 11", wc.execute(new String[]{"test.txt"}, null));
    }

    @Test
    void testWcCommandWithStdin() {
        WcCommand wc = new WcCommand(this.fileSystem);
        Assertions.assertEquals("2 2 11", wc.execute(new String[]{}, "Hello\nWorld"));
    }

    @Test
    void testTrCommandWithFile() {
    	this.fileSystem.echo("test.txt", "Hello");
        TrCommand tr = new TrCommand(this.fileSystem);
        Assertions.assertEquals("HeLLo", tr.execute(new String[]{"l", "L", "test.txt"}, null));
    }

    @Test
    void testTrCommandWithStdin() {
        TrCommand tr = new TrCommand(this.fileSystem);
        Assertions.assertEquals("HeLLo", tr.execute(new String[]{"l", "L"}, "Hello"));
    }

    @Test
    void testSortCommandWithFile() {
    	this.fileSystem.echo("test.txt", "banana\napple\ncherry");
        SortCommand sort = new SortCommand(this.fileSystem);
        String result = sort.execute(new String[]{"test.txt"}, null);
        Assertions.assertEquals("apple\nbanana\ncherry", result);
    }

    @Test
    void testSortCommandWithStdin() {
        SortCommand sort = new SortCommand(this.fileSystem);
        String result = sort.execute(new String[]{}, "banana\napple\ncherry");
        Assertions.assertEquals("apple\nbanana\ncherry", result);
    }
}