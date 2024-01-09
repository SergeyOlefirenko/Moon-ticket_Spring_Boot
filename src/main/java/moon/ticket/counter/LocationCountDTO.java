package moon.ticket.counter;

import lombok.Data;

@Data
public class LocationCountDTO {
    private String country;
    private String city;
    private Long count;

    public LocationCountDTO(String city, Long count) {
        this.city = city;
        this.count = count;
    }
}

