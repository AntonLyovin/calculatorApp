package calculatorApp.calculator.model.dto;

import calculatorApp.calculator.model.enumerated.EmploymentStatusEnum;
import calculatorApp.calculator.model.enumerated.Position;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmploymentDto {
    private EmploymentStatusEnum employmentStatus;
    private String employerINN;
    private BigDecimal salary;
    private Position position;
    private Integer workExperienceTotal;
    private Integer getWorkExperienceCurrent;
}
