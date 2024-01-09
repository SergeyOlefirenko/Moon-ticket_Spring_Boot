package moon.ticket.datamodel;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;

@Data
@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @NotBlank(message = "Field 'firstName' cannot be empty")
    @Length(max = 50, message = "Length cannot exceed 50 characters")
    private String firstName;

    @NonNull
    @NotBlank(message = "Field 'lastName' cannot be empty")
    @Length(max = 50, message = "Length cannot exceed 50 characters")
    private String lastName;

    @NonNull
    @NotBlank(message = "Field 'email' cannot be empty")
    @Email(message = "Incorrect email format")
    private String email;

    @NonNull
    @NotBlank(message = "Field 'country' cannot be empty")
    @Length(min = 1, max = 50, message = "Length must be between 1 and 50 characters")
    private String country;

    @NonNull
    @NotBlank(message = "Field 'city' cannot be empty")
    @Length(min = 1, max = 50, message = "Length must be between 1 and 50 characters")
    private String city;

    @Enumerated(EnumType.STRING)
    private Status status;

    public Person() {
    }

}
