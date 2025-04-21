package main.java.com.vss;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Share extends ImageData{

    Share (int width, int height) {
        super(width, height);
    }

    private int polynomialFunction(int x,int minNumShares, Integer[] coeff){
        int f = 0;

        for(int i = minNumShares-1; i>=1; i-=1){
            f += (int) Math.pow(x,i) * coeff[minNumShares - i];
        }

        f=f%257;

        return f;
    }

    private void processPixels(int width, int height, int[][][] image,
                               Integer[] coefficients, int shareNum,int minNumShares) {

        for (int x=0; x<width; x++) {
            for (int y=0; y<height; y++) {
                for (int z=0; z<3; z++) {
                    coefficients[coefficients.length-1] = image[x][y][z];
                    int xx = polynomialFunction(shareNum,minNumShares,coefficients);
                    this.image[x][y][z] = xx;
                }
            }
        }
    }

    public void generateShare(int[][][] image, Integer[] coefficients,
                              int minNumShares, int shareNum) {
        int height = image.length;
        int width = image[0].length;

        processPixels(width, height, image, coefficients,shareNum, minNumShares);
    }

    public BufferedImage getShareImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int r = this.image[y][x][0];
                int g = this.image[y][x][1];
                int b = this.image[y][x][2];
                int rgb = (r << 16) | (g << 8) | b;
                image.setRGB(x, y, rgb);
            }
        }

        return image;
    }

    public void saveShareAsImage(int index, String outputDir) throws IOException {
        int height = this.image.length;
        int width = this.image[0].length;


        File dir = new File(outputDir);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("Nu s-a putut crea directorul: " + outputDir);
            }
        }

        BufferedImage image = this.getShareImage(width, height);

        String fileName = outputDir + File.separator + "image_" + index + ".png";
        File outputFile = new File(fileName);
        ImageIO.write(image, "png", outputFile);
    }
}
