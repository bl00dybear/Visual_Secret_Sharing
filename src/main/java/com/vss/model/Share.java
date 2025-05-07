package main.java.com.vss.model;

import java.awt.image.BufferedImage;
import java.util.Random;

public class Share extends ImageData {
    private Integer[] coefficients;
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

    /**
     * Evaluează polinomul f(x) = a0 + a1*x + a2*x^2 + ... + a_{k-1}*x^{k-1}
     * Unde a0 este secretul care trebuie împărtășit.
     */
    private int polynomialFunction(int x, int minNumShares, Integer[] coeff) {
        int mod = MOD;
        int result = coeff[0]; // a0 (secretul)
        int xPower = 1;

        for (int i = 1; i < minNumShares; i++) {
            xPower = (int)(((long)xPower * x) % mod);
            int term = (int)(((long)coeff[i] * xPower) % mod);
            result = (result + term) % mod;
        }

        return result;
    }

    /**
     * Generează coeficienți aleatori pentru un polinom de grad k-1.
     * a0 este secretul, a1...a_{k-1} sunt aleatori.
     */
    private void generateCoefficients(int secret) {
        coefficients = new Integer[minimumNumberOfShares];

        // a0 = secretul
        coefficients[0] = secret;

        // a1, a2, ..., a_{k-1} sunt aleatori
        for (int i = 1; i < minimumNumberOfShares; i++) {
            int randomNumber = random.nextInt(MOD); // Valori între 0 și 256
            coefficients[i] = randomNumber;
        }
    }

    /**
     * Procesează pixelii imaginii pentru a genera un share.
     * Pentru fiecare pixel, creează un nou polinom cu secretul = valoarea pixelului.
     */
    private void processPixels(int width, int height, int[][][] image, int shareNum, int minNumShares) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < 3; z++) {
                    int secret = image[y][x][z];
                    generateCoefficients(secret);
                    int shareValue = polynomialFunction(shareNum, minNumShares, coefficients);
                    this.image[y][x][z] = shareValue;
                }
            }
        }
    }

    /**
     * Generează un share folosind schema Shamir pentru VSS.
     */
    public void generateShare(int[][][] image, int minNumShares, int shareNum) {
        int height = image.length;
        int width = image[0].length;

        processPixels(width, height, image, shareNum, minNumShares);
    }

    /**
     * Convertește matricea de pixeli într-o imagine BufferedImage.
     */
    public BufferedImage getShareImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Adăugăm cod de debugging pentru a verifica valorile share-ului
        int countNonZero = 0;
        int maxValue = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int r = this.image[y][x][0];
                int g = this.image[y][x][1];
                int b = this.image[y][x][2];

                // Contorizăm valorile non-zero
                if (r > 0 || g > 0 || b > 0) {
                    countNonZero++;
                }

                maxValue = Math.max(maxValue, Math.max(r, Math.max(g, b)));

                int rgb = (r << 16) | (g << 8) | b;
                image.setRGB(x, y, rgb);
            }
        }

        System.out.println("Share Info: Non-zero pixels: " + countNonZero + ", Max value: " + maxValue);

        return image;
    }
}