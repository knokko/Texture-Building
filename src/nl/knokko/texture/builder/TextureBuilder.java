package nl.knokko.texture.builder;

import java.awt.image.BufferedImage;

import nl.knokko.texture.builder.drawing.GeometryDrawer;
import nl.knokko.texture.color.Color;
import nl.knokko.texture.color.SimpleRGBAColor;

public abstract class TextureBuilder {
	
	public final int width, height;
	public boolean hasAlpha;
	
	public final GeometryDrawer geometry;
	
	public TextureBuilder(int width, int height, boolean hasAlpha) {
		this.width = width;
		this.height = height;
		this.hasAlpha = hasAlpha;
		
		this.geometry = createGeometryDrawer();
	}
	
	protected GeometryDrawer createGeometryDrawer() {
		return new GeometryDrawer(this);
	}
	
	public abstract void setPixel(int x, int y, byte red, byte green, byte blue, byte alpha);
	
	public void setPixel(int x, int y, byte red, byte green, byte blue) {
		setPixel(x, y, red, green, blue, (byte) 255);
	}
	
	public void setPixel(int x, int y, Color color) {
		setPixel(x, y, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}
	
	public abstract byte getRed(int x, int y);
	
	public abstract byte getGreen(int x, int y);
	
	public abstract byte getBlue(int x, int y);
	
	public abstract byte getAlpha(int x, int y);
	
	public Color getPixel(int x, int y) {
		return SimpleRGBAColor.fromBytes(getRed(x, y), getGreen(x, y), getBlue(x, y), getAlpha(x, y));
	}
	
	public abstract BufferedImage createBufferedImage();
}