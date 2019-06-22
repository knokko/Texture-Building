package nl.knokko.texture.color;

public class SimpleRGBAColor extends SimpleRGBColor {
	
	public static SimpleRGBAColor fromFloats(float red, float green, float blue, float alpha) {
		return fromInts((int) (red * 255f + 0.5f), (int) (green * 255f + 0.5f), 
				(int) (blue * 255f + 0.5f), (int) (alpha * 255f + 0.5f));
	}
	
	public static SimpleRGBAColor fromInts(int red, int green, int blue, int alpha) {
		return new SimpleRGBAColor((byte) clamp(red), (byte) clamp(green), (byte) clamp(blue), (byte) clamp(alpha));
	}
	
	public static SimpleRGBAColor fromBytes(byte red, byte green, byte blue, byte alpha) {
		return new SimpleRGBAColor(red, green, blue, alpha);
	}
	
	private final byte alpha;

	protected SimpleRGBAColor(byte red, byte green, byte blue, byte alpha) {
		super(red, green, blue);
		this.alpha = alpha;
	}
	
	@Override
	public byte getAlpha() {
		return alpha;
	}
	
	@Override
	public int getAlphaI() {
		return alpha & 0xFF;
	}
	
	@Override
	public float getAlphaF() {
		return (alpha & 0xFF) / 255f;
	}
}