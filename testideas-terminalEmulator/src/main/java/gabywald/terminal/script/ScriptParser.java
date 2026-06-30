package gabywald.terminal.script;

import java.util.ArrayList;
import java.util.List;

/**
 * Script parser for syntax analysis
 */
public class ScriptParser {
    public static List<ScriptBlock> parse(String script) {
        List<ScriptBlock> blocks = new ArrayList<>();
        if (script == null || script.trim().isEmpty()) return blocks;
        
        String[] lines = script.split("\n");
        ScriptBlock currentBlock = new ScriptBlock(ScriptBlock.Type.NORMAL, 0);
        int depth = 0;
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty() || line.startsWith("#")) {
                if (currentBlock.getType() != ScriptBlock.Type.NORMAL) {
                    currentBlock.addLine(line);
                }
                continue;
            }
            
            if (line.startsWith("if ")) {
                if (currentBlock.getType() != ScriptBlock.Type.NORMAL) blocks.add(currentBlock);
                currentBlock = new ScriptBlock(ScriptBlock.Type.IF, i);
                currentBlock.addLine(line);
                depth++;
            } else if (line.startsWith("while ")) {
                if (currentBlock.getType() != ScriptBlock.Type.NORMAL) blocks.add(currentBlock);
                currentBlock = new ScriptBlock(ScriptBlock.Type.WHILE, i);
                currentBlock.addLine(line);
                depth++;
            } else if (line.startsWith("for ")) {
                if (currentBlock.getType() != ScriptBlock.Type.NORMAL) blocks.add(currentBlock);
                currentBlock = new ScriptBlock(ScriptBlock.Type.FOR, i);
                currentBlock.addLine(line);
                depth++;
            } else if (line.equals("fi") || line.equals("done") || line.equals("end")) {
                currentBlock.addLine(line);
                blocks.add(currentBlock);
                depth--;
                if (depth >= 0) currentBlock = new ScriptBlock(ScriptBlock.Type.NORMAL, i);
            } else {
                currentBlock.addLine(line);
            }
        }
        
        if (!currentBlock.getLines().isEmpty()) blocks.add(currentBlock);
        return blocks;
    }
    
    public static boolean validateSyntax(String script) {
        if (script == null) return false;
        String[] lines = script.split("\n");
        int ifDepth = 0, whileDepth = 0, forDepth = 0;
        
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.startsWith("if ")) ifDepth++;
            else if (trimmed.equals("fi")) ifDepth--;
            else if (trimmed.startsWith("while ")) whileDepth++;
            else if (trimmed.equals("done")) {
                if (whileDepth > 0) whileDepth--;
                else if (forDepth > 0) forDepth--;
            }
            else if (trimmed.startsWith("for ")) forDepth++;
            else if (trimmed.equals("end")) forDepth--;
        }
        return ifDepth == 0 && whileDepth == 0 && forDepth == 0;
    }
    
    public static class ScriptBlock {
        public enum Type { NORMAL, IF, WHILE, FOR }
        private Type type;
        private int startLine;
        private List<String> lines;
        
        public ScriptBlock(Type type, int startLine) {
            this.type = type;
            this.startLine = startLine;
            this.lines = new ArrayList<>();
        }
        
        public void addLine(String line) { lines.add(line); }
        public Type getType() { return type; }
        public int getStartLine() { return startLine; }
        public List<String> getLines() { return lines; }
        public String getContent() { return String.join("\n", lines); }
    }
}
