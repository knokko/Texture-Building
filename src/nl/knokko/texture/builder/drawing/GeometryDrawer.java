package nl.knokko.texture.builder.drawing;

import nl.knokko.texture.builder.TextureBuilder;
import nl.knokko.texture.color.Color;

public class GeometryDrawer {
	
	protected final TextureBuilder texture;
	
	public GeometryDrawer(TextureBuilder texture) {
		this.texture = texture;
	}
	
	public void drawHorizontalLine(int minX, int maxX, int y, byte red, byte green, byte blue, byte alpha) {
		for (int x = minX; x <= maxX; x++)
			texture.setPixel(x, y, red, green, blue, alpha);
	}
	
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
			if (maxX >= texture.width)
				maxX = texture.width - 1;

			// Now just loop over all x's
			for (int x = minX; x <= maxX; x++) {
				double y = a * x + b;

				// TODO anti-aliasing?
				// int lowY = (int) y;
				// int highY = lowY + 1;
				
				int roundedY = (int) (y + 0.5);
				if (roundedY >= 0 && roundedY < texture.height)
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
			if (maxY >= texture.height)
				maxY = texture.height - 1;

			// Now just loop over all y's
			for (int y = minY; y <= maxY; y++) {
				double x = a * y + b;
				int roundedX = (int) (x + 0.5);
				
				// TODO anti-aliasing?
				
				// Don't go outside texture bounds
				if (roundedX >= 0 && roundedX < texture.width)
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

	public void fillCircle(int centreX, int centreY, double radius, Color color) {
		int minX = Math.max((int) (centreX - radius), 0);
		int minY = Math.max((int) (centreY - radius), 0);
		int maxX = Math.min((int) (centreX + radius + 1), texture.width - 1);
		int maxY = Math.min((int) (centreY + radius + 1), texture.height - 1);
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				double distance = Math.hypot(x - centreX, y - centreY);
				if (distance <= radius)
					texture.setPixel(x, y, color);
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
