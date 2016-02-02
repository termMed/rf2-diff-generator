package org.ihtsdo.changeanalyzer.file;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

public class CreateTIFF {
	  public static void main(String[] args) throws IOException {
	        try {

	            // collect correct inputs or DIE.
	            String email ="este es un texto se va a convertir en una imagen";
	            Color fg = new Color(Integer.parseInt("000000", 16));
	            Color bg = new Color(Integer.parseInt("ffffff", 16));
	            String filename = "File.tiff";

	            // call render image method.
	            RenderedImage rendImage = writeImage(email, fg, bg);

	            File file = new File(filename);

	            ImageIO.write(rendImage, "png", file);

	        } catch (Exception e) {
	            // Sloppy Error handling below
	            System.out.println("Usage: textToImage.jar email fg-colour-hex bg-colour-hex filename");
	            System.out.println("Example: textToImage.jar eg@eg.com FFFFFF 0000FF C:\\dir\\image.png");
	            System.out.print(e.getMessage());
	        }
	    }

	    private static RenderedImage writeImage(String text, Color fgc, Color bgc) {

	        // calculate image size requirements.
	        int width = (text.length() * 7) + 5;

	        // standard height requirement of 16 px.
	        int height = 16;
	        BufferedImage buffRenderImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	        Graphics2D flatGraphic = buffRenderImage.createGraphics();


	        // Draw background
	        flatGraphic.setColor(bgc);
	        flatGraphic.fillRect(0, 0, width, height);

	        //Draw text
	        flatGraphic.setColor(fgc);
	        Font font = new Font("Courier", Font.BOLD, 12);
	        flatGraphic.setFont(font);
	        flatGraphic.drawString(text, 1, 10);

	        // don't use drawn graphic anymore.
	        flatGraphic.dispose();

	        return buffRenderImage;
	    }
}