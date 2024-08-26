package gabywald.sound.colorednoises;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import gabywald.sound.colorednoises.IColoredNoiseStrategy.NoiseColors;

/**
 * 
 * @author Gabriel Chandesris (2024)
 */
@SuppressWarnings("serial")
public class GColoredNoisesFrame extends JFrame {
	
	GColoredNoisesFrame() {
		this.setTitle("Colored Noise Generator");
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(100, 100, 120 * NoiseColors.values().length, 70);
		this.setLocationRelativeTo(null);
		this.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 1, 1));
		for (int i = 0 ; i < NoiseColors.values().length ; i++) {
			NoiseColors current = NoiseColors.values()[i];
			JButton toAddButton = new JButton(current.getName());
			toAddButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					for (int i = 0 ; i < NoiseColors.values().length ; i++) 
						{ NoiseColors.values()[i].getColoredNoise().isActiv(false); }
					if (e.getSource() == toAddButton) {
						Thread newThread = new Thread(current.getColoredNoise());
						current.getColoredNoise().isActiv(true);
						newThread.start();
					}
				}
			});
			this.getContentPane().add(toAddButton);
		}
	}

	public static void main(String[] args) {
		GColoredNoisesFrame gFrame = new GColoredNoisesFrame();
		gFrame.setVisible(true);
	}
	
}
