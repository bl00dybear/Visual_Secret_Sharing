package main.java.com.vss.model;

import java.awt.image.BufferedImage;

public class ImageData {
    protected int[][][] image;

    public void setImageMatrix(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        this.image = new int[height][width][3];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int rgb = image.getRGB(j, i);

                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                this.image[i][j][0] = red;
                this.image[i][j][1] = green;
                this.image[i][j][2] = blue;
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
    public void printImageStats() {
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
                    System.out.print(image[y][x][z] + " ");
                }
                System.out.println();
            }
        }
    }
}