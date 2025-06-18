package calculatorApp.calculator.service;

import calculatorApp.calculator.model.dto.LoanOfferDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PreScoringService {
    List<LoanOfferDto> calculatePreOffer(BigDecimal amount, Integer term, String firstName, String lastName, String middleName,
                                         String email, LocalDate birthdate, String passportSeries, String passportNumber);

}
