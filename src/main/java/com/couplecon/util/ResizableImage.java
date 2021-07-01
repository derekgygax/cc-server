package com.couplecon.util;

import java.awt.Image;
import java.io.InputStream;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.lang.Math;

public class ResizableImage {
	private BufferedImage nativeImage;
	
	public ResizableImage(InputStream inputBytes) throws IOException {
		this.nativeImage = ImageIO.read(inputBytes);
	}
	
	public ResizableImage(Image image) {
		this.nativeImage = (BufferedImage) image;
	}
	
	public BufferedImage resizeByMaxSide(int maxSideLength) {
		Integer nativeWidth = this.nativeImage.getWidth();
		Integer nativeHeight = this.nativeImage.getHeight();
		Integer newHeight;
		Integer newWidth;
		if (nativeHeight >= nativeWidth) {
			newHeight =  Math.min((int)nativeHeight, maxSideLength);
			newWidth =  Math.round((float) nativeWidth*((float)newHeight/(float)nativeHeight));
		} else {
			newWidth = Math.min(nativeWidth, maxSideLength);
			newHeight =  Math.round((float) nativeHeight*((float)newWidth/(float)nativeWidth));
			
		}
		return this.resize(newWidth,newHeight);
	}
	
	public BufferedImage resize(int width, int height) {
		Image tempImage = this.nativeImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage resizedImage = new BufferedImage(width,height,this.nativeImage.getType());
		Graphics2D g2d = resizedImage.createGraphics();
		g2d.drawImage(tempImage, 0, 0, null);
		g2d.dispose();
		return resizedImage;
	}
	
	public BufferedImage getNative() {
		return this.nativeImage;
	}
}
