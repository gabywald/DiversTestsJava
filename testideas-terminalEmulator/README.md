Test Ideas : Terminal Emulator

A complete Unix/Linux terminal emulator implemented in Java 8 with Swing GUI. (3 versions !)
 - Graphical Implementation (Java Swing) ; 
 - Local implementation ; 
 - REST implementation (servers + clients) ; 
 - WebSocket implemntation (servers + clients) ; 
 - Java 8 ; Maven 3.6.3 ; Eclipse 2025-2026 ; 
 - Comprehensive unit and integration tests (JUnit 5) ; 
 

## Main ideas and implementations

Features:
- Virtual file system with directories and text files
- 14+ Unix-like commands (ls, cd, pwd, cat, echo, touch, mkdir, rm, rmdir, cp, mv, clear, help, exit)
- Minimalist scripting language with variables, conditions, and loops
- Swing-based graphical user interface
- Command history with up/down arrow navigation
- Adding redirections, pipes, better scripting... !

Usage:
1. Compile It ! 
2. Run It ! (see optios)
3. Type 'help' for available commands
4. Enjoy !
5. Type 'exit' to quit


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

...

## CLI launch options

```
Usage: testideas-terminalemulator [-hV] (-v1 | -v2 | -v3) [-c | -s | -b] [-r |
                                  -w] [--warn | --trace | --none | --debug |
                                  --info | --error]
Application CLI with picocli.
  -h, --help            Show this help message and exit.
  -V, --version         Print version information and exit.
Version Options
      -v1, --version1   First Version of Terminal Emulator.
      -v2, --version2   Second Version of Terminal Emulator.
      -v3, --version3   Third Version of Terminal Emulator.
(Apply on v3 only) Client/Server Options
  -b, --both            (Apply on v3 only) Server and Client Execution.
  -c, --onlyclient      (Apply on v3 only) Only Client Execution.
  -s, --onlyserver      (Apply on v3 only) Only Server Execution.
(Apply on v3 only) REST/WebSocket Options
  -r, --rest            (Apply on v3 only) Client / Server exchanges on REST
                          mode.
  -w, --websocket       (Apply on v3 only) Client / Server exchanges on Web
                          Socket mode.
Log Level Options
      --debug           Sets log level to DEBUG.
      --error           Sets log level to ERROR.
      --info            Sets log level to INFO.
      --none            Sets log level to NONE.
      --trace           Sets log level to NONE.
      --warn            Sets log level to WARN.

```

## Basic Launcher

```
java -jar testideas-terminalEmulator/target/testideas-terminalEmulator-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```


