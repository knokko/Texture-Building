package nl.knokko.texture.builder.drawing;

import java.util.Random;

import nl.knokko.texture.builder.TextureBuilder;
import nl.knokko.texture.color.Color;
import nl.knokko.texture.color.SimpleRGBAColor;
import nl.knokko.texture.color.SimpleRGBColor;

public class AverageDrawer {
	
	public static Color getDifColor(Random random, Color basic, double maxDifference) {
		return getMultipliedColor(basic, 1.0 - maxDifference + random.nextDouble() * maxDifference * 2);
	}

	public static Color getMultipliedColor(Color basic, double factor) {
		if (basic.getAlphaI() == 255)
			return SimpleRGBColor.fromDoubles(basic.getRedD() * factor, basic.getGreenD() * factor, basic.getBlueD() * factor);
		else
			return SimpleRGBAColor.fromDoubles(basic.getRedD() * factor, basic.getGreenD() * factor, basic.getBlueD() * factor, basic.getAlphaD());
	}
	
	protected final TextureBuilder texture;
	protected final int width, height;
	
	public AverageDrawer(TextureBuilder textureBuilder) {
		texture = textureBuilder;
		width = texture.width();
		height = texture.height();
	}
	
	public void fillAverage(int minX, int minY, int maxX, int maxY, Color color, double maxDifference, Random random) {
		for (int x = minX; x <= maxX; x++)
			for (int y = minY; y <= maxY; y++)
				texture.setPixel(x, y, getDifColor(random, color, maxDifference));
	}

	public void fillAverageChance(int minX, int minY, int maxX, int maxY, Color color, double maxDifference,
			Random random, double chance) {
		for (int x = minX; x <= maxX; x++)
			for (int y = minY; y <= maxY; y++)
				if (random.nextDouble() < chance)
					texture.setPixel(x, y, getDifColor(random, color, maxDifference));
	}
}
