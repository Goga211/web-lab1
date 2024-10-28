import com.fastcgi.FCGIInterface;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class Main {

    /**
     * Параметры запроса.
     */
    private static final HashMap<String, String> params = new HashMap<>();

    /**
     * Метод обработки запроса.
     */
    public static void main(String[] args) {
        long startTime = System.nanoTime();
        FCGIInterface fcgi = new FCGIInterface();

        while (fcgi.FCGIaccept() >= 0) {
            parseParams();

            double x = Double.parseDouble(params.get("x"));
            double y = Double.parseDouble(params.get("y"));
            double r = Double.parseDouble(params.get("r"));

            InputValidator inputValidator = new InputValidator(x, y, r);
            if (!inputValidator.validateInput()) {
                sendErrorResponse("Invalid input values. x must be in range [-3, 5], y must be one of {-2, -1.5, -1, ..., 2}, and R must be in range [2, 5].");
                return;
            }

            boolean isPointInside = isPointInside(x, y, r);
            String result = isPointInside ? "Point is inside the graph" : "Point is outside the graph";

            RequestData requestData = new RequestData(x, y, r, result, getCurrentTime(), getExecutionTime(startTime));

            sendJsonResponse(requestData);
        }
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

    /**
     * Метод для проверки того, что точка лежит в окружности.
     *
     * @param x Координата по оси x.
     * @param y Координата по оси y.
     * @param r Радиус окружности.
     *
     * @return Лежит ли точка в окружности.
     */
    private static boolean isPointInside(double x, double y, double r) {
        if(x <= 0 && y >= 0){
            return (x * x + y * y <= r * r);
        }else if (x >= 0 && y >= 0) {
            return (r/2 >= y && r/2 >= x && y <= x);
        }else if (x <= 0 && y <= 0) {
            return (-r <= x && y >= -r && y <= -r/2);
        }
        return false;
    }

    /**
     * Возвращает JSON ответ.
     *
     * @param requestData Данные запроса.
     */
    private static void sendJsonResponse(RequestData requestData) {
        System.out.println("Content-type: application/json\n\n");
        String jsonResponse = requestData.toJson();
        System.out.println(jsonResponse);
    }

    /**
     * Возвращает JSON сообщение об ошибке.
     *
     * @param errorMessage Сообщение.
     */
    private static void sendErrorResponse(String errorMessage) {
        System.out.print("HTTP 1.0 400 Bad Request\n");
        System.out.print("Content-type: application/json\n\n");
        String jsonResponse = String.format("{\"error\": \"%s\"}", errorMessage);
        System.out.println(jsonResponse);
    }

    /**
     * Возвращает текущее время в формате ISO.
     *
     * @return Текущее время.
     */
    private static String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    /**
     * Возвращает время работы скрипта в миллисекундах.
     *
     * @param startTime Время начала работы скрипта.
     * @return Время работы скрипта в миллисекундах.
     */
    private static long getExecutionTime(long startTime) {
        return (System.nanoTime() - startTime) / 1_000_000;
    }

    /**
     * Запись для хранения результата запроса.
     *
     * @param x
     * @param y
     * @param r
     * @param result
     * @param currentTime
     * @param executionTime
     */
    private record RequestData(double x, double y, double r, String result, String currentTime, long executionTime) implements Serializable {
        public String toJson() {
            return String.format(java.util.Locale.US,
                    "{\"x\": %.2f, \"y\": %.2f, \"r\": %.2f, \"result\": \"%s\", \"current_time\": \"%s\", \"execution_time\": \"%d ms\"}",
                    x, y, r, result, currentTime, executionTime);
        }
    }
}
