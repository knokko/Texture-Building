package nl.knokko.texture.builder.drawing;

import java.util.Random;

import nl.knokko.texture.builder.TextureBuilder;
import nl.knokko.texture.color.Color;
import nl.knokko.texture.color.SimpleRGBColor;

public class DecayingDrawer {
	
	protected final TextureBuilder texture;
	protected final int width, height;
	
	public DecayingDrawer(TextureBuilder texture) {
		this.texture = texture;
		this.width = texture.width();
		this.height = texture.height();
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
		if (maxX >= width) {
			maxX = width - 1;
		}

		if (maxY >= height) {
			maxY = height - 1;
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
					double currentWeight = distanceSQ / radiusSQ;
					double colorWeight = 1.0 - currentWeight;

					Color current = texture.getPixel(x, y);
					texture.setPixel(x, y,
							SimpleRGBColor.fromDoubles(currentWeight * current.getRedD() + colorWeight * color.getRedD(),
									currentWeight * current.getGreenD() + colorWeight * color.getGreenD(),
									currentWeight * current.getBlueD() + colorWeight * color.getBlueD()));
				}
			}
		}
	}

	public void addDecayingCirclePattern(int minX, int minY, int maxX, int maxY, Color color, double maxColorDifference,
			double minRadius, double maxRadius, double density, Random random) {
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
					AverageDrawer.getDifColor(random, color, maxColorDifference));
		}
	}

	public void addDecayingCirclePattern(Color color, double maxColorDifference, double minRadius, double maxRadius,
			double density, Random random) {
		addDecayingCirclePattern(0, 0, width - 1, height - 1, color, maxColorDifference, minRadius, maxRadius,
				density, random);
	}
}
