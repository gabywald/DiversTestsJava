package gabywald.sound.colorednoises;

/**
 * 
 * @author Gabriel Chandesris (2024)
 */
class ColoredNoisePink extends AbstractSamplingNoise {
	
	ColoredNoisePink() { ; }

	@Override
	public void run() {
		if ( ! this.init()) { return; }

        double b0 = 0.0;
        double b1 = 0.0;
        double b2 = 0.0;
        double b3 = 0.0;
        double b4 = 0.0;
        double b5 = 0.0;
        double b6 = 0.0;        

        while(super.isActiv()) {
        	super.buffer.clear();

            for (int i = 0 ; i < super.BASE_CONFIG.PACKET_SIZE / super.BASE_CONFIG.SAMPLE_SIZE ; i++) {
                double white = super.random.nextGaussian();
                b0 = 0.99886 * b0 + white * 0.0555179;
                b1 = 0.99332 * b1 + white * 0.0750759;
                b2 = 0.96900 * b2 + white * 0.1538520;
                b3 = 0.86650 * b3 + white * 0.3104856;
                b4 = 0.55000 * b4 + white * 0.5329522;
                b5 = -0.7616 * b5 - white * 0.0168980;
                double output = b0 + b1 + b2 + b3 + b4 + b5 + b6 + white * 0.5362;
                output *= 0.05; // (roughly) compensate for gain
                b6 = white * 0.115926;
                super.buffer.putShort((short)(output * Short.MAX_VALUE));
            }

            super.line.write(super.buffer.array(), 0, super.buffer.position());
        }
		
	}

}
