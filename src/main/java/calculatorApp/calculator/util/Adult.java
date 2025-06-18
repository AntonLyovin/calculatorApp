package calculatorApp.calculator.util;

import jakarta.validation.Constraint;
import org.springframework.messaging.handler.annotation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy=AdultValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Adult {
    String message() default "Пользователь должен быть старше 18 лет";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
