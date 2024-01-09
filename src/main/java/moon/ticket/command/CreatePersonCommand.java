package moon.ticket.command;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import moon.ticket.datamodel.Status;
import moon.ticket.datamodel.Person;
import moon.ticket.validation.CustomValidation;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class CreatePersonCommand {
    @NonNull
    @NotBlank(message = "Field cannot be empty")
    @Length(max = 50)
    private String firstName;

    @NonNull
    @NotBlank(message = "Field cannot be empty")
    @Length(max = 50)
    private String lastName;

    @NonNull
    @NotBlank(message = "Field cannot be empty")
    @CustomValidation
    @Pattern(regexp = "^[^@]+@[^@]+$", message = "Email should not start with @ and must contain @")
    @Email(message = "Incorrect email format")
    private String email;

    @NonNull
    @NotBlank(message = "Field cannot be empty")
    @Length(min = 1, max = 50, message = "Length must be between 1 and 50 characters")
    private String country;

    @NonNull
    @NotBlank(message = "Field cannot be empty")
    @Length(min = 1, max = 50, message = "Length must be between 1 and 50 characters")
    private String city;

    @Enumerated(EnumType.STRING)
    private Status status;


    public boolean isRegistered() {
        return email != null && !email.isEmpty();
    }
    public Person toEntity() {
        Person person = new Person();
        person.setFirstName(this.firstName.toUpperCase());
        person.setLastName(this.lastName.toUpperCase());
        person.setEmail(email);
        person.setCountry(country.toUpperCase());
        person.setCity(city.toUpperCase());
        person.setStatus(isRegistered() ? Status.CONFIRMED : Status.NOT_CONFIRMED);
        return person;
    }
}


