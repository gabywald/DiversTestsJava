package gabywald.terminal3.serverside.shell.scripts;

import java.util.Map;

import gabywald.terminal3.serverside.filesystem.TerminalDirectory;
import gabywald.terminal3.serverside.filesystem.TerminalFile;
import gabywald.terminal3.serverside.filesystem.TerminalNode;
import gabywald.terminal3.serverside.filesystem.TerminalState;
import gabywald.terminal3.serverside.shell.CommandFactory;
import gabywald.terminal3.serverside.shell.CommandParser;
import gabywald.terminal3.serverside.shell.ICommand;

/**
 * Script execution engine with minimal scripting language support
 * @author Gabriel Chandesris (2026)
 */
public class ScriptEngine {
	
    private TerminalState state;
    private ScriptContext context;
    
    public ScriptEngine(TerminalState state) {
        this.state = state;
        this.context = new ScriptContext();
    }
    
    public String executeFile(TerminalFile file) {
        if (file == null) { return "Script file not found"; }
        return this.execute(file.getContent());
    }
    
    public String execute(String script) {
        if (script == null || script.trim().isEmpty()) { return ""; }
        
        StringBuilder output = new StringBuilder();
        String[] lines = script.split("\n");
        
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.startsWith("#") || trimmed.isEmpty()) { continue; }
            
            String result = this.executeLine(trimmed);
            if (result != null && !result.isEmpty()) 
            	{ output.append(result).append("\n"); }
        }
        return output.toString();
    }
    
    private String executeLine(String line) {
        if (line.startsWith("if "))		{ return executeIf(line); }
        if (line.startsWith("while "))	{ return executeWhile(line); }
        if (line.startsWith("for "))	{ return executeFor(line); }
        if (line.equals("fi") || line.equals("done") || line.equals("end"))	{ return null; }
        if (line.startsWith("set "))	{ return executeSet(line); }
        if (line.startsWith("echo ")) 	{ return executeScriptEcho(line); }
        if (line.startsWith("cd ")) 	{ return executeScriptCd(line); }
        if (line.startsWith("pwd")) 	{ return state.getCurrentDirectory().getPath(); }
        if (line.startsWith("ls")) 		{ return executeLs(line); }
        if (line.startsWith("cat")) 	{ return executeCat(line); }
        
        String[] parts = CommandParser.parse(line);
        if (parts.length > 0) {
            ICommand cmd = CommandFactory.getCommand(parts[0]);
            if (cmd != null) {
                String[] args = new String[parts.length - 1];
                System.arraycopy(parts, 1, args, 0, args.length);
                return cmd.execute(state, args);
            }
        }
        return "Script error: unknown command '" + line.split(" ")[0] + "'";
    }
    
    private String executeIf(String line) {
        String condition = line.substring(3, line.length() - 1).trim();
        boolean result = this.evaluateCondition(condition);
        this.context.setInIfBlock(true);
        this.context.setIfConditionResult(result);
        this.context.setIfBlockDepth(1);
        return null;
    }
    
    private String executeWhile(String line) {
        String condition = line.substring(6, line.length() - 1).trim();
        boolean result = this.evaluateCondition(condition);
        this.context.setInWhileBlock(true);
        this.context.setWhileCondition(condition);
        this.context.setWhileConditionResult(result);
        this.context.setWhileBlockDepth(1);
        return null;
    }
    
    private String executeFor(String line) {
        String[] parts = line.substring(4).trim().split(" in ");
        if (parts.length != 2) { return "Script error: invalid for syntax"; }
        
        String varName = parts[0].trim();
        String[] values = parts[1].trim().split(" ");
        
        this.context.setInForBlock(true);
        this.context.setForVariable(varName);
        this.context.setForValues(values);
        this.context.setForIndex(0);
        this.context.setForBlockDepth(1);
        
        if (values.length > 0) { this.context.getVariables().put(varName, values[0]); }
        return null;
    }
    
    private String executeSet(String line) {
        String[] parts = line.substring(4).trim().split("=", 2);
        if (parts.length != 2) { return "Script error: invalid set syntax"; }
        
        String varName = parts[0].trim();
        String value = parts[1].trim();
        value = this.replaceVariables(value);
        this.context.getVariables().put(varName, value);
        return null;
    }
    
    private String executeScriptEcho(String line) {
        String text = line.substring(5).trim();
        return this.replaceVariables(text);
    }
    
    private String executeScriptCd(String line) {
        String path = line.substring(3).trim();
        TerminalDirectory newDir = this.resolveDirectory(state, path);
        if (newDir == null) { return "cd: no such file or directory: " + path; }
        this.state.setCurrentDirectory(newDir);
        return null;
    }
    
    private String executeLs(String line) {
        String[] parts = CommandParser.parse(line);
        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, args.length);
        ICommand cmd = CommandFactory.getCommand("ls");
        if (cmd != null) { return cmd.execute(state, args); }
        return "ls: command not available";
    }
    
    private String executeCat(String line) {
        String[] parts = CommandParser.parse(line);
        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, args.length);
        ICommand cmd = CommandFactory.getCommand("cat");
        if (cmd != null) { return cmd.execute(state, args); } 
        return "cat: command not available";
    }
    
    private boolean evaluateCondition(String condition) {
        condition = condition.trim();
        if (condition.startsWith("$")) {
            String[] parts = condition.split("=");
            if (parts.length == 2) {
                String varName = parts[0].trim();
                String value = parts[1].trim().replaceAll("^$", ""); // NOTE "^\"|"$"
                String varValue = this.context.getVariables().getOrDefault(varName, "");
                return varValue.equals(value);
            }
        } else if (condition.startsWith("-")) {
            String[] parts = condition.split(" ");
            if (parts.length == 2) {
                String test = parts[0];
                String path = parts[1].trim().replaceAll("^$", ""); // NOTE "^\"|"$"
                TerminalNode node = this.resolveFile(state, path);
                if ("-f".equals(test)) { return node != null && node.isFile(); } 
                if ("-d".equals(test)) { return node != null && node.isDirectory(); } 
                if ("-e".equals(test)) { return node != null; } 
            }
        }
        return false;
    }
    
    private String replaceVariables(String text) {
        String result = text;
        for (Map.Entry<String, String> entry : this.context.getVariables().entrySet()) 
        	{ result = result.replace("$" + entry.getKey(), entry.getValue()); }
        return result;
    }
    
    private TerminalNode resolveFile(TerminalState state, String fileName) {
        TerminalDirectory current = state.getCurrentDirectory();
        if (fileName.startsWith("/")) { return this.resolveAbsolutePath(state.getRootDirectory(), fileName); }
        return this.resolveRelativePath(current, fileName);
    }
    
    private TerminalDirectory resolveDirectory(TerminalState state, String dirName) {
        TerminalDirectory current = state.getCurrentDirectory();
        if (dirName.startsWith("/")) { return this.resolveAbsoluteDirectory(state.getRootDirectory(), dirName); }
        return this.resolveRelativeDirectory(current, dirName);
    }
    
    private TerminalNode resolveAbsolutePath(TerminalDirectory root, String path) {
        String[] parts = path.split("/");
        TerminalDirectory current = root;
        for (int i = 1; i < parts.length; i++) {
            if (parts[i].isEmpty() || ".".equals(parts[i])) { continue; }
            if ("..".equals(parts[i])) {
                if (current.getParent() != null) { current = current.getParent(); }
            } else {
                TerminalNode node = current.getChild(parts[i]);
                if (node == null)			{ return null; }
                if (i == parts.length - 1)	{ return node; }
                if (!node.isDirectory())	{ return null; }
                current = (TerminalDirectory) node;
            }
        }
        return current;
    }
    
    private TerminalNode resolveRelativePath(TerminalDirectory current, String path) {
        String[] parts = path.split("/");
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].isEmpty() || ".".equals(parts[i])) { continue; }
            if ("..".equals(parts[i])) {
                if (current.getParent() != null) { current = current.getParent(); }
            } else {
                TerminalNode node = current.getChild(parts[i]);
                if (node == null)			{ return null; }
                if (i == parts.length - 1)	{ return node; }
                if (!node.isDirectory())	{ return null; }
                current = (TerminalDirectory) node;
            }
        }
        return current;
    }
    
    private TerminalDirectory resolveAbsoluteDirectory(TerminalDirectory root, String path) {
        String[] parts = path.split("/");
        TerminalDirectory current = root;
        for (int i = 1; i < parts.length; i++) {
            if (parts[i].isEmpty() || ".".equals(parts[i])) continue;
            if ("..".equals(parts[i])) {
                if (current.getParent() != null) current = current.getParent();
            } else {
                TerminalNode node = current.getChild(parts[i]);
                if (node == null || !node.isDirectory()) return null;
                current = (TerminalDirectory) node;
            }
        }
        return current;
    }
    
    private TerminalDirectory resolveRelativeDirectory(TerminalDirectory current, String path) {
        String[] parts = path.split("/");
        for (String part : parts) {
            if (part.isEmpty() || ".".equals(part)) continue;
            if ("..".equals(part)) {
                if (current.getParent() != null) current = current.getParent();
            } else {
                TerminalNode node = current.getChild(part);
                if (node == null || !node.isDirectory()) return null;
                current = (TerminalDirectory) node;
            }
        }
        return current;
    }
    
    public ScriptContext getContext()			{ return context; }
    public TerminalState getState()				{ return state; }
    public void setState(TerminalState state)	{ this.state = state; }
    
}
