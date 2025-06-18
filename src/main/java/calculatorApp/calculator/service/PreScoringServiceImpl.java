package calculatorApp.calculator.service;

import calculatorApp.calculator.model.dto.LoanOfferDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PreScoringServiceImpl implements PreScoringService {
    @Override
    public List<LoanOfferDto> calculatePreOffer(BigDecimal amount, Integer term, String firstName, String lastName, String middleName,
                                                String email, LocalDate birthdate,String passportSeries,String passportNumber) {
        List<CalcDto> calcDTOList = prepareDtoList(amount, term);
        log.info("Предоставление кредитных предложений");
        return calcDTOList.stream()
                .map(this::calculatePreScoring)
                .sorted(Comparator.comparing(LoanOfferDto::getRate))
                .collect(Collectors.toList());
    }

    private LoanOfferDto calculatePreScoring(CalcDto calcDto) {
        log.info("Добавление резульатов расчета прескоринга");
        LoanOfferDto result = new LoanOfferDto();

        BigDecimal annualRate = BigDecimal.valueOf(20);
        if (calcDto.getIsInsuranceEnabled()) {
            annualRate = annualRate.subtract(BigDecimal.valueOf(1));
            calcDto.setAmount(calcDto.getAmount().add(BigDecimal.valueOf(50000)));
        }

        if (calcDto.getIsSalaryClient()) {
            annualRate = annualRate.subtract(BigDecimal.valueOf(1));
        }

        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(12), MathContext.DECIMAL128).divide(BigDecimal.valueOf(100), MathContext.DECIMAL128);

        Integer termInYears = calcDto.getTerm();
        BigDecimal termInYearsBigDecimal = BigDecimal.valueOf(termInYears);

        BigDecimal numberOfPayments = termInYearsBigDecimal.multiply(BigDecimal.valueOf(12)
        );

        BigDecimal one = BigDecimal.ONE;
        BigDecimal pow = (one.add(monthlyRate)).pow(numberOfPayments.intValue());
        BigDecimal numerator = monthlyRate.multiply(pow);
        BigDecimal denominator = pow.subtract(one);
        BigDecimal monthlyPayment = calcDto.getAmount().multiply(numerator).divide(denominator, MathContext.DECIMAL128);


        result.setRequestedAmount(calcDto.getAmount());
        result.setTotalAmount(calcDto.getAmount());
        result.setIsSalaryClient(calcDto.isSalaryClient);
        result.setIsInsuranceEnabled(calcDto.isInsuranceEnabled);
        result.setTerm(calcDto.getTerm());
        result.setRate(annualRate);
        result.setMonthlyPayment(monthlyPayment);
        return result;
    }

    private List<CalcDto> prepareDtoList(BigDecimal amount, Integer term) {
        log.info("Подготовка кредитных предложений");
        List<CalcDto> result = new ArrayList<>();
        result.add(CalcDto.builder().amount(amount).term(term).isInsuranceEnabled(false).isSalaryClient(false).build());
        result.add(CalcDto.builder().amount(amount).term(term).isInsuranceEnabled(false).isSalaryClient(true).build());
        result.add(CalcDto.builder().amount(amount).term(term).isInsuranceEnabled(true).isSalaryClient(true).build());
        result.add(CalcDto.builder().amount(amount).term(term).isInsuranceEnabled(true).isSalaryClient(false).build());
        return result;
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Builder
    static class CalcDto {
        BigDecimal amount;
        Integer term;
        Boolean isInsuranceEnabled;
        Boolean isSalaryClient;
    }
}
