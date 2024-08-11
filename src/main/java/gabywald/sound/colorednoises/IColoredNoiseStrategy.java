package gabywald.sound.colorednoises;

/**
 * 
 * @author gchandesris
 *
 */
public class IColoredNoiseStrategy {
	
	public enum NoiseColors {
		WHITE(new ColoredNoiseWhite()), 
		PINK(new ColoredNoisePink()), 
		/** NOTE : red = brownian / brown */
		BROWN(new ColoredNoiseBrown()), 
		/** NOTE : azur = blue */
		BLUE(new ColoredNoiseBlue()), 
		/** NOTE purple = violet */
		VIOLET(new ColoredNoiseViolet()), 
		GREY(new ColoredNoiseGrey());
		
		private IColoredNoise localICN = null;
		
		private NoiseColors(IColoredNoise icn) { this.localICN = icn; }
		
		public IColoredNoise getColoredNoise() { return this.localICN; }
	}

}
