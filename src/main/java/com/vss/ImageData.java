package main.java.com.vss;

import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

public abstract class ImageData {
//    protected byte[] imageBytes;
    protected int[][][] image;

//    public byte[] getImageBytes(){}
//    public void setImageBytes(byte[] imageBytes){}
//    public BufferedImage toBufferedImage(){}
//    public void fromBufferedImage(BufferedImage image){}

    ImageData(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][][] matrix = new int[height][width][3];

        System.out.println(width + " " + height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);

                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                matrix[y][x][0] = red;
                matrix[y][x][1] = green;
                matrix[y][x][2] = blue;
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(matrix[y][x][1] + " ");
            }
            System.out.println();
        }

        this.image = matrix;
    }

}