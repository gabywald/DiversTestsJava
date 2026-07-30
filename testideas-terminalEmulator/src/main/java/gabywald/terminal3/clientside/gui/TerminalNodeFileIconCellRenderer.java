package gabywald.terminal3.clientside.gui;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import gabywald.terminal3.serverside.filesystem.TerminalNode;

/**
 * @author Gabriel Chandesris (2026)
 */
public class TerminalNodeFileIconCellRenderer extends DefaultListCellRenderer {
	
	private static final long serialVersionUID = 1L;
	
	// private final FileSystemView fileSystemView;
	private final TerminalNodeFileSystemView fileSystemView;

	public TerminalNodeFileIconCellRenderer() {
		// this.fileSystemView = FileSystemView.getFileSystemView();
		this.fileSystemView = new TerminalNodeFileSystemView();
	}

	@Override
	public Component getListCellRendererComponent(
			JList<?> list, Object value, int index,
			boolean isSelected, boolean cellHasFocus) {

		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		if (value instanceof TerminalNode) {
			TerminalNode file = (TerminalNode) value;
			this.setIcon(this.fileSystemView.getSystemIcon(file));
			this.setText(this.fileSystemView.getSystemDisplayName(file));
		}

		return this;
	}
}
