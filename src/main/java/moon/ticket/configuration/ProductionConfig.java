package moon.ticket.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("prod")
public class ProductionConfig {
    @Bean
    public String profileName() {
        return "Production";
    }
}
