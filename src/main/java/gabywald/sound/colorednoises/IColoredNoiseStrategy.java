package gabywald.sound.colorednoises;

/**
 * 
 * @author Gabriel Chandesris (2024)
 */
public class IColoredNoiseStrategy {
	
	public enum NoiseColors {
		WHITE(new ColoredNoiseWhite(), 	 "White Noise"), 
		PINK(new ColoredNoisePink(), 	 "Pink Noise"), 
		/** NOTE : red = brownian / brown */
		BROWN(new ColoredNoiseBrown(), 	 "Brown Noise"), 
		/** NOTE : azur = blue */
		BLUE(new ColoredNoiseBlue(), 	 "Blue Noise"), 
		/** NOTE purple = violet */
		VIOLET(new ColoredNoiseViolet(), "Violet Noise"), 
		GREY(new ColoredNoiseGrey(), 	 "Grey Noise");
		
		private IColoredNoise localICN = null;
		private String name = null;
		
		private NoiseColors(IColoredNoise icn, String name) { 
			this.localICN = icn;
			this.name = name;
		}
		
		public IColoredNoise getColoredNoise() { return this.localICN; }
		public String getName() { return this.name; }
	}

}
