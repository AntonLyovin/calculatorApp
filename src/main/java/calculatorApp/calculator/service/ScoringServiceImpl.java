package calculatorApp.calculator.service;

import calculatorApp.calculator.model.dto.*;
import calculatorApp.calculator.util.EmploymentStatusEnum;
import calculatorApp.calculator.util.Gender;
import calculatorApp.calculator.util.MartialStatus;
import calculatorApp.calculator.util.Position;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Service
@ComponentScan
@Slf4j
public class ScoringServiceImpl implements ScoringService {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class ScoringResult {
        private BigDecimal rate;
        private boolean isApproved;
        private String rejectionReason;
    }

    @Override
    public CreditDto calculateCredit(ScoringDataDto data) {

        CreditDto result = new CreditDto();
        ScoringResult scoringResult = performScoring(data);

        if (!scoringResult.isApproved()) {
            throw new RuntimeException("Заявка отклонена: " + scoringResult.getRejectionReason());
        }
        log.info("Заявка одобрена");
        BigDecimal rate = scoringResult.getRate();
        BigDecimal psk = calculateTotalCost(data.getAmount(), rate);
        BigDecimal monthlyPayment = calculateMonthlyPayment(psk, data.getTerm(), rate);
        List<PaymentScheduleElementDto> schedule =
                generatePaymentSchedule(psk, data.getTerm(), performScoring(data).getRate(), monthlyPayment);


        result.setAmount(data.getAmount());
        result.setTerm(data.getTerm());
        result.setRate(performScoring(data).getRate());
        result.setPsk(calculateTotalCost(data.getAmount(), performScoring(data).getRate()));
        result.setIsSalaryClient(data.getIsSalaryClient());
        result.setIsInsuranceEnabled(data.getIsInsuranceEnabled());
        result.setPaymentSchedule(schedule);

        return result;
    }

    ScoringResult performScoring(ScoringDataDto data) {
        log.info("Начало скоринга");
        ScoringResult result = new ScoringResult();
        result.setApproved(true);

        BigDecimal baseRate = new BigDecimal("20");
        BigDecimal rate = baseRate;

        int age = calculateAge(data.getBirthdate());

        if (age < 20 || age > 65) {
            result.setApproved(false);
            result.setRejectionReason("Возраст вне допустимых границ");
            return result;
        }

        if (data.getMaritalStatus() == MartialStatus.MARRIED) {
            rate = rate.subtract(new BigDecimal("3"));
        } else if (data.getMaritalStatus() == MartialStatus.DIVORCED) {
            rate = rate.add(new BigDecimal("1"));
        }

        Gender gender = data.getGender();
        if (gender == Gender.FEMALE && age >= 32 && age <= 60) {
            rate = rate.subtract(new BigDecimal("3"));
        } else if (gender == Gender.MALE && age >= 30 && age <= 55) {
            rate = rate.subtract(new BigDecimal("3"));
        } else if (gender == Gender.NOT_BINARY) {
            rate = rate.add(new BigDecimal("7"));
        }

        EmploymentDto employment = data.getEmployment();
        if (employment != null) {
            int totalExperienceMonths = employment.getWorkExperienceTotal() != null ? employment.getWorkExperienceTotal() : 0;
            int currentExperienceMonths = employment.getGetWorkExperienceCurrent() != null ? employment.getGetWorkExperienceCurrent() : 0;

            if (totalExperienceMonths < 18 || currentExperienceMonths < 3) {
                result.setApproved(false);
                result.setRejectionReason("Недостаточный стаж работы");
                return result;
            }

            BigDecimal salary = employment.getSalary() != null ? employment.getSalary() : BigDecimal.ZERO;
            if (salary.compareTo(BigDecimal.ZERO) > 0 && data.getAmount().compareTo(salary.multiply(BigDecimal.valueOf(24))) > 0) {
                result.setApproved(false);
                result.setRejectionReason("Сумма займа превышает 24 зарплаты");
                return result;
            }

            int totalWorkExpMonths = employment.getWorkExperienceTotal() != null ? employment.getWorkExperienceTotal() : Integer.MAX_VALUE;
            if (totalWorkExpMonths < 18) {
                result.setApproved(false);
                result.setRejectionReason("Общий стаж менее 18 месяцев");
                return result;
            }


            EmploymentStatusEnum status = employment.getEmploymentStatus();
            if (status == EmploymentStatusEnum.UNEMPLOYED) {
                result.setApproved(false);
                result.setRejectionReason("Статус безработный");
                return result;
            } else if (status == EmploymentStatusEnum.SELF_EMPLOYED) {
                rate = rate.add(new BigDecimal("2"));
            } else if (status == EmploymentStatusEnum.BUSINESS_OWNER) {
                rate = rate.add(new BigDecimal("1"));
            }

            Position position = employment.getPosition();
            if (position == Position.MIDDLE_MANAGER) {
                rate = rate.subtract(new BigDecimal("2"));
            } else if (position == Position.TOP_MANAGER) {
                rate = rate.subtract(new BigDecimal("3"));
            }

        }

        result.setRate(rate.max(BigDecimal.ZERO));

        return result;
    }

    private int calculateAge(LocalDate birthdate) {
        log.info("Проверка возраста");
        return Period.between(birthdate, LocalDate.now()).getYears();
    }

    private BigDecimal calculateTotalCost(BigDecimal amount, BigDecimal ratePercent) {
        log.info("Расчет полной суммы кредита");
        return amount.multiply(BigDecimal.ONE.add(ratePercent.divide(BigDecimal.valueOf(100))));

    }

    private BigDecimal calculateMonthlyPayment(BigDecimal psk, int termMonths, BigDecimal annualRatePercent) {
        log.info("Расчет ежемесячного платежа");
        BigDecimal monthlyRate = annualRatePercent.divide(BigDecimal.valueOf(12 * 100), MathContext.DECIMAL128);

        BigDecimal numerator = psk.multiply(monthlyRate);
        BigDecimal denominatorFactor = BigDecimal.ONE.add(monthlyRate).pow(termMonths);
        denominatorFactor = BigDecimal.ONE.divide(denominatorFactor, MathContext.DECIMAL128);
        ;
        BigDecimal denominator = BigDecimal.ONE.subtract(denominatorFactor);
        return numerator.divide(denominator, MathContext.DECIMAL128);
    }

    private List<PaymentScheduleElementDto> generatePaymentSchedule(BigDecimal psk, int termMonths,
                                                                    BigDecimal annualRatePercent, BigDecimal monthlyPayment) {
        log.info("Генерация графика платежей");

        List<PaymentScheduleElementDto> schedule = new ArrayList<>();
        BigDecimal remainingDebt = psk;

        for (int month = 1; month <= termMonths; month++) {
            BigDecimal interestPart = remainingDebt.multiply(annualRatePercent.divide(BigDecimal.valueOf(12 * 100), MathContext.DECIMAL128));
            BigDecimal debtPart = monthlyPayment.subtract(interestPart);

            if (remainingDebt.compareTo(debtPart) < 0) {
                debtPart = remainingDebt;
                monthlyPayment = interestPart.add(debtPart);
            }

            remainingDebt = remainingDebt.subtract(debtPart);

            schedule.add(new PaymentScheduleElementDto(
                    month,
                    LocalDate.now().plusMonths(month),
                    monthlyPayment,
                    interestPart,
                    debtPart,
                    remainingDebt.max(BigDecimal.ZERO)
            ));
        }
        return schedule;
    }

}
