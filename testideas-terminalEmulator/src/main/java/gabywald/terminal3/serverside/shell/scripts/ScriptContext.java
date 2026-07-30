package gabywald.terminal3.serverside.shell.scripts;

import java.util.HashMap;
import java.util.Map;

/**
 * Script execution context with variables and control flow state
 * @author Gabriel Chandesris (2026)
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
    
    public ScriptContext() 
    	{ this.variables = new HashMap<String, String>(); }
    
    public Map<String, String> getVariables() { return this.variables; }
    public void setVariables(Map<String, String> variables) 
    	{ this.variables = variables; }
    
    public boolean isInIfBlock() { return this.inIfBlock; }
    public void setInIfBlock(boolean inIfBlock) 
    	{ this.inIfBlock = inIfBlock; }
    
    public boolean isIfConditionResult() { return this.ifConditionResult; }
    public void setIfConditionResult(boolean ifConditionResult) 
    	{ this.ifConditionResult = ifConditionResult; }
    
    public int getIfBlockDepth() { return this.ifBlockDepth; }
    public void setIfBlockDepth(int ifBlockDepth) 
    	{ this.ifBlockDepth = ifBlockDepth; }
    
    public boolean isInWhileBlock() { return this.inWhileBlock; }
    public void setInWhileBlock(boolean inWhileBlock) 
    	{ this.inWhileBlock = inWhileBlock; }
    
    public String getWhileCondition() { return this.whileCondition; }
    public void setWhileCondition(String whileCondition) 
    	{ this.whileCondition = whileCondition; }
    
    public boolean isWhileConditionResult() { return this.whileConditionResult; }
    public void setWhileConditionResult(boolean whileConditionResult) 
    	{ this.whileConditionResult = whileConditionResult; }
    
    public int getWhileBlockDepth() { return this.whileBlockDepth; }
    public void setWhileBlockDepth(int whileBlockDepth) 
    	{ this.whileBlockDepth = whileBlockDepth; }
    
    public boolean isInForBlock() { return this.inForBlock; }
    public void setInForBlock(boolean inForBlock) 
    	{ this.inForBlock = inForBlock; }
    
    public String getForVariable() { return this.forVariable; }
    public void setForVariable(String forVariable) 
    	{ this.forVariable = forVariable; }
    
    public String[] getForValues() { return this.forValues; }
    public void setForValues(String[] forValues) 
    	{ this.forValues = forValues; }
    
    public int getForIndex() { return this.forIndex; }
    public void setForIndex(int forIndex) 
    	{ this.forIndex = forIndex; }
    
    public int getForBlockDepth() { return this.forBlockDepth; }
    public void setForBlockDepth(int forBlockDepth) 
    	{ this.forBlockDepth = forBlockDepth; }
    
    public void reset() {
    	this.variables.clear();
    	this.inIfBlock = false;
    	this.ifConditionResult = false;
    	this.ifBlockDepth = 0;
    	this.inWhileBlock = false;
    	this.whileCondition = "";
    	this.whileConditionResult = false;
    	this.whileBlockDepth = 0;
    	this.inForBlock = false;
    	this.forVariable = "";
    	this.forValues = new String[0];
    	this.forIndex = 0;
    	this.forBlockDepth = 0;
    }
    
}
