package main.java.com.vss;

import java.awt.image.BufferedImage;
import java.util.List;

public class Secret extends ImageData{
    private List<Share> shares;
    private Integer totalNumberOfShares;
    private Integer minimumNumberOfShares;

    Secret(BufferedImage image) {
        super(image);
        totalNumberOfShares = 0;
        minimumNumberOfShares = 0;
    }

    public void setMinimumNumberOfShares(Integer minimumNumberOfShares) {
        this.minimumNumberOfShares = minimumNumberOfShares;
    }

    public void setTotalNumberOfShares(Integer totalNumberOfShares) {
        this.totalNumberOfShares = totalNumberOfShares;
    }

    public List<Share> getShares() {return shares;}
}
