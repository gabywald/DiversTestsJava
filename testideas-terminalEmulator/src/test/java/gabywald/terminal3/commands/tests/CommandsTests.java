package gabywald.terminal3.commands.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gabywald.terminal3.serverside.filesystem.TerminalFile;
import gabywald.terminal3.serverside.filesystem.TerminalNode;
import gabywald.terminal3.serverside.filesystem.TerminalState;
import gabywald.terminal3.serverside.shell.CommandHelper;
import gabywald.terminal3.serverside.shell.commands.CatCommand;
import gabywald.terminal3.serverside.shell.commands.EchoCommand;
import gabywald.terminal3.serverside.shell.commands.GrepCommand;
import gabywald.terminal3.serverside.shell.commands.SortCommand;
import gabywald.terminal3.serverside.shell.commands.TouchCommand;
import gabywald.terminal3.serverside.shell.commands.TrCommand;
import gabywald.terminal3.serverside.shell.commands.WcCommand;

/**
 * @author Gabriel Chandesris (2026)
 */
class CommandsTest {
    private TerminalState state;
    
    /**
     * "echo $content >> $filename"
     * @param filename
     * @param content
     */
    private void createFileAndWrite(String filename, String content) {
    	this.state.getCurrentDirectory().createFile( filename );
    	TerminalNode testfile = CommandHelper.resolveFile(this.state, filename);
    	((TerminalFile)testfile).appendContent(content);
    }

    @BeforeEach
    void setUp() {
        this.state = new TerminalState();
    }

    @Test
    void testCatCommandWithFile() {
    	this.createFileAndWrite("test.txt", "Hello");
    	
        CatCommand cat = new CatCommand();
        Assertions.assertEquals("Hello", cat.execute(this.state, new String[]{"test.txt"}, null));
    }

    @Test
    void testCatCommandWithStdin() {
        CatCommand cat = new CatCommand();
        Assertions.assertEquals("Input text", cat.execute(this.state, new String[]{}, "Input text"));
    }
    
    @Test
    void testTouchCommand() {
        TouchCommand touch = new TouchCommand();
        Assertions.assertEquals("", touch.execute(this.state, new String[]{"test.txt"}, null));
    }

    @Test
    void testEchoCommand() {
        EchoCommand echo = new EchoCommand();
        Assertions.assertEquals("Hello World", echo.execute(this.state, new String[]{"Hello", "World"}, null));
    }
    
    @Test
    void testEchoRedirectionCommand() {
        TouchCommand touch = new TouchCommand();
        Assertions.assertEquals("", touch.execute(this.state, new String[]{"test.txt"}, null));
    	EchoCommand echo = new EchoCommand();
        Assertions.assertEquals("", echo.execute(this.state, new String[]{"Hello World"}, "test.txt"));
        CatCommand cat = new CatCommand();
        Assertions.assertEquals("Hello World", cat.execute(this.state, new String[]{ "test.txt" }));
    }

    @Test
    void testGrepCommandWithFile() {
    	this.createFileAndWrite("test.txt", "line1\nline2\nline3");
        GrepCommand grep = new GrepCommand();
        Assertions.assertEquals("line2", grep.execute(this.state, new String[]{ "line2", "test.txt" }, null));
    }

    @Test
    void testGrepCommandWithStdin() {
        GrepCommand grep = new GrepCommand();
        Assertions.assertEquals("line2", grep.execute(this.state, new String[]{ "line2" }, "line1\nline2\nline3"));
    }

    @Test
    void testWcCommandWithFile() {
    	this.createFileAndWrite("test.txt", "Hello World");
        WcCommand wc = new WcCommand();
        Assertions.assertEquals("1 2 11", wc.execute(this.state, new String[]{"test.txt"}, null));
    }

    @Test
    void testWcCommandWithStdin() {
        WcCommand wc = new WcCommand();
        Assertions.assertEquals("2 2 11", wc.execute(this.state, new String[]{}, "Hello\nWorld"));
    }

    @Test
    void testTrCommandWithFile() {
    	this.createFileAndWrite("test.txt", "Hello");
        TrCommand tr = new TrCommand();
        Assertions.assertEquals("HeLLo", tr.execute(this.state, new String[]{"l", "L", "test.txt"}, null));
    }

    @Test
    void testTrCommandWithStdin() {
        TrCommand tr = new TrCommand();
        Assertions.assertEquals("HeLLo", tr.execute(this.state, new String[]{"l", "L"}, "Hello"));
    }

    @Test
    void testSortCommandWithFile() {
    	this.createFileAndWrite("test.txt", "banana\napple\ncherry");
        SortCommand sort = new SortCommand();
        String result = sort.execute(this.state, new String[]{"test.txt"}, null);
        Assertions.assertEquals("apple\nbanana\ncherry", result);
    }

    @Test
    void testSortCommandWithStdin() {
        SortCommand sort = new SortCommand();
        String result = sort.execute(this.state, new String[]{}, "banana\napple\ncherry");
        Assertions.assertEquals("apple\nbanana\ncherry", result);
    }
}