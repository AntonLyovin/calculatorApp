package calculatorApp.calculator.controller;

import calculatorApp.calculator.model.dto.LoanOfferDto;
import calculatorApp.calculator.model.dto.LoanStatementRequestDto;
import calculatorApp.calculator.service.PreScoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Validated
@Tag(name = "PreScoringAPI", description = "Контроллер для расчета прескоринга")
@RequestMapping("/preScoring")
@Slf4j
public class PreScoringController {
    PreScoringService preScoringService;

    @PostMapping("/calculator/offers/{amount}/{term}/{firstName}/{lastName}/{middleName}/{email}/{birthdate}/{passportSeries}/{passportNumber}")
    @Operation(
            summary = "Расчет кредита",
            description = "Позволяет рассчитать предварительные условия кредита"
    )
    public List<LoanOfferDto> calculatePreOffer(
            @Parameter(description = "Сумма кредита") @PathVariable("amount") BigDecimal amount,
            @Parameter(description = "Срок кредита") @PathVariable("term") int term,
            @Parameter(description = "Имя") @PathVariable("firstName") String firstName,
            @Parameter(description = "Фамилия") @PathVariable("middleName") String middleName,
            @Parameter(description = "Отчество") @PathVariable("lastName") String lastName,
            @Parameter(description = "Email") @PathVariable("email") String email,
            @Parameter(description = "Дата рождения") @PathVariable("birthdate") LocalDate birthdate,
            @Parameter(description = "Серия паспорта") @PathVariable("passportSeries") String passportSeries,
            @Parameter(description = "Номер паспорта") @PathVariable("passportNumber") String passportNumber,
            @RequestBody @Parameter(description = "Параметры расчета") @Validated LoanStatementRequestDto requestDto) {
        log.info("Начало обработки условий займа");
        return preScoringService.calculatePreOffer(amount, term, requestDto.getFirstName(), requestDto.getLastName(), requestDto.getMiddleName(),
                requestDto.getEmail(), requestDto.getBirthdate(),requestDto.getPassportSeries(),requestDto.getPassportNumber());
    }


}
