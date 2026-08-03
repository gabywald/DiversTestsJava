package gabywald.terminal3.clientside.gui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import gabywald.global.structures.PairSimple;
import gabywald.terminal3.clientside.TerminalClient;
import gabywald.terminal3.serverside.filesystem.TerminalDirectory;
import gabywald.terminal3.serverside.filesystem.TerminalFile;
import gabywald.terminal3.serverside.filesystem.TerminalNode;

/**
 * @author Gabriel Chandesris (2026)
 */
public class TerminalNodeFileExplorerFrame extends JInternalFrame {
	
	private static final long serialVersionUID = 1L;
	
	private final JList<TerminalNode> fileList;
	private TerminalNode currentDirectory;

	public TerminalNodeFileExplorerFrame(TerminalNode directory, JDesktopPane desktopPane) {
		super( (directory != null) ? directory.getName() : "", true, true, true, true);
		this.currentDirectory = directory;

		this.setLayout(new BorderLayout());
		this.setSize(400, 300);

		// Create list to show files / directory
		TerminalNode[] files = (directory != null) ? ((TerminalDirectory)directory).listFiles() : null ;
		if (files == null) { files = new TerminalNode[0]; }

		this.fileList = new JList<TerminalNode>(files);
		this.fileList.setCellRenderer(new TerminalNodeFileIconCellRenderer());
		this.fileList.setVisibleRowCount(10);

		// Add a listener for double-click on file/directory
		this.fileList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					TerminalNode selectedFile = fileList.getSelectedValue();
					if (selectedFile != null) {
						if (selectedFile.isDirectory()) {
							if (selectedFile != null) { actualize(selectedFile); }
						} else {
							// Show a message for files (or open with default editor)
							JOptionPane.showMessageDialog(
								TerminalNodeFileExplorerFrame.this,
								"Selected File: " + selectedFile.getName(),
								"File", JOptionPane.INFORMATION_MESSAGE
							);
						}
					}
				}
			}
		});

		// Add list to JScrollPane
		JScrollPane scrollPane = new JScrollPane(this.fileList);
		this.add(scrollPane, BorderLayout.CENTER);

		// Add button to get level up
		JButton parentButton = new JButton("..");
		parentButton.addActionListener(e -> {
			TerminalNode parentFile = (this.currentDirectory != null) ? this.currentDirectory.getParentFile() : null;
			if (parentFile != null) { this.actualize(parentFile); }
			else {
				/* PairSimple<String, String> cdResult = */TerminalClient.getInstance().callCommandServer("cd ..");
				PairSimple<String, String> pwdResult = TerminalClient.getInstance().callCommandServer("pwd");
            	PairSimple<String, String> lslResult = TerminalClient.getInstance().callCommandServer("ls -l");
            	if ( ! pwdResult.first.equals(this.currentDirectory.getName())) {
	            	TerminalNode tn = new TerminalDirectory(pwdResult.first, null);
	            	Arrays.asList(lslResult.first.split("\n")).stream().forEach( line -> {
	            		char firstChar = line.charAt(0);
	            		int  lastSpace = line.lastIndexOf(" ");
	            		if (firstChar == 'd') {
	            			((TerminalDirectory)tn).addChild(new TerminalDirectory(line.substring(lastSpace), null));
	            		}
	            		if (firstChar == '-') {
	            			((TerminalDirectory)tn).addChild(new TerminalFile(line.substring(lastSpace), null));
	            		}
	            	});
	            	this.currentDirectory.setParent((TerminalDirectory)tn);
	            	this.actualize( tn );
            	}
			}
		});
		this.add(parentButton, BorderLayout.NORTH);
	}
	
	private void actualize(TerminalNode directory) {
		this.currentDirectory = directory;
		
    	TerminalClient.getInstance().callCommandServer("cd " + this.currentDirectory.getName());
    	PairSimple<String, String> lslResult = TerminalClient.getInstance().callCommandServer("ls -l");
    	
    	((TerminalDirectory)directory).clear(); // Only done here ?!
    	
    	Arrays.asList(lslResult.first.split("\n")).stream().forEach( line -> {
    		char firstChar = line.charAt(0);
    		int  lastSpace = line.lastIndexOf(" ");
    		if (firstChar == 'd') {
    			((TerminalDirectory)directory).addChild(new TerminalDirectory(line.substring(lastSpace), null));
    		}
    		if (firstChar == '-') {
    			((TerminalDirectory)directory).addChild(new TerminalFile(line.substring(lastSpace), null));
    		}
    	});
		
		TerminalNode[] files = (directory != null) ? ((TerminalDirectory)directory).listFiles() : null ;
		if (files == null) { files = new TerminalNode[0]; }
		// this.fileList.removeAll();
		this.fileList.setListData(files);
	}
	
}
