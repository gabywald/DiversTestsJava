package gabywald.terminal;

import gabywald.terminal.gui.TerminalFrame;

/**
 * Main entry point for the terminal emulator application.
 * @author Gabriel Chandesris (2026)
 * @deprecated Use {@code gabywald.launchers.TerminalEmulatorLaunchers}
 */
public class MainTerminal {
    public static void main(String[] args) {
        TerminalFrame frame = new TerminalFrame();
        frame.setVisible(true);
    }
}