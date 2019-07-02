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
import java.util.Arrays;

import nl.knokko.texture.builder.drawing.*;
import nl.knokko.texture.color.*;

public class ByteArrayTextureBuilder implements TextureBuilder {

	protected final byte[] data;
	
	protected final int width, height;
	protected final boolean hasAlpha;
	
	protected final GeometryDrawer geometry;
	protected final MaterialDrawer materials;
	protected final AverageDrawer average;
	protected final DecayingDrawer decaying;

	public ByteArrayTextureBuilder(int width, int height, boolean useAlpha) {
		this.width = width;
		this.height = height;
		this.hasAlpha = useAlpha;
		
		data = new byte[width * height * (useAlpha ? 4 : 3)];
		
		geometry = new GeometryDrawer(this);
		materials = new MaterialDrawer(this);
		average = new AverageDrawer(this);
		decaying = new DecayingDrawer(this);
	}
	
	@Override
	public int width() {
		return width;
	}
	
	@Override
	public int height() {
		return height;
	}
	
	@Override
	public boolean useAlpha() {
		return hasAlpha;
	}
	
	@Override
	public GeometryDrawer geometry() {
		return geometry;
	}
	
	@Override
	public MaterialDrawer materials() {
		return materials;
	}
	
	@Override
	public AverageDrawer average() {
		return average;
	}
	
	@Override
	public DecayingDrawer decaying() {
		return decaying;
	}
	
	@Override
	public void setPixel(int x, int y, byte red, byte green, byte blue, byte alpha) {
		int index = (y * width + x) * (hasAlpha ? 4 : 3);
		data[index] = red;
		data[index + 1] = green;
		data[index + 2] = blue;
		if (hasAlpha)
			data[index + 3] = alpha;
	}
	
	@Override
	public byte getRed(int x, int y) {
		return data[(y * width + x) * (hasAlpha ? 4 : 3)];
	}
	
	@Override
	public byte getGreen(int x, int y) {
		return data[(y * width + x) * (hasAlpha ? 4 : 3) + 1];
	}
	
	@Override
	public byte getBlue(int x, int y) {
		return data[(y * width + x) * (hasAlpha ? 4 : 3) + 2];
	}
	
	@Override
	public byte getAlpha(int x, int y) {
		if (hasAlpha)
			return data[(y * width + x) * (hasAlpha ? 4 : 3) + 3];
		else
			return (byte) 255;
	}
	
	@Override
	public Color getPixel(int x, int y) {
		int index = (y * width + x) * (hasAlpha ? 4 : 3);
		return hasAlpha ? SimpleRGBAColor.fromBytes(data[index], data[index + 1], data[index + 2], data[index + 3])
				: SimpleRGBColor.fromBytes(data[index], data[index + 1], data[index + 2]);
	}
	
	@Override
	public BufferedImage createBufferedImage() {
		BufferedImage image = new BufferedImage(width, height,
				hasAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int dataIndex = (hasAlpha ? 4 : 3) * (y * width + x);
				int alpha = hasAlpha ? data[dataIndex + 3] : 255;
				image.setRGB(x, y,((alpha & 0xFF) << 24) |
		                ((data[dataIndex] & 0xFF) << 16) |
		                ((data[dataIndex + 1] & 0xFF) << 8)  |
		                ((data[dataIndex + 2] & 0xFF) << 0));
			}
		}
		return image;
	}

	@Override
	public byte[] createArrrayRGBA() {
		return Arrays.copyOf(data, data.length);
	}
}