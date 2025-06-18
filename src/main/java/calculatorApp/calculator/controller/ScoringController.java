package calculatorApp.calculator.controller;

import calculatorApp.calculator.model.dto.CreditDto;
import calculatorApp.calculator.model.dto.ScoringDataDto;
import calculatorApp.calculator.service.ScoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Validated
@Tag(name = "ScoringAPI", description = "Контроллер для расчета скоринга")
@RequestMapping("/scoring")
@Slf4j
public class ScoringController {

    private final ScoringService scoringService;

    @PostMapping("/calculator/calc")
    @Operation(
            summary = "Расчет кредита",
            description = "Принимает данные для скоринга и возвращает параметры кредита"
    )
    public CreditDto calculateCredit(@RequestBody @Parameter(description = "Данные для скоринга")@Validated ScoringDataDto data) {
        log.info("Начало расчета кредита");
        return scoringService.calculateCredit(data);
    }
}
