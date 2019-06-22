package nl.knokko.texture.color;

public class SimpleRGBColor implements Color {
	
	public static SimpleRGBColor fromFloats(float red, float green, float blue) {
		return fromInts((int) (red * 255f + 0.5f), (int) (green * 255f + 0.5f), (int) (blue * 255f + 0.5f));
	}
	
	public static SimpleRGBColor fromInts(int red, int green, int blue) {
		return new SimpleRGBColor((byte) clamp(red), (byte) clamp(green), (byte) clamp(blue));
	}
	
	protected static int clamp(int original) {
		if (original < 0)
			return 0;
		if (original > 255)
			return 255;
		return original;
	}
	
	public static SimpleRGBColor fromBytes(byte red, byte green, byte blue) {
		return new SimpleRGBColor(red, green, blue);
	}
	
	private final byte red,green,blue;
	
	protected SimpleRGBColor(byte red, byte green, byte blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	@Override
	public byte getRed() {
		return red;
	}

	@Override
	public byte getGreen() {
		return green;
	}

	@Override
	public byte getBlue() {
		return blue;
	}

	@Override
	public byte getAlpha() {
		return -1;
	}

	@Override
	public int getRedI() {
		return red & 0xFF;
	}

	@Override
	public int getGreenI() {
		return green & 0xFF;
	}

	@Override
	public int getBlueI() {
		return blue & 0xFF;
	}

	@Override
	public int getAlphaI() {
		return 255;
	}

	@Override
	public float getRedF() {
		return (red & 0xFF) / 255f;
	}

	@Override
	public float getGreenF() {
		return (green & 0xFF) / 255f;
	}

	@Override
	public float getBlueF() {
		return (blue & 0xFF) / 255f;
	}

	@Override
	public float getAlphaF() {
		return 1f;
	}
}