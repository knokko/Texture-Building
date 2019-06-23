package nl.knokko.test;

import nl.knokko.texture.builder.ByteArrayTextureBuilder;
import nl.knokko.texture.builder.TextureBuilder;
import nl.knokko.texture.color.Color;
import nl.knokko.texture.color.SimpleRGBColor;

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
		texture.setPixel(47, 23, (byte) 30, (byte) 67, (byte) 178);
		texture.setPixel(94, 41, SimpleRGBColor.fromInts(200, 101, 102));
		
		assert texture.getRed(47, 23) == 30;
		assert texture.getGreen(47, 23) == 67;
		assert texture.getBlue(47, 23) == (byte) 178;
		assert texture.getAlpha(47, 23) == -1;
		assert texture.getRed(94, 41) == (byte) 200;
		assert texture.getGreen(94, 41) == 101;
		assert texture.getBlue(94, 41) == 102;
		assert texture.getAlpha(94, 41) == (byte) 255;
		
		// Now the line operations
		texture.geometry().drawHorizontalLine(10, 20, 15, SimpleRGBColor.fromInts(200, 50, 230));
		assert texture.getRed(10, 15) == (byte) 200;
		assert texture.getGreen(15, 15) == 50;
		assert texture.getBlue(10, 15) == (byte) 230;
		
		// Now the graphical test which needs to be done by hand
		texture.saveTestImage("byteArray");
	}
}
