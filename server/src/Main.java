import com.fastcgi.FCGIInterface;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class Main {

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

            Dto dto = new Dto();
            dto.SetVal();

            InputValidator inputValidator = new InputValidator();
            if (!inputValidator.validateInput(dto.getX(), dto.getY(), dto.getR())) {
                sendErrorResponse400("Invalid input values. x must be in range [-3, 5], y must be one of {-2, -1.5, -1, ..., 2}, and R must be in range [2, 5].");
                return;
            }

            long startTime = System.nanoTime();

            boolean isPointInsideCheck = CheckPointPos.isPointInside(dto.getX(), dto.getY(), dto.getR());
            String result = isPointInsideCheck ? "True" : "False";

            long Time = (System.nanoTime() - startTime) / 1_000_000;

            RequestData requestData = new RequestData(dto.getX(), dto.getY(), dto.getR(), result, getCurrentTime(), Time);

            sendJsonResponse(requestData);
        }
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
