package moon.ticket.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class JsonReader {
    public static void main(String[] args) {
        String filePath = "/data/countries.json";

        try {
            // Инициализируем объект ObjectMapper из библиотеки Jackson
            ObjectMapper objectMapper = new ObjectMapper();

            // Считываем JSON-файл и создаем объект JsonNode
            JsonNode rootNode = objectMapper.readTree(new File(filePath));

            // Преобразуем JsonNode в Map
            Map<String, Map<String, Object>> countriesData = objectMapper.convertValue(rootNode, Map.class);

            // Вывод названий стран
            for (String country : countriesData.keySet()) {
                System.out.println("Country: " + country);
            }

            // Вывод городов в Украине
            Map<String, Object> ukraineData = countriesData.get("Ukraine");
            if (ukraineData != null) {
                System.out.println("Cities in Ukraine: " + ukraineData.get("city"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

