package nl.knokko.texture.builder.drawing;

import nl.knokko.texture.builder.TextureBuilder;
import nl.knokko.texture.color.Color;
import nl.knokko.texture.color.SimpleRGBAColor;

/**
 * Instances of this class can be used to draw simple geometrical shapes on texture builders.
 * It should be available on all implementations of TextureBuilder and can be accessed by calling
 * the geometry() method of the texture builder to draw on.
 * @author knokko
 *
 */
public class GeometryDrawer {
	
	protected final TextureBuilder texture;
	
	protected final int width, height;
	
	/**
	 * Constructs a new GeometryDrawer. Only one instance of GeometryDrawer should be created per instance
	 * of TextureBuilder (preferable within the constructor of the TextureBuilder). It can then be accessed
	 * by calling the geometry() method of the corresponding texture builder.
	 * @param texture The texture builder that this GeometryDrawer should draw on
	 */
	public GeometryDrawer(TextureBuilder texture) {
		this.texture = texture;
		this.width = texture.width();
		this.height = texture.height();
	}
	
	/**
	 * Draws a horizontal line between the points (minX,y) and (maxX,y) with the given color.
	 * @param minX The minimum x-coordinate where the line should be drawn (should be smaller than maxX)
	 * @param maxX The maximum x-coordinate, where the line should be drawn (should be larger than minX)
	 * @param y The y-coordinate of all points on the line to draw
	 * @param red The red component of the color (the value in range 0 to 255 casted down to byte)
	 * @param green The green component of the color (the value in range 0 to 255 casted down to byte)
	 * @param blue The blue component of the color (the value in range 0 to 255 casted down to byte)
	 * @param alpha The alpha component of the color (the value in range 0 to 255 casted down to byte)
	 */
	public void drawHorizontalLine(int minX, int maxX, int y, byte red, byte green, byte blue, byte alpha) {
		
		// Prevent division by 0
		if (alpha == 0)
			return;
		
		// If the color is not transparent, do it the quick and easy way
		if (alpha == -1) {
			for (int x = minX; x <= maxX; x++)
				texture.setPixel(x, y, red, green, blue, alpha);
		} else {
			
			// The color is transparent, so do it the hard way...
			float lineFactor = (alpha & 0xFF) / 255f;
			
			float lineRed = (red & 0xFF) * lineFactor / 255f;
			float lineGreen = (green & 0xFF) * lineFactor / 255f;
			float lineBlue = (blue & 0xFF) * lineFactor / 255f;
			
			for (int x = minX; x <= maxX; x++) {
				Color oldColor = texture.getPixel(x, y);
				
				// Let's hope its the easy way
				if (oldColor.getAlpha() == 0) {
					texture.setPixel(x, y, red, green, blue, alpha);
				} else {
					
					// Or do it the hard way...
					texture.setPixel(x, y, mixColors(lineFactor, lineRed, lineGreen, lineBlue, texture.getPixel(x, y)));
				}
			}
		}
	}
	
	private Color mixColors(float lineFactor, float lineRed, float lineGreen, float lineBlue, Color old) {
		float oldFactor = old.getAlphaF();
		float factorSum = lineFactor + oldFactor;
		float finalFactor = 1f / factorSum;
		return SimpleRGBAColor.fromFloats(
				(lineRed + old.getRedF() * oldFactor) * finalFactor, 
				(lineGreen + old.getGreenF() * oldFactor) * finalFactor, 
				(lineBlue + old.getBlueF() * oldFactor) * finalFactor, factorSum);
	}
	
	/**
	 * Draws a horizontal line between the points (minX,y) and (maxX,y) with the given color.
	 * @param minX The minimum x-coordinate where the line should be drawn (should be smaller than maxX)
	 * @param maxX The maximum x-coordinate, where the line should be drawn (should be larger than minX)
	 * @param y The y-coordinate of all points on the line to draw
	 * @param color The color of the line to draw
	 */
	public void drawHorizontalLine(int minX, int maxX, int y, Color color) {
		drawHorizontalLine(minX, maxX, y, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}
	
	public void drawVerticalLine(int minY, int maxY, int x, byte red, byte green, byte blue, byte alpha) {
		for (int y = minY; y <= maxY; y++)
			texture.setPixel(x, y, red, green, blue, alpha);
	}

	public void drawVerticalLine(int minY, int maxY, int x, Color color) {
		drawVerticalLine(minY, maxY, x, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	public void drawLine(int startX, int startY, int endX, int endY, Color color) {

		// This case can be dealt with quicker than the usual case
		if (startX == endX) {
			if (endY > startY) {
				drawVerticalLine(startY, endY, startX, color);
			} else {
				drawVerticalLine(endY, startY, startX, color);
			}
		}

		// Same for this case
		if (startY == endY) {
			if (endX > startX) {
				drawHorizontalLine(startX, endX, startY, color);
			} else {
				drawHorizontalLine(endX, startX, startY, color);
			}
		}

		int minX, maxX;
		if (endX > startX) {
			minX = startX;
			maxX = endX;
		} else {
			minX = endX;
			maxX = startX;
		}

		int minY, maxY;
		if (endY > startY) {
			minY = startY;
			maxY = endY;
		} else {
			minY = endY;
			maxY = startY;
		}
		int distanceX = maxX - minX;
		int distanceY = maxY - minY;

		// We use two cases so that no points get skipped in case of a large slope
		if (distanceX >= distanceY) {

			/*
			 * Make use of the formula y = ax + b
			 * 
			 * Use a = (endY - startY) / (endX - startX)
			 * 
			 * Then it must hold that a*startX + b = startY so b = startY - a*startX
			 */
			double a = (double) (endY - startY) / (endX - startX);
			double b = startY - a * startX;

			// Don't go outside texture bounds
			if (minX < 0)
				minX = 0;
			if (maxX >= width)
				maxX = width - 1;

			// Now just loop over all x's
			for (int x = minX; x <= maxX; x++) {
				double y = a * x + b;

				// TODO anti-aliasing?
				// int lowY = (int) y;
				// int highY = lowY + 1;
				
				int roundedY = (int) (y + 0.5);
				if (roundedY >= 0 && roundedY < height)
					texture.setPixel(x, roundedY, color);
			}
		} else {

			/*
			 * Make use of the formula x = ay + b
			 * 
			 * Use a = (endX - startX) / (endY - startY)
			 * 
			 * Then it must hold that a*startY + b = startX so b = startX - a*startY
			 */

			double a = (double) (endX - startX) / (endY - startY);
			double b = startX - a * startY;

			// Don't go outside texture bounds
			if (minY < 0)
				minY = 0;
			if (maxY >= height)
				maxY = height - 1;

			// Now just loop over all y's
			for (int y = minY; y <= maxY; y++) {
				double x = a * y + b;
				int roundedX = (int) (x + 0.5);
				
				// TODO anti-aliasing?
				
				// Don't go outside texture bounds
				if (roundedX >= 0 && roundedX < width)
					texture.setPixel(roundedX, y, color);
			}
		}
	}

	public void fillRect(int minX, int minY, int maxX, int maxY, byte red, byte green, byte blue, byte alpha) {
		for (int x = minX; x <= maxX; x++)
			for (int y = minY; y <= maxY; y++)
				texture.setPixel(x, y, red, green, blue, alpha);
	}

	public void fillRect(int minX, int minY, int maxX, int maxY, Color color) {
		fillRect(minX, minY, maxX, maxY, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	public void fillCircle(double centerX, double centerY, double radius, Color color) {
		int minX = Math.max((int) Math.floor(centerX - radius), 0);
		int maxX = Math.min((int) Math.floor(centerX + radius), width - 1);
		double radiusSQ = radius * radius;
		for (int x = minX; x <= maxX; x++) {
			
			/*
			 * Use (x + 0.5 - centerX)^2 + (y + 0.5 - centerY)^2 <= radius^2
			 * so (y + 0.5 - centerY)^2 <= radius^2 - (x + 0.5 - centerX)^2
			 * so |y + 0.5 - centerY| <= sqrt(radius^2 - (x + 0.5 - centerX)^2)
			 * so if y >= centerY - 0.5: y + 0.5 - centerY <= sqrt(...) thus y <= centerY + sqrt(...) - 0.5
			 * so if y <= centerY - 0.5: -y + centerY <= sqrt(...) thus y >= centerY - sqrt(...) - 0.5
			 */
			double dx = x + 0.5 - centerX;
			double maxDistSqY = radiusSQ - dx * dx;
			double maxDistY = Math.sqrt(maxDistSqY);
			
			double minYD = centerY - maxDistY - 0.5;
			double minYDF = Math.floor(minYD);
			int minY = (int) minYDF;
			texture.setPixel(x, minY, mixColors((float) (1 - (minYD - minYDF)), color.getRedF(), color.getGreenF(), color.getBlueF(), texture.getPixel(x, minY)));
			
			double maxYD = centerY + maxDistY - 0.5;
			double maxYDF = Math.floor(maxYD);
			
			// TODO Finetune this someday
			int maxY = (int) maxYDF;
			if (maxY != minY) {
				texture.setPixel(x, maxY, mixColors((float) (1 - (maxYD - maxYDF)), color.getRedF(), color.getGreenF(), color.getBlueF(), texture.getPixel(x, maxY)));
			
				if (maxY > minY + 1) {
					drawVerticalLine(minY + 1, maxY - 1, x, color);
				}
			}
		}
	}

	public void fillOval(int centreX, int centreY, double radiusX, double radiusY, Color color) {
		int minX = (int) (centreX - radiusX);
		int minY = (int) (centreY - radiusY);
		int maxX = (int) (centreX + radiusX + 1);
		int maxY = (int) (centreY + radiusY + 1);
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				double distance = Math.hypot((x - centreX) / radiusX, (y - centreY) / radiusY);
				if (distance <= 1)
					texture.setPixel(x, y, color);
			}
		}
	}
}
