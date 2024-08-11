package gabywald.sound.colorednoises;

/**
 * 
 * @author gchandesris
 *
 */
class ColoredNoiseWhite extends AbstractSamplingNoise {
	
	ColoredNoiseWhite() { ; }

	@Override
	public void run() {
		if ( ! this.init()) { return; }

		while(super.isActiv()) {
           super.buffer.clear();

            for (int i = 0; i < super.BASE_CONFIG.PACKET_SIZE / super.BASE_CONFIG.SAMPLE_SIZE; i++) {
            	super.buffer.putShort((short)(super.random.nextGaussian() * Short.MAX_VALUE));
            }

            super.line.write(super.buffer.array(), 0, super.buffer.position());
        }
		
	}

}
