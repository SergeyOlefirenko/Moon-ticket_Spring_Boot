package moon.ticket.api;

import moon.ticket.dto.JsonCountries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ApiController {
    private final JsonCountries jsonCountries;
    @Autowired
    public ApiController(JsonCountries jsonCountries) {
        this.jsonCountries = jsonCountries;
    }

    @GetMapping("/filtered-data")
    public ResponseEntity<Map<String, List<String>>> getFilteredData() {
        // Получаем список стран
        // Формируем ответ с пустым списком городов (будут добавлены динамически)
        Map<String, List<String>> response = new HashMap<>();
        jsonCountries.getData().stream().map(o -> response.put(o.getCountry() , o.getCities())).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filtered-data/cities")
    public ResponseEntity<List<String>> getCitiesByCountry(@RequestParam(name = "country") String country) {
        var countryWithCity = jsonCountries.getData().stream().filter(c-> c.getCountry().equals(country)).findFirst();
        return ResponseEntity.ok(countryWithCity.get().getCities());
    }
}

