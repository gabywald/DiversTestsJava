Test Ideas : Terminal Emulator

A complete Unix/Linux terminal emulator implemented in Java 8 with Swing GUI. (2 versions !)

## Version 1

Features:
- Virtual file system with directories and text files
- 14+ Unix-like commands (ls, cd, pwd, cat, echo, touch, mkdir, rm, rmdir, cp, mv, clear, help, exit)
- Minimalist scripting language with variables, conditions, and loops
- Swing-based graphical user interface
- Command history with up/down arrow navigation
- Basic tab completion
- Comprehensive unit and integration tests (JUnit 5)

Usage:
1. Compile: javac -d bin src/com/terminal/emulator/**/*.java
2. Run: java -cp bin com.terminal.emulator.Main
3. Type 'help' for available commands
4. Type 'exit' to quit

Project Structure:
com.terminal/
  - Main.java (entry point)
  - TerminalState.java (global state)
  - gui/ (Swing GUI classes)
  - filesystem/ (Directory, TerminalFile, FileNode)
  - commands/ (Command interface and implementations)
  - script/ (ScriptEngine, ScriptParser, ScriptContext)

tests/
  - filesystem/ (FileSystemTest, DirectoryTest)
  - commands/ (CommandParserTest, LsCommandTest, CdCommandTest, CommandFactoryTest)
  - TerminalStateTest.java
  - ScriptEngineTest.java
  - IntegrationTest.java

```
Commands Available:
ls [OPTIONS] [FILE...]    - List directory contents
cd [DIR]                - Change directory
pwd                    - Print working directory
cat [FILE...]           - Concatenate and print files
echo [STRING...]        - Display a line of text
touch [FILE...]         - Create empty files
mkdir [DIRECTORY...]    - Create directories
rm [FILE...]            - Remove files
rmdir [DIRECTORY...]    - Remove empty directories
cp SOURCE DEST          - Copy files
mv SOURCE DEST          - Move or rename files
clear                  - Clear the terminal screen
help [COMMAND]          - Display help information
exit                   - Exit the terminal

Scripting Language:
# Variables
set var=value
echo $var

# Conditions
if [ "$var" = "value" ]
  echo Equal
fi

# File tests
if [ -f "file.txt" ]
  echo File exists
fi

# While loops
set count=0
while [ "$count" != "5" ]
  echo $count
  # Note: Manual incrementation needed in this version
  set count=1
done

# For loops
for item in a b c
  echo $item
done

Note: The scripting engine is minimal and does not support all Unix shell features.
```

## Version 2

Adding rediretiuons, pipes, better scripting... !

...