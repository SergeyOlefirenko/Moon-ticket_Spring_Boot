package moon.ticket.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import moon.ticket.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomValidator implements ConstraintValidator<CustomValidation, String> {
    @Autowired
    private PersonRepository personRepository;

    @Autowired
    public CustomValidator(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }
    public CustomValidator(){
    }

    @Override
    public void initialize(CustomValidation constraintAnnotation) {
        String message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return !personRepository.existsByEmail(value);
    }
}
