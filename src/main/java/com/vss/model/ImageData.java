package main.java.com.vss.model;

import java.awt.image.BufferedImage;

public class ImageData {
    protected int[][][] image;

    public void setImageMatrix(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        this.image = new int[height][width][3];

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
        this.image = new int[height][width][3];

        setImageMatrix(image);

        System.out.println("Imagine încărcată: " + width + "x" + height);
    }

    ImageData(int width, int height) {
        this.image = new int[height][width][3];
        System.out.println("Imagine nouă creată: " + width + "x" + height);
    }

    // Metodă utilă pentru debugging
    protected void printImageStats() {
        if (image == null) {
            System.out.println("Matricea de imagine nu a fost inițializată!");
            return;
        }

        int height = image.length;
        int width = image[0].length;
        int nonZeroPixels = 0;
        int maxValue = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < 3; z++) {
                    int val = image[y][x][z];
                    if (val > 0) nonZeroPixels++;
                    maxValue = Math.max(maxValue, val);
                }
            }
        }

        System.out.println("Statistici imagine: " + width + "x" + height);
        System.out.println("Pixeli non-zero: " + nonZeroPixels + ", Valoare maximă: " + maxValue);
    }
}