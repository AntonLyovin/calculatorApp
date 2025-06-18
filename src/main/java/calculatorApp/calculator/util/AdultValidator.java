package calculatorApp.calculator.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class AdultValidator implements ConstraintValidator<Adult, LocalDate> {
    public void initialize(Adult constraint) {
    }

    public boolean isValid(LocalDate birthdate, ConstraintValidatorContext context) {
        if (birthdate == null) return false;
        return birthdate.isBefore(LocalDate.now().minusYears(18).plusDays(1));
    }
}
