package moon.ticket.configuration;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import moon.ticket.wrapper.LanguageWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ui.Model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Configuration
public class DataConfig {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public Map<String, Map<String, String>> languageData() {
        try {
            // Проверяем наличие файла data.json
            ClassPathResource resource = new ClassPathResource("/data/data.json");
            if (!resource.exists()) {
                throw new RuntimeException("File data.json not found");
            }
            // Считываем данные из файла
            InputStream is = resource.getInputStream();
            JavaType type = objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Map.class);
            Map<String, Map<String, String>> data = objectMapper.readValue(is, type);
            // Проверяем наличие данных в файле
            if (data == null || data.isEmpty()) {
                throw new RuntimeException("No data found in data.json");
            }
            return data;
        } catch (IOException e) {
            throw new RuntimeException("Error loading language data", e);
        }
    }

public void addAttributesToModel(Model model, LanguageWrapper lang) {
    Map<String, String> langData = languageData().get(lang.getLang());

        model.addAttribute("yourTicket", getValue(langData, "yourTicket"));
        model.addAttribute("checkTicket", getValue(langData, "checkTicket"));
        model.addAttribute("description", getValue(langData, "description"));
        model.addAttribute("firstName", getValue(langData, "firstName"));
        model.addAttribute("lastName", getValue(langData, "lastName"));
        model.addAttribute("email", getValue(langData, "email"));
        model.addAttribute("countryName", getValue(langData, "countryName"));
        model.addAttribute("locationName", getValue(langData, "locationName"));
        model.addAttribute("sendName", getValue(langData, "sendName"));
        model.addAttribute("agreement", getValue(langData, "agreement"));
        model.addAttribute("videoDescription", getValue(langData, "videoDescription"));
        model.addAttribute("main", getValue(langData, "main"));
        model.addAttribute("map", getValue(langData, "map"));
        model.addAttribute("faq", getValue(langData, "faq"));
        model.addAttribute("statistics", getValue(langData, "statistics"));
        model.addAttribute("totalSold", getValue(langData, "totalSold"));
        model.addAttribute("selectCountryName", getValue(langData, "selectCountryName"));
        model.addAttribute("greetings", getValue(langData, "greetings"));
        model.addAttribute("information", getValue(langData, "information"));
        model.addAttribute("moonTicket", getValue(langData, "moonTicket"));
        model.addAttribute("location", getValue(langData, "location"));
        model.addAttribute("locationParameter", getValue(langData, "locationParameter"));
        model.addAttribute("rocket", getValue(langData, "rocket"));
        model.addAttribute("rocketName", getValue(langData, "rocketName"));
        model.addAttribute("departure", getValue(langData, "departure"));
        model.addAttribute("arrival", getValue(langData, "arrival"));
        model.addAttribute("earthName", getValue(langData, "earthName"));
        model.addAttribute("moonName", getValue(langData, "moonName"));
        model.addAttribute("download", getValue(langData, "download"));
        model.addAttribute("faqGreetings", getValue(langData, "faqGreetings"));
        model.addAttribute("faqDescription", getValue(langData, "faqDescription"));
        model.addAttribute("checkAgain", getValue(langData, "checkAgain"));
        model.addAttribute("statusOk", getValue(langData, "statusOk"));
        model.addAttribute("statusBad", getValue(langData, "statusBad"));
        model.addAttribute("changeLanguage", getValue(langData, "changeLanguage"));
    }

    private String getValue(Map<String, String> langData, String key) {
        return langData.get(key);
    }


}

