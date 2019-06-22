package nl.knokko.test;

import nl.knokko.texture.builder.ByteArrayTextureBuilder;
import nl.knokko.texture.builder.TextureBuilder;
import nl.knokko.texture.color.Color;

public class BasicTextureTester {

	public static void main(String[] args) {
		testByteArrayTextureBuilder();
	}
	
	static void testByteArrayTextureBuilder() {
		TextureBuilder texture = new ByteArrayTextureBuilder(100, 100, false);
		
		// Test basic get operations
		assert texture.getRed(20, 45) == 0;
		assert texture.getGreen(47, 21) == 0;
		assert texture.getBlue(76, 91) == 0;
		assert texture.getAlpha(84, 17) == -1;
		
		{
			Color color = texture.getPixel(37, 53);
			assert color.getRed() == 0;
			assert color.getGreen() == 0;
			assert color.getBlue() == 0;
			assert color.getAlpha() == -1;
		}
		
		// Test basic set operations
	}
}
