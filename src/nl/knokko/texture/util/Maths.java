package nl.knokko.texture.util;

public class Maths {
	
	public static final double DEGREES_TO_RADIANS = Math.PI / 180.0;
	
	public static double sinDegrees(double angle) {
		return Math.sin(angle * DEGREES_TO_RADIANS);
	}
	
	public static double cosDegrees(double angle) {
		return Math.cos(angle * DEGREES_TO_RADIANS);
	}
	
	public static int min(int...numbers) {
		int min = numbers[0];
		for (int index = 1; index < numbers.length; index++) {
			int current = numbers[index];
			if (current < min) {
				min = current;
			}
		}
		return min;
	}
	
	public static int max(int...numbers) {
		int max = numbers[0];
		for (int index = 1; index < numbers.length; index++) {
			int current = numbers[index];
			if (current > max) {
				max = current;
			}
		}
		return max;
	}
	
	// TODO Remove after debugging
	public static float sin(float angle){
		return (float) Math.sin(Math.toRadians(angle));
	}
	
	public static float cos(float angle){
		return (float) Math.cos(Math.toRadians(angle));
	}
}