/**
 * Класс для валидации входных данных.
 */
public class InputValidator {
    private final double x;
    private final double y;
    private final double r;

    public InputValidator(double x, double y, double r) {
        this.x = x;
        this.y = y;
        this.r = r;
    }

    /**
     * Проверяет корректность всех параметров.
     *
     * @return true, если все параметры корректны, иначе false.
     */
    public boolean validateInput() {
        return validateX() && validateY() && validateR();
    }

    /**
     * Проверяет корректность X.
     * X должен находиться в диапазоне [-3, 5].
     *
     * @return true, если X валидно, иначе false.
     */
    private boolean validateX() {
        return x >= -3 && x <= 5;
    }

    /**
     * Проверяет корректность Y.
     * Y должно быть одним из значений: {-2, -1.5, -1, -0.5, 0, 0.5, 1, 1.5, 2}.
     *
     * @return true, если Y валидно, иначе false.
     */
    private boolean validateY() {
        double[] validYValues = {-2, -1.5, -1, -0.5, 0, 0.5, 1, 1.5, 2};
        for (double validY : validYValues)
            if (y == validY)
                return true;
        return false;
    }

    /**
     * Проверяет корректность R.
     * R должен находиться в диапазоне [2, 5].
     *
     * @return true, если R валидно, иначе false.
     */
    private boolean validateR() {
        return r >= 2 && r <= 5;
    }
}
