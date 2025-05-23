package main.java.com.vss.model;

import java.awt.desktop.SystemSleepEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Secret extends ImageData{
    private final List<Share> shares;
    private Integer totalNumberOfShares;
    private Integer minimumNumberOfShares;
    private static final int MOD = 257;

    private Integer[] coefficients;

    public Secret(BufferedImage image) {
        super(image);
        totalNumberOfShares = 0;
        minimumNumberOfShares = 0;
        shares = new ArrayList<>();

        System.out.println("[1][1]:" + this.image[0][0][0] + " " + this.image[0][0][1] + " " + this.image[0][0][2] );

    }

    public Secret(int width, int height) {
        super(width, height);
        totalNumberOfShares = 0;
        minimumNumberOfShares = 0;
        shares = new ArrayList<>();
    }

    public void setMinimumNumberOfShares(Integer minimumNumberOfShares) {
        this.minimumNumberOfShares = minimumNumberOfShares;
    }

    public void setTotalNumberOfShares(Integer totalNumberOfShares) {
        this.totalNumberOfShares = totalNumberOfShares;
    }

    private void generateCoefficients(int secret) {
        coefficients = new Integer[minimumNumberOfShares];

        coefficients[0] = secret;
        int max = 256;
        int min = 1;
        int range = max - min + 1;

        for (int i = 1; i < minimumNumberOfShares; i++) {
            int randomNumber = (int)(Math.random() * range) + min;
            coefficients[i] = randomNumber;
        }
    }

    public List<Share> getShares(Integer width, Integer height) {
        for (int i=1; i<=totalNumberOfShares; i++) {
            Share share = new Share(width, height);
            share.setMinimumNumberOfShares(minimumNumberOfShares);
            share.setTotalNumberOfShares(totalNumberOfShares);

            shares.add(share);
        }

        for(int i=0; i<height; i++) {
            for(int j=0; j<width; j++) {
                for(int z=0; z<3; z++) {
                    int secret = image[i][j][z];
                    generateCoefficients(secret);

                    for(int shareNum=0; shareNum<shares.size(); shareNum++) {
                        shares.get(shareNum).set_share_pixel(i, j, z, coefficients,shareNum+1, minimumNumberOfShares);
                    }
                }
            }
        }

        return shares;
    }

    private int modInverse(int x) {
        int mod = MOD;
        x = x % mod;
        if (x < 0) x += mod;

        int t = 0, newT = 1;
        int r = mod, newR = x;

        while (newR != 0) {
            int quotient = r / newR;

            int tempT = t;
            t = newT;
            newT = tempT - quotient * newT;
            int tempR = r;
            r = newR;
            newR = tempR - quotient * newR;
        }
        if (r > 1) {
            throw new ArithmeticException("Numărul " + x + " nu are invers modular mod " + mod);
        }
        if (t < 0) {
            t += mod;
        }
        return t;
    }

    private int L_i(int xi, List<Integer> shareIndices) {
        int mod = MOD;
        int L_up = 1;
        int L_down = 1;

        for (Integer xj : shareIndices) {
            if (xi != xj) {
                // x_j / (x_i - x_j)
                int diff = (xi - xj);

                if (diff < 0) diff += mod;
                else diff = diff % mod;

                L_up = (L_up * xj) % mod;
                L_down = (L_down * diff) % mod;
            }
        }

        int L_down_inv = modInverse(L_down);
        int L = (int)(((long)L_up * L_down_inv) % mod);
        return L;
    }

    private int[][][] calculateSecretPixels(int width, int height, List<Share> shares,List<Integer> shareIndices) {
        int[][][] matrix = new int[height][width][3];
//        int mod = MOD;

        System.out.println("Reconstruim secretul din " + shares.size() + " shares");

//        List<Integer> shareIndices = new ArrayList<>();
//        for (int i = 0; i < shares.size(); i++) {
//            shareIndices.add(i + 1);
//        }

        boolean hasValidPixels = false;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                for (int z = 0; z < 3; z++) {
                    int secret = 0;

                    for(int index=0;index<shares.size();index++) {
                        int shareIndex = shareIndices.get(index);
                        int shareValue = shares.get(index).image[i][j][z];
                        int lagrangeCoeff = L_i(shareIndex, shareIndices);

                        secret = (secret + (int)(((long)shareValue * lagrangeCoeff) % 257)) % 257;
                    }

                    if (secret > 0) {
                        hasValidPixels = true;
                    }

                    if (secret == 256) {
                        secret = 0;
                    }

                    matrix[i][j][z] = secret;
                }
            }
        }

        if (!hasValidPixels) {
            System.err.println("AVERTISMENT: Toate valorile reconstruite sunt 0!");
        }

        return matrix;
    }

    public BufferedImage getSecretImage(int width, int height, List<Share> shares,List<Integer> shareIndices) {
        System.out.println("Creăm imagine secretă de " + width + "x" + height);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        this.image = calculateSecretPixels(width, height, shares,shareIndices);

        System.out.println("[1][1]:" + this.image[1][1][1] + " " + this.image[1][1][2] + " " + this.image[1][1][0] );


        int minR = 255, maxR = 0;
        int minG = 255, maxG = 0;
        int minB = 255, maxB = 0;
        int countInvalid = 0;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int r = this.image[i][j][0];
                int g = this.image[i][j][1];
                int b = this.image[i][j][2];

                // Verifică valorile invalide
                if (r == 256 || g == 256 || b == 256) {
                    countInvalid++;
                    // Limitează la 255
                    r = Math.min(r, 255);
                    g = Math.min(g, 255);
                    b = Math.min(b, 255);
                }

                // Actualizează min/max
                if (r > 0) { // Ignoră valorile 0 pentru statistici
                    minR = Math.min(minR, r);
                    maxR = Math.max(maxR, r);
                }
                if (g > 0) {
                    minG = Math.min(minG, g);
                    maxG = Math.max(maxG, g);
                }
                if (b > 0) {
                    minB = Math.min(minB, b);
                    maxB = Math.max(maxB, b);
                }

                int rgb = (r << 16) | (g << 8) | b;
                image.setRGB(j, i, rgb);
            }
        }

        System.out.println("Valori RGB găsite:");
        System.out.println("R: min=" + minR + ", max=" + maxR);
        System.out.println("G: min=" + minG + ", max=" + maxG);
        System.out.println("B: min=" + minB + ", max=" + maxB);
        if (countInvalid > 0) {
            System.out.println("Pixeli cu valori >255: " + countInvalid);
        }

        return image;
    }
}