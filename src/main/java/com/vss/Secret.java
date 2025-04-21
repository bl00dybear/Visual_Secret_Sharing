package main.java.com.vss;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Secret extends ImageData{
    private List<Share> shares;
    private Integer totalNumberOfShares;
    private Integer minimumNumberOfShares;
    private Integer[] coefficients;

    Secret(BufferedImage image) {
        super(image);
        totalNumberOfShares = 0;
        minimumNumberOfShares = 0;
        shares = new ArrayList<>();
    }

    public Integer[] getCoefficients() {
        return coefficients;
    }

    public void setMinimumNumberOfShares(Integer minimumNumberOfShares) {
        this.minimumNumberOfShares = minimumNumberOfShares;
    }

    public void setTotalNumberOfShares(Integer totalNumberOfShares) {
        this.totalNumberOfShares = totalNumberOfShares;
    }

    private void generateCoefficients() {
        coefficients = new Integer[minimumNumberOfShares];
        for (int i = 0; i < minimumNumberOfShares-1; i+=1) {
            int randomNumber = new Random().nextInt(255)+1;
            coefficients[i] = randomNumber;
        }
    }

    public List<Share> getShares(Integer width, Integer height) {
        if (coefficients == null) {
            generateCoefficients();
        }

        for (Integer coefficient : coefficients) {
            System.out.println(coefficient);
        }

        for (int i=0; i<totalNumberOfShares; i++) {
            Share share = new Share(width, height);
            share.generateShare(this.image,coefficients,minimumNumberOfShares,i+1);
            shares.add(share);
//            System.out.println(share.image[0][0][0]);
        }


        return shares;
    }
}
