package main.java.com.vss.model;

import java.awt.image.BufferedImage;

public class ImageData {
    protected int[][][] image;

    public void setImageMatrix(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);

                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                this.image[y][x][0] = red;
                this.image[y][x][1] = green;
                this.image[y][x][2] = blue;
            }
        }
    }

    ImageData(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][][] matrix = new int[height][width][3];

        System.out.println(width + " " + height);

        this.image = matrix;
    }

    ImageData(int width, int height) {
        int[][][] matrix = new int[height][width][3];

        System.out.println(width + " " + height);

        this.image = matrix;
    }
}
