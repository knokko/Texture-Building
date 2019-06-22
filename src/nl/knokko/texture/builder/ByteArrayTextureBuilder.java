/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2019 knokko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *  
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
package nl.knokko.texture.builder;

import java.awt.image.BufferedImage;
import java.util.Random;

import nl.knokko.texture.color.Color;
import nl.knokko.texture.color.SimpleRGBAColor;
import nl.knokko.texture.color.SimpleRGBColor;

public class ByteArrayTextureBuilder {

	public static Color getDifColor(Random random, Color basic, float maxDifference) {
		return getMultipliedColor(basic, 1 - maxDifference + random.nextFloat() * maxDifference * 2);
	}

	public static Color getMultipliedColor(Color basic, float factor) {
		if (basic.getAlphaI() == 255)
			return SimpleRGBColor.fromFloats(basic.getRedF() * factor, basic.getGreenF() * factor, basic.getBlueF() * factor);
		else
			return SimpleRGBAColor.fromFloats(basic.getRedF() * factor, basic.getGreenF() * factor, basic.getBlueF() * factor, basic.getAlphaF());
	}

	private byte[] data;

	private int width;
	private int height;

	private boolean useAlpha;

	public ByteArrayTextureBuilder(int width, int height, boolean useAlpha) {
		data = new byte[width * height * (useAlpha ? 4 : 3)];
		this.width = width;
		this.height = height;
		this.useAlpha = useAlpha;
	}

	public void setPixel(int x, int y, byte red, byte green, byte blue, byte alpha) {
		int index = (y * width + x) * (useAlpha ? 4 : 3);
		data[index] = red;
		data[index + 1] = green;
		data[index + 2] = blue;
		if (useAlpha)
			data[index + 3] = alpha;
	}

	public void setPixel(int x, int y, byte red, byte green, byte blue) {
		setPixel(x, y, red, green, blue, (byte) 0);
	}

	public void setPixel(int x, int y, Color color) {
		setPixel(x, y, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	public Color getPixel(int x, int y) {
		int index = (y * width + x) * (useAlpha ? 4 : 3);
		return useAlpha ? SimpleRGBAColor.fromBytes(data[index], data[index + 1], data[index + 2], data[index + 3])
				: SimpleRGBColor.fromBytes(data[index], data[index + 1], data[index + 2]);
	}

	public void drawHorizontalLine(int minX, int maxX, int y, byte red, byte green, byte blue, byte alpha) {
		for (int x = minX; x <= maxX; x++)
			setPixel(x, y, red, green, blue, alpha);
	}

	public void drawHorizontalLine(int minX, int maxX, int y, Color color) {
		drawHorizontalLine(minX, maxX, y, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	public void drawVerticalLine(int minY, int maxY, int x, byte red, byte green, byte blue, byte alpha) {
		for (int y = minY; y <= maxY; y++)
			setPixel(x, y, red, green, blue, alpha);
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
					setPixel(x, roundedY, color);
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
				
				// Don't go outside texture bounds
				if (roundedX >= 0 && roundedX < width)
					setPixel(roundedX, y, color);
			}
		}
	}

	public void fillRect(int minX, int minY, int maxX, int maxY, byte red, byte green, byte blue, byte alpha) {
		for (int x = minX; x <= maxX; x++)
			for (int y = minY; y <= maxY; y++)
				setPixel(x, y, red, green, blue, alpha);
	}

	public void fillRect(int minX, int minY, int maxX, int maxY, Color color) {
		fillRect(minX, minY, maxX, maxY, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	public void fillCircle(int centreX, int centreY, double radius, Color color) {
		int minX = Math.max((int) (centreX - radius), 0);
		int minY = Math.max((int) (centreY - radius), 0);
		int maxX = Math.min((int) (centreX + radius + 1), width - 1);
		int maxY = Math.min((int) (centreY + radius + 1), height - 1);
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				double distance = Math.hypot(x - centreX, y - centreY);
				if (distance <= radius)
					setPixel(x, y, color);
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
					setPixel(x, y, color);
			}
		}
	}

	public void fillAverage(int minX, int minY, int maxX, int maxY, Color color, float maxDifference, Random random) {
		for (int x = minX; x <= maxX; x++)
			for (int y = minY; y <= maxY; y++)
				setPixel(x, y, getDifColor(random, color, maxDifference));
	}

	public void fillAverageChance(int minX, int minY, int maxX, int maxY, Color color, float maxDifference,
			Random random, int chance) {
		for (int x = minX; x <= maxX; x++)
			for (int y = minY; y <= maxY; y++)
				if (random.nextInt(chance) == 0)
					setPixel(x, y, getDifColor(random, color, maxDifference));
	}

	public void fillAverageChance(int minX, int minY, int maxX, int maxY, Color color, float maxDifference,
			Random random, int partChance, int wholeChance) {
		for (int x = minX; x <= maxX; x++)
			for (int y = minY; y <= maxY; y++)
				if (random.nextInt(wholeChance) < partChance)
					setPixel(x, y, getDifColor(random, color, maxDifference));
	}

	public void fillBrickPattern(int minX, int minY, int maxX, int maxY, int brickLength, int brickHeight,
			Color brickColor, Color edgeColor, float maxDifference, Random random) {
		fillAverage(minX, minY, maxX, maxY, brickColor, maxDifference, random);
		for (int y = minY; y <= maxY; y += brickHeight)
			fillAverage(minX, y, maxX, y, edgeColor, maxDifference, random);
		boolean flipper = false;
		for (int y = minY; y <= maxY; y += brickHeight) {
			for (int x = flipper ? minX : minX + brickLength / 2; x <= maxX; x += brickLength)
				fillAverage(x, y, x, Math.min(y + brickHeight, maxY), edgeColor, maxDifference, random);
			flipper = !flipper;
		}
	}

	public void fillWoodPlanksPattern(int minX, int minY, int maxX, int maxY, int plankLength, int plankHeight,
			int plankShift, Color plankColor, Color edgeColor, float maxDifference, Random random) {
		// fillWoodPattern(minX, minY, maxX, maxY, plankColor, random);
		int shift = plankShift;
		for (int y = minY; y <= maxY; y += plankHeight) {
			while (shift > plankLength)
				shift -= plankLength;
			fillWoodPattern(minX, y, minX + shift, Math.min(y + plankHeight, maxY), plankColor, random);
			for (int x = minX + shift; x <= maxX; x += plankLength)
				fillWoodPattern(x, y, Math.min(x + plankLength, maxX), Math.min(y + plankHeight, maxY), plankColor,
						random);
			shift += plankShift;
		}
		shift = plankShift;
		for (int y = minY; y <= maxY; y += plankHeight) {
			fillAverage(minX, y, maxX, y, edgeColor, maxDifference, random);
			for (int x = minX + shift; x <= maxX; x += plankLength)
				fillAverage(x, y, x, Math.min(y + plankHeight, maxY), edgeColor, maxDifference, random);
			shift += plankShift;
			while (shift > plankLength)
				shift -= plankLength;
		}
		drawHorizontalLine(minX, maxX, minY, edgeColor);
		drawHorizontalLine(minX, maxX, maxY, edgeColor);
		drawVerticalLine(minY, maxY, minX, edgeColor);
		drawVerticalLine(minY, maxY, maxX, edgeColor);
	}

	public void fillWoodPattern(int minX, int minY, int maxX, int maxY, Color averageColor, Random random) {
		Color color = getDifColor(random, averageColor, 0.3f);
		fillRect(minX, minY, maxX, maxY, color);
		for (int i = 0; i < 10; i++) {
			Color lineColor = getDifColor(random, color, 0.3f);
			int y = minY + random.nextInt(maxY - minY + 1);
			for (int x = minX; x <= maxX; x++) {
				setPixel(x, y, lineColor);
				if (y < maxY && random.nextInt(4) == 0)
					y++;
				if (y > minY && random.nextInt(4) == 0)
					y--;
			}
		}
	}

	public void fillDecayingCircle(int centerX, int centerY, double radius, Color color) {
		int minX = (int) (centerX - radius);
		int minY = (int) (centerY - radius);

		// Don't go over the bounds
		if (minX < 0) {
			minX = 0;
		}

		if (minY < 0) {
			minY = 0;
		}

		double radiusSQ = radius * radius;

		// Casting to int will round down, but we need to round upwards
		int maxX = (int) (centerX + radius);
		int maxY = (int) (centerY + radius);

		// If radius is not an integer, we rounded down, so we need to increase them by
		// 1
		if (radius != (int) radius) {
			maxX++;
			maxY++;
		}

		// Again, don't go over the bounds
		if (maxX >= width()) {
			maxX = width() - 1;
		}

		if (maxY >= height()) {
			maxY = height() - 1;
		}

		// Now the actual work
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				double distanceSQ = (x - centerX) * (x - centerX) + (y - centerY) * (y - centerY);

				// Only if we are in the circle
				if (distanceSQ < radiusSQ) {

					// The current weight determines how 'much' of the current pixel color at this
					// place will be kept
					// and how 'much' of the color parameter will be put in this pixel.
					float currentWeight = (float) (distanceSQ / radiusSQ);
					float colorWeight = 1f - currentWeight;

					Color current = getPixel(x, y);
					setPixel(x, y,
							SimpleRGBColor.fromFloats(currentWeight * current.getRedF() + colorWeight * color.getRedF(),
									currentWeight * current.getGreenF() + colorWeight * color.getGreenF(),
									currentWeight * current.getBlueF() + colorWeight * color.getBlueF()));
				}
			}
		}
	}

	public void addDecayingCirclePattern(int minX, int minY, int maxX, int maxY, Color color, float maxColorDifference,
			double minRadius, double maxRadius, float density, Random random) {
		int width = maxX - minX + 1;
		int height = maxY - minY + 1;
		int area = width * height;

		double radiusDifference = maxRadius - minRadius;

		int amount = (int) (density * area);
		System.out.println("amount is " + amount);

		for (int counter = 0; counter < amount; counter++) {
			int x = minX + random.nextInt(width);
			int y = minY + random.nextInt(height);
			fillDecayingCircle(x, y, minRadius + radiusDifference * random.nextDouble(),
					getDifColor(random, color, maxColorDifference));
		}
	}

	public void addDecayingCirclePattern(Color color, float maxColorDifference, double minRadius, double maxRadius,
			float density, Random random) {
		addDecayingCirclePattern(0, 0, width() - 1, height() - 1, color, maxColorDifference, minRadius, maxRadius,
				density, random);
	}

	public int width() {
		return width;
	}

	public int height() {
		return height;
	}

	public boolean useAlpha() {
		return useAlpha;
	}

	public BufferedImage createBufferedImage() {
		BufferedImage image = new BufferedImage(width, height,
				useAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int dataIndex = (useAlpha ? 4 : 3) * (y * width + x);
				int alpha = useAlpha ? data[dataIndex + 3] : 255;
				image.setRGB(x, y,((alpha & 0xFF) << 24) |
		                ((data[dataIndex] & 0xFF) << 16) |
		                ((data[dataIndex + 1] & 0xFF) << 8)  |
		                ((data[dataIndex + 2] & 0xFF) << 0));
			}
		}
		return image;
	}
}