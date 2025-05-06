package main.java.com.vss.model;

import main.java.com.vss.model.Share;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Secret extends ImageData{
    private final List<Share> shares;
    private Integer totalNumberOfShares;
    private Integer minimumNumberOfShares;

    public Secret(BufferedImage image) {
        super(image);
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

//    private void generateCoefficients() {
//        coefficients = new Integer[minimumNumberOfShares];
//        for (int i = 0; i < minimumNumberOfShares-1; i+=1) {
//            int randomNumber = new Random().nextInt(255)+1;
//            coefficients[i] = randomNumber;
//        }
//    }

    public List<Share> getShares(Integer width, Integer height) {
//        if (coefficients == null) {
//            generateCoefficients();
//        }

        for (int i=0; i<totalNumberOfShares; i++) {
            Share share = new Share(width, height);
            share.setMinimumNumberOfShares(minimumNumberOfShares);
            share.setTotalNumberOfShares(totalNumberOfShares);

            share.generateShare(this.image,minimumNumberOfShares,i+1);
            shares.add(share);
        }

        return shares;
    }

}
