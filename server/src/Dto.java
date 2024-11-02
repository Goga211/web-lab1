import com.fastcgi.FCGIInterface;

import java.io.*;
import java.util.HashMap;

public class Dto {
    /**
     * Параметры запроса.
     */
    private static final HashMap<String, String> params = new HashMap<>();

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
    public void SetVal(){
        parseParams();
        this.x = Double.parseDouble(params.get("x"));
        this.y = Double.parseDouble(params.get("y"));
        this.r = Double.parseDouble(params.get("r"));
    }

    /**
     * Обработать параметры запроса.
     */
    public static void parseParams() {
        String queryString = FCGIInterface.request.params.getProperty("QUERY_STRING");
        if (queryString != null && !queryString.isEmpty()) {
            for (String pair : queryString.split("&")) {
                String[] keyValue = pair.split("=");
                if (keyValue.length > 1) {
                    params.put(keyValue[0], keyValue[1]);
                } else {
                    params.put(keyValue[0], "");
                }
            }
        }
    }

}