package gabywald.terminal.script;

import java.util.HashMap;
import java.util.Map;

/**
 * Script execution context with variables and control flow state
 */
public class ScriptContext {
    private Map<String, String> variables;
    private boolean inIfBlock;
    private boolean ifConditionResult;
    private int ifBlockDepth;
    private boolean inWhileBlock;
    private String whileCondition;
    private boolean whileConditionResult;
    private int whileBlockDepth;
    private boolean inForBlock;
    private String forVariable;
    private String[] forValues;
    private int forIndex;
    private int forBlockDepth;
    
    public ScriptContext() {
        this.variables = new HashMap<>();
    }
    
    public Map<String, String> getVariables() { return variables; }
    public void setVariables(Map<String, String> variables) { this.variables = variables; }
    
    public boolean isInIfBlock() { return inIfBlock; }
    public void setInIfBlock(boolean inIfBlock) { this.inIfBlock = inIfBlock; }
    
    public boolean isIfConditionResult() { return ifConditionResult; }
    public void setIfConditionResult(boolean ifConditionResult) { this.ifConditionResult = ifConditionResult; }
    
    public int getIfBlockDepth() { return ifBlockDepth; }
    public void setIfBlockDepth(int ifBlockDepth) { this.ifBlockDepth = ifBlockDepth; }
    
    public boolean isInWhileBlock() { return inWhileBlock; }
    public void setInWhileBlock(boolean inWhileBlock) { this.inWhileBlock = inWhileBlock; }
    
    public String getWhileCondition() { return whileCondition; }
    public void setWhileCondition(String whileCondition) { this.whileCondition = whileCondition; }
    
    public boolean isWhileConditionResult() { return whileConditionResult; }
    public void setWhileConditionResult(boolean whileConditionResult) { this.whileConditionResult = whileConditionResult; }
    
    public int getWhileBlockDepth() { return whileBlockDepth; }
    public void setWhileBlockDepth(int whileBlockDepth) { this.whileBlockDepth = whileBlockDepth; }
    
    public boolean isInForBlock() { return inForBlock; }
    public void setInForBlock(boolean inForBlock) { this.inForBlock = inForBlock; }
    
    public String getForVariable() { return forVariable; }
    public void setForVariable(String forVariable) { this.forVariable = forVariable; }
    
    public String[] getForValues() { return forValues; }
    public void setForValues(String[] forValues) { this.forValues = forValues; }
    
    public int getForIndex() { return forIndex; }
    public void setForIndex(int forIndex) { this.forIndex = forIndex; }
    
    public int getForBlockDepth() { return forBlockDepth; }
    public void setForBlockDepth(int forBlockDepth) { this.forBlockDepth = forBlockDepth; }
    
    public void reset() {
        variables.clear();
        inIfBlock = false;
        ifConditionResult = false;
        ifBlockDepth = 0;
        inWhileBlock = false;
        whileCondition = "";
        whileConditionResult = false;
        whileBlockDepth = 0;
        inForBlock = false;
        forVariable = "";
        forValues = new String[0];
        forIndex = 0;
        forBlockDepth = 0;
    }
}
