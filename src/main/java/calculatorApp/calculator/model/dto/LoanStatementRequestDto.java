package calculatorApp.calculator.model.dto;

import calculatorApp.calculator.util.Adult;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanStatementRequestDto {
//    @Max(1)
    @Schema(description = "Сумма кредита")
    @NotNull(message = "Сумма обязательна для заполнения")
    @DecimalMin(value = "20000", inclusive = true, message = "сумма должна быть не менее 20000")
    private BigDecimal amount;
    @Schema(description = "Срок кредита (в месяцах)")
    @NotNull(message = "Срок обязателен для заполнения")
    @Min(value = 6, message = "Срок должен не иенее 6 месяцев")
    private Integer term;
    @Schema(description = "Имя")
    @Size(min = 2, max = 30, message = "Имя должно быть от 2 до 30 латинских букв")
    @Pattern(regexp = "^[A-Za-z]{2,30}$", message = "Имя должно содержать только латинские буквы")
    private String firstName;
    @Schema(description = "Фамилия")
    @Size(min = 2, max = 30, message = "Фамилия должна быть от 2 до 30 латинских букв")
    @Pattern(regexp = "^[A-Za-z]{2,30}$", message = "Фамилия должна содержать только латинские буквы")
    private String lastName;
    @Schema(description = "Отчество (при наличии)")
    @Size(min = 2, max = 30, message = "Отчество должно быть от 2 до 30 латинских букв")
    @Pattern(regexp = "^[A-Za-z]{2,30}$", message = "Отчество должно содержать только латинские буквы")
    private String middleName;
    @Schema(description = "Email адрес")
    @Pattern(regexp = "^[a-z0-9A-Z_!#$%&'*+/=?`{|}~^.-]+@[a-z0-9A-Z.-]+$", message="Неправильный формат Email")
    private String email;
    @Schema(description = "Дата рождения (гггг-мм-дд)")
    @Adult
    private LocalDate birthdate;
    @Schema(description="Серия паспорта - 4 цифры")
    @Pattern(regexp="^\\d{4}$", message="Серия паспорта должна состоять из 4 цифр")
    private String passportSeries;
    @Schema(description="Номер паспорта - 6 цифр")
    @Pattern(regexp="^\\d{6}$", message="Номер паспорта должен состоять из 6 цифр")
    private String passportNumber;
}

