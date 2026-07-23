package gabywald.terminal3.clientside.gui;

import java.util.List;

import javax.swing.Icon;
import javax.swing.UIManager;

import gabywald.terminal3.serverside.filesystem.TerminalDirectory;
import gabywald.terminal3.serverside.filesystem.TerminalNode;

/**
 * @author Gabriel Chandesris (2026)
 */
public class TerminalNodeFileSystemView {
    private final Icon directoryIcon;
    private final Icon fileIcon;

    public TerminalNodeFileSystemView() {
        this.directoryIcon = UIManager.getIcon("FileView.directoryIcon");
        this.fileIcon = UIManager.getIcon("FileView.fileIcon");
    }

    public Icon getSystemIcon(TerminalNode node) {
        return node.isDirectory() ? this.directoryIcon : this.fileIcon; // node.getIcon();
    }

    public String getSystemDisplayName(TerminalNode node) {
        return node.getName();
    }

    public List<TerminalNode> getChildren(TerminalDirectory directory) {
        return directory.getChildren();
    }

    public boolean isDirectory(TerminalNode node) {
        return node.isDirectory();
    }
}
