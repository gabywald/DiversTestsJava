package gabywald.terminal.script;

import gabywald.terminal.TerminalState;
import gabywald.terminal.commands.Command;
import gabywald.terminal.commands.CommandFactory;
import gabywald.terminal.commands.CommandParser;
import gabywald.terminal.filesystem.Directory;
import gabywald.terminal.filesystem.FileNode;
import gabywald.terminal.filesystem.TerminalFile;
import java.util.Map;

/**
 * Script execution engine with minimal scripting language support
 */
public class ScriptEngine {
    private TerminalState state;
    private ScriptContext context;
    
    public ScriptEngine(TerminalState state) {
        this.state = state;
        this.context = new ScriptContext();
    }
    
    public String executeFile(TerminalFile file) {
        if (file == null) return "Script file not found";
        return execute(file.getContent());
    }
    
    public String execute(String script) {
        if (script == null || script.trim().isEmpty()) return "";
        
        StringBuilder output = new StringBuilder();
        String[] lines = script.split("\n");
        
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.startsWith("#") || trimmed.isEmpty()) continue;
            
            String result = executeLine(trimmed);
            if (result != null && !result.isEmpty()) {
                output.append(result).append("\n");
            }
        }
        return output.toString();
    }
    
    private String executeLine(String line) {
        if (line.startsWith("if ")) return executeIf(line);
        if (line.startsWith("while ")) return executeWhile(line);
        if (line.startsWith("for ")) return executeFor(line);
        if (line.equals("fi") || line.equals("done") || line.equals("end")) return null;
        if (line.startsWith("set ")) return executeSet(line);
        if (line.startsWith("echo ")) return executeScriptEcho(line);
        if (line.startsWith("cd ")) return executeScriptCd(line);
        if (line.startsWith("pwd")) return state.getCurrentDirectory().getPath();
        if (line.startsWith("ls")) return executeLs(line);
        if (line.startsWith("cat")) return executeCat(line);
        
        String[] parts = CommandParser.parse(line);
        if (parts.length > 0) {
            Command cmd = CommandFactory.getCommand(parts[0]);
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
        boolean result = evaluateCondition(condition);
        context.setInIfBlock(true);
        context.setIfConditionResult(result);
        context.setIfBlockDepth(1);
        return null;
    }
    
    private String executeWhile(String line) {
        String condition = line.substring(6, line.length() - 1).trim();
        boolean result = evaluateCondition(condition);
        context.setInWhileBlock(true);
        context.setWhileCondition(condition);
        context.setWhileConditionResult(result);
        context.setWhileBlockDepth(1);
        return null;
    }
    
    private String executeFor(String line) {
        String[] parts = line.substring(4).trim().split(" in ");
        if (parts.length != 2) return "Script error: invalid for syntax";
        
        String varName = parts[0].trim();
        String[] values = parts[1].trim().split(" ");
        
        context.setInForBlock(true);
        context.setForVariable(varName);
        context.setForValues(values);
        context.setForIndex(0);
        context.setForBlockDepth(1);
        
        if (values.length > 0) context.getVariables().put(varName, values[0]);
        return null;
    }
    
    private String executeSet(String line) {
        String[] parts = line.substring(4).trim().split("=", 2);
        if (parts.length != 2) return "Script error: invalid set syntax";
        
        String varName = parts[0].trim();
        String value = parts[1].trim();
        value = replaceVariables(value);
        context.getVariables().put(varName, value);
        return null;
    }
    
    private String executeScriptEcho(String line) {
        String text = line.substring(5).trim();
        return replaceVariables(text);
    }
    
    private String executeScriptCd(String line) {
        String path = line.substring(3).trim();
        Directory newDir = resolveDirectory(state, path);
        if (newDir == null) return "cd: no such file or directory: " + path;
        state.setCurrentDirectory(newDir);
        return null;
    }
    
    private String executeLs(String line) {
        String[] parts = CommandParser.parse(line);
        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, args.length);
        Command cmd = CommandFactory.getCommand("ls");
        if (cmd != null) return cmd.execute(state, args);
        return "ls: command not available";
    }
    
    private String executeCat(String line) {
        String[] parts = CommandParser.parse(line);
        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, args.length);
        Command cmd = CommandFactory.getCommand("cat");
        if (cmd != null) return cmd.execute(state, args);
        return "cat: command not available";
    }
    
    private boolean evaluateCondition(String condition) {
        condition = condition.trim();
        if (condition.startsWith("$")) {
            String[] parts = condition.split("=");
            if (parts.length == 2) {
                String varName = parts[0].trim();
                String value = parts[1].trim().replaceAll("^$", ""); // NOTE "^\"|"$"
                String varValue = context.getVariables().getOrDefault(varName, "");
                return varValue.equals(value);
            }
        } else if (condition.startsWith("-")) {
            String[] parts = condition.split(" ");
            if (parts.length == 2) {
                String test = parts[0];
                String path = parts[1].trim().replaceAll("^$", ""); // NOTE "^\"|"$"
                FileNode node = resolveFile(state, path);
                if ("-f".equals(test)) return node != null && node.isFile();
                if ("-d".equals(test)) return node != null && node.isDirectory();
                if ("-e".equals(test)) return node != null;
            }
        }
        return false;
    }
    
    private String replaceVariables(String text) {
        String result = text;
        for (Map.Entry<String, String> entry : context.getVariables().entrySet()) {
            result = result.replace("$" + entry.getKey(), entry.getValue());
        }
        return result;
    }
    
    private FileNode resolveFile(TerminalState state, String fileName) {
        Directory current = state.getCurrentDirectory();
        if (fileName.startsWith("/")) return resolveAbsolutePath(state.getRootDirectory(), fileName);
        return resolveRelativePath(current, fileName);
    }
    
    private Directory resolveDirectory(TerminalState state, String dirName) {
        Directory current = state.getCurrentDirectory();
        if (dirName.startsWith("/")) return resolveAbsoluteDirectory(state.getRootDirectory(), dirName);
        return resolveRelativeDirectory(current, dirName);
    }
    
    private FileNode resolveAbsolutePath(Directory root, String path) {
        String[] parts = path.split("/");
        Directory current = root;
        for (int i = 1; i < parts.length; i++) {
            if (parts[i].isEmpty() || ".".equals(parts[i])) continue;
            if ("..".equals(parts[i])) {
                if (current.getParent() != null) current = current.getParent();
            } else {
                FileNode node = current.getChild(parts[i]);
                if (node == null) return null;
                if (i == parts.length - 1) return node;
                if (!node.isDirectory()) return null;
                current = (Directory) node;
            }
        }
        return current;
    }
    
    private FileNode resolveRelativePath(Directory current, String path) {
        String[] parts = path.split("/");
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].isEmpty() || ".".equals(parts[i])) continue;
            if ("..".equals(parts[i])) {
                if (current.getParent() != null) current = current.getParent();
            } else {
                FileNode node = current.getChild(parts[i]);
                if (node == null) return null;
                if (i == parts.length - 1) return node;
                if (!node.isDirectory()) return null;
                current = (Directory) node;
            }
        }
        return current;
    }
    
    private Directory resolveAbsoluteDirectory(Directory root, String path) {
        String[] parts = path.split("/");
        Directory current = root;
        for (int i = 1; i < parts.length; i++) {
            if (parts[i].isEmpty() || ".".equals(parts[i])) continue;
            if ("..".equals(parts[i])) {
                if (current.getParent() != null) current = current.getParent();
            } else {
                FileNode node = current.getChild(parts[i]);
                if (node == null || !node.isDirectory()) return null;
                current = (Directory) node;
            }
        }
        return current;
    }
    
    private Directory resolveRelativeDirectory(Directory current, String path) {
        String[] parts = path.split("/");
        for (String part : parts) {
            if (part.isEmpty() || ".".equals(part)) continue;
            if ("..".equals(part)) {
                if (current.getParent() != null) current = current.getParent();
            } else {
                FileNode node = current.getChild(part);
                if (node == null || !node.isDirectory()) return null;
                current = (Directory) node;
            }
        }
        return current;
    }
    
    public ScriptContext getContext() { return context; }
    public TerminalState getState() { return state; }
    public void setState(TerminalState state) { this.state = state; }
}
