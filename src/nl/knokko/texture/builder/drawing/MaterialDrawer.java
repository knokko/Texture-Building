package nl.knokko.texture.builder.drawing;

import java.util.Random;

import nl.knokko.texture.builder.TextureBuilder;
import nl.knokko.texture.color.Color;
import nl.knokko.texture.util.Maths;

public class MaterialDrawer {

	protected final TextureBuilder texture;
	protected final int width, height;

	public MaterialDrawer(TextureBuilder texture) {
		this.texture = texture;
		this.width = texture.width();
		this.height = texture.height();
	}

	public void drawGrass(int minX, int minY, int maxX, int maxY, Color grassColor, Color brightGrassColor,
			Color groundColor, Random random) {
		long startTime = System.currentTimeMillis();

		// Calculate and define the most used variables before starting the actual stuff
		int drawWidth = maxX - minX + 1;
		int drawHeight = maxY - minY + 1;

		int redBase = grassColor.getRedI();
		int greenBase = grassColor.getGreenI();
		int blueBase = grassColor.getBlueI();

		int redLeft = brightGrassColor.getRedI() - redBase;
		int greenLeft = brightGrassColor.getGreenI() - greenBase;
		int blueLeft = brightGrassColor.getBlueI() - blueBase;

		byte redGround = groundColor.getRed();
		byte greenGround = groundColor.getGreen();
		byte blueGround = groundColor.getBlue();

		// First color the ground
		texture.geometry().fillRect(minX, minY, maxX, maxY, redGround, greenGround, blueGround, (byte) 255);

		// Use the height map to make sure the highest grass is always shown
		byte[] heightMap = new byte[drawWidth * drawHeight];

		// Draw that many grass 'lines'
		int numGrassLines = drawWidth * drawHeight / 70;
		for (int counter = 0; counter < numGrassLines; counter++) {
			int startX = minX + random.nextInt(drawWidth);
			int startY = minY + random.nextInt(drawHeight);
			double angle = random.nextDouble() * 360.0;

			// The angle between vertical and the angle at the end of the grass
			double vertAngle = random.nextDouble() * 70.0;
			double sinVertAngle = Maths.sinDegrees(vertAngle);
			
			double length = (50.0 + 30.0 * random.nextDouble()) * sinVertAngle;
			double invLength = 1.0 / length;
			
			double cosVertAngleTimesLength = Maths.cosDegrees(vertAngle) * length;

			double sinAngle = Maths.sinDegrees(angle);
			double cosAngle = Maths.cosDegrees(angle);

			double width = 4.0 + 3 * random.nextDouble();
			int endX = startX + (int) (cosAngle * length);
			int endY = startY + (int) (sinAngle * length);

			// The line through these coordinates will be perpendicular to angle and go
			// through (startX,startY)
			int startX1 = startX - (int) (sinAngle * width);
			int startY1 = startY + (int) (cosAngle * width);
			int startX2 = startX + (int) (sinAngle * width);
			int startY2 = startY - (int) (cosAngle * width);

			// revertA and revertB will be used to transform the effective region such that
			// it becomes vertical
			double revertA = sinAngle;
			double revertB = cosAngle;

			// The next variables will make it easier to loop
			int localMinX = Maths.min(startX1, startX2, endX);
			int localMinY = Maths.min(startY1, startY2, endY);
			int localMaxX = Maths.max(startX1, startX2, endX);
			int localMaxY = Maths.max(startY1, startY2, endY);
			int effectiveWidth = localMaxX - localMinX + 1;
			int effectiveHeight = localMaxY - localMinY + 1;
			double fictiveStartX = startX - localMinX;
			double fictiveStartY = startY - localMinY;

			// Loop over all relevant coordinates
			for (int x = 0; x < effectiveWidth; x++) {
				
				// Let's now get the actual x-coordinate
				int realX = localMinX + x;
				
				// If we get a little outside of the texture range, we continue on the other
				// side
				if (realX > maxX) {
					realX -= drawWidth;
				}
				if (realX < minX) {
					realX += drawWidth;
				}
				
				int heightMapX = realX - minX;
				
				double dx = x - fictiveStartX;
				
				for (int y = 0; y < effectiveHeight; y++) {

					// Rotate (x - fictiveStartX, y - fictiveStartY)
					double dy = y - fictiveStartY;
					double transformedX = revertA * dx - revertB * dy;
					double transformedY = revertA * dy + revertB * dx;

					// Check if the pixel at this location should be affected
					if (transformedX > -width && transformedX < width && transformedY >= 0 && transformedY < length) {
						double progress = transformedY * invLength;
						if (Math.abs(transformedX) <= Math.sqrt(1 - progress) * width) {

							// Let's now get the actual y-coordinate
							int realY = localMinY + y;

							if (realY > maxY) {
								realY -= drawHeight;
							}
							if (realY < minY) {
								realY += drawHeight;
							}

							// Finally test if we are not 'below' some other grass 'line'
							byte realHeight = (byte) (cosVertAngleTimesLength * progress);
							int heightMapIndex = heightMapX + drawWidth * (realY - minY);
							if (realHeight >= heightMap[heightMapIndex]) {
								heightMap[heightMapIndex] = realHeight;
								
								double extraColor = sinVertAngle * progress * progress;
								byte newRed = (byte) (redBase + extraColor * redLeft);
								byte newGreen = (byte) (greenBase + extraColor * greenLeft);
								byte newBlue = (byte) (blueBase + extraColor * blueLeft);
								texture.setPixel(realX, realY, newRed, newGreen, newBlue, (byte) 255);
							}
						}
					}
				}
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Creating big grass texture took " + (endTime - startTime) + " ms");
	}
}
