import java.io.*;

public class Dto {
    private double x;
    private double y;
    private double r;


    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getR() {
        return r;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setR(double r) {
        this.r = r;
    }

    public void setAll (double x, double y, double r) {
        this.x = x;
        this.y = y;
        this.r = r;
    }

}