package main.java.com.vss.model;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Secret extends ImageData{
    private final List<Share> shares;
    private Integer totalNumberOfShares;
    private Integer minimumNumberOfShares;
    private static final int MOD = 257;

    public Secret(BufferedImage image) {
        super(image);
        totalNumberOfShares = 0;
        minimumNumberOfShares = 0;
        shares = new ArrayList<>();
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

    public List<Share> getShares(Integer width, Integer height) {
        for (int i=0; i<totalNumberOfShares; i++) {
            Share share = new Share(width, height);
            share.setMinimumNumberOfShares(minimumNumberOfShares);
            share.setTotalNumberOfShares(totalNumberOfShares);

            share.generateShare(this.image, minimumNumberOfShares, i+1);
            shares.add(share);
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

    private int functionL_i(int i, List<Integer> shareIndices) {
        int mod = MOD;
        int L_up = 1;
        int L_down = 1;

        System.out.println("Calculăm L pentru i=" + i + " din " + shareIndices.size() + " shares");

        for(Integer j : shareIndices) {
            if(i != j) {
                // x_j / (x_i - x_j)
                int xj = j;
                int diff = (i - j);

                if (diff < 0) diff += mod;
                else diff = diff % mod;

                L_up = (L_up * xj) % mod;
                L_down = (L_down * diff) % mod;

                System.out.println("  j=" + j + ": L_up=" + L_up + ", L_down=" + L_down);
            }
        }

        try {
            int L_down_inv = modInverse(L_down);
            int L = (int)(((long)L_up * L_down_inv) % mod);
            System.out.println("L_" + i + " = " + L);
            return L;
        } catch (ArithmeticException e) {
            System.err.println("Eroare la calculul L_" + i + ": " + e.getMessage());
            return 0; // Sau gestionează eroarea în alt mod
        }
    }

    private int[][][] calculateSecretPixels(int width, int height, List<Share> shares) {
        int[][][] matrix = new int[height][width][3];
        int mod = MOD;

        System.out.println("Reconstruim secretul din " + shares.size() + " shares");

        // Extrage indicii share-urilor
        List<Integer> shareIndices = new ArrayList<>();
        for (int i = 0; i < shares.size(); i++) {
            shareIndices.add(i + 1); // Presupunem că share-urile au indici de la 1 la n
        }

        // Calculează coeficienții Lagrange
        int[] coefficients = new int[shares.size()];
        for (int i = 0; i < shares.size(); i++) {
            coefficients[i] = functionL_i(i + 1, shareIndices);
        }

        // Verifică dacă valoarea unui pixel este în intervalul [0, 255]
        boolean hasValidPixels = false;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < 3; z++) {
                    int secret = 0;

                    for (int shareIndex = 0; shareIndex < shares.size(); shareIndex++) {
                        int coef = coefficients[shareIndex];
                        int shareValue = shares.get(shareIndex).image[y][x][z];

                        int term = (int)(((long)coef * shareValue) % mod);
                        secret = (secret + term) % mod;
                    }

                    // Verifică dacă secretul este valid
                    if (secret > 0) {
                        hasValidPixels = true;
                    }

                    // Gestionează valoarea 256 care este invalidă pentru RGB
                    if (secret == 256) {
                        secret = 0; // Sau 255, depinde de cum este tratat în schema ta
                    }

                    matrix[y][x][z] = secret;
                }
            }
        }

        if (!hasValidPixels) {
            System.err.println("AVERTISMENT: Toate valorile reconstruite sunt 0!");
        }

        return matrix;
    }

    public BufferedImage getSecretImage(int width, int height, List<Share> shares) {
        System.out.println("Creăm imagine secretă de " + width + "x" + height);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        this.image = calculateSecretPixels(width, height, shares);

        // Verifică valorile pixelilor
        int minR = 255, maxR = 0;
        int minG = 255, maxG = 0;
        int minB = 255, maxB = 0;
        int countInvalid = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int r = this.image[y][x][0];
                int g = this.image[y][x][1];
                int b = this.image[y][x][2];

                // Verifică valorile invalide
                if (r > 255 || g > 255 || b > 255) {
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
                image.setRGB(x, y, rgb);
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