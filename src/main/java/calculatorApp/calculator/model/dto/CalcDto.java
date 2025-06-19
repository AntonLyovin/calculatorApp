package calculatorApp.calculator.model.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CalcDto {
    BigDecimal amount;
    Integer term;
    Boolean isInsuranceEnabled;
    Boolean isSalaryClient;
}