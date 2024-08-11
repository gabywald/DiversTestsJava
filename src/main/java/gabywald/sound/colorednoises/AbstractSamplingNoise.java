package gabywald.sound.colorednoises;

import java.nio.ByteBuffer;
import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * 
 * @author gchandesris
 *
 */
public abstract class AbstractSamplingNoise implements IColoredNoise, Runnable {
	protected AbstractSamplingNoiseConfig BASE_CONFIG = new AbstractSamplingNoiseConfig();
    
    protected AudioFormat format = null;
    protected DataLine.Info info = null;
    protected SourceDataLine line = null;
    protected ByteBuffer buffer = null;
    protected Random random = null;
    
    private boolean isActiv = true;
    protected boolean isActiv() { return this.isActiv; }
    public void isActiv(boolean isActiv) { this.isActiv = isActiv; }
    
    boolean init() {
	    this.format = new AudioFormat(
	    		BASE_CONFIG.SAMPLE_RATE,
	    		BASE_CONFIG.BITS,
	    		BASE_CONFIG.CHANNELS,
	        true, // signed
	        true  // big endian
	    );
	    this.info = new DataLine.Info(
	        SourceDataLine.class,
	        this.format,
	        BASE_CONFIG.PACKET_SIZE * 2
	    );
	
	    try {
	    	this.line = (SourceDataLine)AudioSystem.getLine(this.info);
	    	this.line.open(this.format);
	    }
	    catch (LineUnavailableException e) {
	        // e.printStackTrace();
	        // return false;
	    	this.line = null;
	    }
	
	    this.line.start();
	    Runtime.getRuntime().addShutdownHook(new Thread(() -> { this.line.close(); }));
	    this.buffer = ByteBuffer.allocate(BASE_CONFIG.PACKET_SIZE);
	    this.random = new Random();
	    
	    return (this.line != null);
    }
    
    
}
