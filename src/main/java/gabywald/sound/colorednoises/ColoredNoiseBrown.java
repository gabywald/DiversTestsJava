package gabywald.sound.colorednoises;

/**
 * 
 * @author gchandesris
 *
 */
class ColoredNoiseBrown extends AbstractSamplingNoise {
	
	ColoredNoiseBrown() { ; }

	@Override
	public void run() {
		if ( ! this.init()) { return; }

        double lastOut = 0.0;

        while(super.isActiv()) {
            super.buffer.clear();

            for (int i = 0 ; i < super.BASE_CONFIG.PACKET_SIZE / super.BASE_CONFIG.SAMPLE_SIZE ; i++) {
                double white = super.random.nextGaussian();
                double output = (lastOut + (0.02 * white)) / 1.02;
                lastOut = output;
                output *= 1.5; // (roughly) compensate for gain
                super.buffer.putShort((short)(output * Short.MAX_VALUE));
            }

            super.line.write(super.buffer.array(), 0, super.buffer.position());
        }
		
	}

}
