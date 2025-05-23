package main.java.com.vss.test;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class TestVSS2 {

    Integer coefficients[];

    private void generateCoefficients(int secret, int minimumNumberOfShares) {
        coefficients = new Integer[minimumNumberOfShares];

        // a0 = secretul
        coefficients[0] = secret;
        int max = 256;
        int min = 1;
        int range = max - min + 1;

        // a1, a2, ..., a_{k-1} sunt aleatori
        for (int i = 1; i < minimumNumberOfShares; i++) {
            int randomNumber = (int)(Math.random() * range) + min; // Valori între 0 și 256
            coefficients[i] = randomNumber;
        }
    }

    private int polynomialFunction(int x, int minNumShares, Integer[] coeff) {
        int mod = 257;
        int result = coeff[0]; // a0 (secretul)
        int xPower = 1;

        for (int i = 1; i < minNumShares; i++) {
            xPower = (int)(((long)xPower * x) % mod);
            int term = (int)(((long)coeff[i] * xPower) % mod);
            result = (result + term) % mod;
        }

        return result;
    }

    private int modInverse(int x) {
        int mod = 257;
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
        int mod = 257;
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

    public static void main(String[] args) {
        int numShares, minShares;
        int secret;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter number of shares and minimum number of shares:");

        numShares = scanner.nextInt();
        minShares = scanner.nextInt();

        System.out.println("Enter secret:");

        secret = scanner.nextInt();

        TestVSS2 testVSS = new TestVSS2();
        testVSS.generateCoefficients(secret, minShares);

        for(int i=1;i<=numShares;i++) {
            int share = testVSS.polynomialFunction(i, minShares, testVSS.coefficients);
            System.out.println("Share " + i + " = " + share);
        }


        ArrayList<Integer> sharesIndex = new ArrayList<Integer>();
        ArrayList<Integer> sharesValue = new ArrayList<Integer>();

        System.out.println("\nEnter share index and share values:");
        for(int i=1; i<=minShares; i++) {
            System.out.print("Index: ");
            int shareIndex = scanner.nextInt();
            sharesIndex.add(shareIndex);

            System.out.print("Share " + shareIndex + ": ");
            int shareValue = scanner.nextInt();
            sharesValue.add(shareValue);
        }

        int secret2 = 0;
        for(int i=0; i<minShares; i++) {
            int shareIndex = sharesIndex.get(i);
            int shareValue = sharesValue.get(i);
            int lagrangeCoeff = testVSS.L_i(shareIndex, sharesIndex);
            secret2 = (secret2 + (int)(((long)shareValue * lagrangeCoeff) % 257)) % 257;
        }

        System.out.println("Secret: " + secret2);
    }
}