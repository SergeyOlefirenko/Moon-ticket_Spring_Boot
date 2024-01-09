package moon.ticket.configuration;

import moon.ticket.api.ExternalAPIService;
import moon.ticket.dto.JsonCountries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    @Autowired
    private ExternalAPIService externalAPIService;

    @Bean
    public JsonCountries jsonCountries() {
        return externalAPIService.getCountries();
    }
}
