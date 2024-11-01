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
        FCGIInterface fcgi = new FCGIInterface();

        while (fcgi.FCGIaccept() >= 0) {
            try{
                String requestMethod = System.getProperties().getProperty("REQUEST_METHOD");
                if (!"GET".equals(requestMethod)) {
                    throw new Exception("Invalid request method");
                }
            }catch (Exception e){
                sendErrorResponse405("Поддерживаются только GET запросы");
            }
            
            parseParams();

            Dto dto = new Dto();

            dto.setAll(Double.parseDouble(params.get("x")), Double.parseDouble(params.get("y")), Double.parseDouble(params.get("r")));

            InputValidator inputValidator = new InputValidator();
            if (!inputValidator.validateInput(dto.getX(), dto.getY(), dto.getR())) {
                sendErrorResponse400("Invalid input values. x must be in range [-3, 5], y must be one of {-2, -1.5, -1, ..., 2}, and R must be in range [2, 5].");
                return;
            }

            long startTime = System.nanoTime();

            boolean isPointInside = isPointInside(dto.getX(), dto.getY(), dto.getR());
            String result = isPointInside ? "True" : "False";

            long Time = (System.nanoTime() - startTime) / 1_000_000;

            RequestData requestData = new RequestData(dto.getX(), dto.getY(), dto.getR(), result, getCurrentTime(), Time);

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

    public static boolean isPointLowerLeftQuarter(double x, double y, double r) {
        if (x <= 0 && y <= 0) {
            return (-r <= x && y >= -r && y <= -r/2);
        }
        return false;
    }

    public static boolean isPointLowerRightQuarter(double x, double y, double r) {
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
    private static void sendErrorResponse400(String errorMessage) {
        System.out.print("HTTP 1.0 400 Bad Request\n");
        System.out.print("Content-type: application/json\n\n");
        String jsonResponse = String.format("{\"error\": \"%s\"}", errorMessage);
        System.out.println(jsonResponse);
    }

    /**
     * Возвращает JSON сообщение об ошибке.
     *
     * @param errorMessage Сообщение.
     */
    private static void sendErrorResponse405(String errorMessage) {
        System.out.print("HTTP 1.0 405 Method not Allowed\n");
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
