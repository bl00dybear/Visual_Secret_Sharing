package main.java.com.vss.model;

import java.awt.image.BufferedImage;
import java.util.Random;

public class Share extends ImageData {
    private Integer totalNumberOfShares;
    private Integer minimumNumberOfShares;
    private static final int MOD = 257;
    private static final Random random = new Random(); // O singură instanță de Random

    public Share (int width, int height) {
        super(width, height);
    }

    public Share(BufferedImage image) {
        super(image);
    }

    public void setMinimumNumberOfShares(Integer minimumNumberOfShares) {
        this.minimumNumberOfShares = minimumNumberOfShares;
    }

    public void setTotalNumberOfShares(Integer totalNumberOfShares) {
        this.totalNumberOfShares = totalNumberOfShares;
    }


    private int polynomialFunction(int x, int minNumShares, Integer[] coeff) {
        int mod = MOD;
        int result = coeff[0];
        int xPower = 1;

        for (int i = 1; i < minNumShares; i++) {
            xPower = (int)(((long)xPower * x) % mod);
            int term = (int)(((long)coeff[i] * xPower) % mod);
            result = (result + term) % mod;
        }

        return result;
    }

    public void set_share_pixel(int i,int j,int z, Integer[] coeff,int shareIndex,int minNumShares) {
        this.image[i][j][z] = this.polynomialFunction(shareIndex, minNumShares, coeff);

    }

    public BufferedImage getShareImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int countNonZero = 0;
        int maxValue = 0;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int r = this.image[i][j][0];
                int g = this.image[i][j][1];
                int b = this.image[i][j][2];

                if (r > 0 || g > 0 || b > 0) {
                    countNonZero++;
                }

                maxValue = Math.max(maxValue, Math.max(r, Math.max(g, b)));

                int rgb = (r << 16) | (g << 8) | b;
                image.setRGB(j, i, rgb);
            }
        }

        System.out.println("Share Info: Non-zero pixels: " + countNonZero + ", Max value: " + maxValue);

        return image;
    }
}