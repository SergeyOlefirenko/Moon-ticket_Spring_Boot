package moon.ticket.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import moon.ticket.dto.JsonCountries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Data
@Slf4j
@Service
public class ExternalAPIService {

    private final RestTemplate restTemplate;
    private final String apiBaseUrl;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(ExternalAPIService.class);


    @Autowired
    public ExternalAPIService(RestTemplate restTemplate, @Value("${external.api.baseurl}") String apiBaseUrl, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.apiBaseUrl = apiBaseUrl;
        this.objectMapper = objectMapper;
    }


    public JsonCountries getCountries() {
        String countriesEndpoint = apiBaseUrl + "/countries";
        return fetchDataFromApi(countriesEndpoint, "countries");
    }

    private JsonCountries fetchDataFromApi(String endpoint, String dataType) {
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    endpoint,
                    HttpMethod.GET,
                    null,
                    String.class
            );
            String dataJson = responseEntity.getBody();
            return objectMapper.readValue(dataJson , JsonCountries.class);
        } catch (RestClientException e) {
            logger.error("Error while getting {} from API", dataType, e);
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}


