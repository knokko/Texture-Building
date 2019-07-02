package nl.knokko.texture.builder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import nl.knokko.texture.builder.drawing.*;
import nl.knokko.texture.color.Color;
import nl.knokko.texture.color.SimpleRGBAColor;

public interface TextureBuilder {
	
	int width();
	
	int height();
	
	boolean useAlpha();
	
	GeometryDrawer geometry();
	
	MaterialDrawer materials();
	
	AverageDrawer average();
	
	DecayingDrawer decaying();
	
	void setPixel(int x, int y, byte red, byte green, byte blue, byte alpha);
	
	default void setPixel(int x, int y, byte red, byte green, byte blue) {
		setPixel(x, y, red, green, blue, (byte) 255);
	}
	
	default void setPixel(int x, int y, Color color) {
		setPixel(x, y, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}
	
	public abstract byte getRed(int x, int y);
	
	public abstract byte getGreen(int x, int y);
	
	public abstract byte getBlue(int x, int y);
	
	public abstract byte getAlpha(int x, int y);
	
	default Color getPixel(int x, int y) {
		return SimpleRGBAColor.fromBytes(getRed(x, y), getGreen(x, y), getBlue(x, y), getAlpha(x, y));
	}
	
	BufferedImage createBufferedImage();
	
	byte[] createArrrayRGBA();
	
	default void saveTestImage(String name) {
		try {
			ImageIO.write(createBufferedImage(), "PNG", new File(name + ".png"));
		} catch (IOException ioex) {
			throw new RuntimeException(ioex);
		}
	}
}