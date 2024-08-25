package gabywald.sound;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.ByteBuffer;
import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JFrame;

/**
 * https://stackoverflow.com/questions/26963342/generating-colors-of-noise-in-java
 */
@SuppressWarnings("serial")
public class WhiteNoise extends JFrame {

	private GeneratorThread generatorThread;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WhiteNoise frame = new WhiteNoise();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public WhiteNoise() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				generatorThread.exit();
				System.exit(0);
			}
		});

		this.setTitle("White Noise Generator");
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(100, 100, 200, 50);
		this.setLocationRelativeTo(null);
		this.getContentPane().setLayout(new BorderLayout(0, 0));
		this.generatorThread = new GeneratorThread();
		this.generatorThread.start();
	}

	class GeneratorThread extends Thread {

		final static public int SAMPLE_SIZE = 2;
		final static public int PACKET_SIZE = 5000;

		SourceDataLine line;
		public boolean exitExecution = false;

		public void run() {

			try {
				AudioFormat format = new AudioFormat(44100, 16, 1, true, true);
				DataLine.Info info = new DataLine.Info(SourceDataLine.class, format, GeneratorThread.PACKET_SIZE * 2);

				if (!AudioSystem.isLineSupported(info)) {
					throw new LineUnavailableException();
				}

				this.line = (SourceDataLine)AudioSystem.getLine(info);
				this.line.open(format);
				this.line.start();
			} catch (LineUnavailableException e) {
				e.printStackTrace();
				System.exit(-1);
			}

			ByteBuffer buffer = ByteBuffer.allocate(GeneratorThread.PACKET_SIZE);

			Random random = new Random();
			while (this.exitExecution == false) {
				buffer.clear();
				for (int i = 0 ; i < GeneratorThread.PACKET_SIZE / GeneratorThread.SAMPLE_SIZE ; i++) {
					buffer.putShort((short) (random.nextGaussian() * Short.MAX_VALUE));
				}
				this.line.write(buffer.array(), 0, buffer.position());
			}

			this.line.drain();
			this.line.close();
		}

		public void exit() { this.exitExecution = true; }
	}
}
