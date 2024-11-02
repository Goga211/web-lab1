public class CheckPointPos {
    /**
     * Метод для проверки того, что точка лежит в окружности.
     *
     * @param x Координата по оси x.
     * @param y Координата по оси y.
     * @param r Радиус окружности.
     *
     * @return Лежит ли точка в окружности.
     */
    public static boolean isPointInside(double x, double y, double r) {
        return isPointLowerLeftQuarter(x, y, r) || isPointLowerRightQuarter(x, y, r) || isPointTopLeftQuarter(x, y, r) || isPointTopRightQuarter(x, y, r);
    }

    private static boolean isPointTopLeftQuarter(double x, double y, double r) {
        if(x <= 0 && y >= 0){
            return (x * x + y * y <= r * r);
        }
        return false;
    }

    private static boolean isPointTopRightQuarter(double x, double y, double r) {
        if (x >= 0 && y >= 0){
            return (r/2 >= y && r/2 >= x && y <= x);
        }
        return false;
    }

    private static boolean isPointLowerLeftQuarter(double x, double y, double r) {
        if (x <= 0 && y <= 0) {
            return (-r <= x && y >= -r && y <= -r/2);
        }
        return false;
    }

    private static boolean isPointLowerRightQuarter(double x, double y, double r) {
        return false;
    }
}
