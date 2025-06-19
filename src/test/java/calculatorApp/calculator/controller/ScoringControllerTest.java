package calculatorApp.calculator.controller;

import calculatorApp.calculator.service.ScoringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ScoringControllerTest {
    @Mock
    private ScoringService scoringService;

    @InjectMocks
    private ScoringController scoringController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(scoringController).build();
    }

    @Test
    void calculatePreOfferTest() throws Exception {
        String requestBody = "{"
                + "\"amount\":200000,"
                + "\"term\":12,"
                + "\"firstName\":\"an\","
                + "\"lastName\":\"lez\","
                + "\"middleName\":\"dre\","
                + "\"gender\":\"MALE\","
                + "\"birthdate\":\"1995-06-17\","
                + "\"passportSeries\":\"0374\","
                + "\"passportNumber\":\"492684\","
                + "\"passportIssueDate\":\"2020-06-17\","
                + "\"passportIssueBranch\":\"string\","
                + "\"maritalStatus\":\"SINGLE\","
                + "\"dependentAmount\":0,"
                + "\"employment\":{"
                + "\"employmentStatus\":\"SELF_EMPLOYED\","
                + "\"employerINN\":\"string\","
                + "\"salary\":300000,"
                + "\"position\":\"DIRECTOR\","
                + "\"getWorkExperienceCurrent\":20,"
                + "\"workExperienceTotal\":20"
                + "},"
                + "\"accountNumber\":\"string\","
                + "\"isInsuranceEnabled\":true,"
                + "\"isSalaryClient\":true"
                + "}";

        mockMvc.perform(post("/scoring/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    void calculateOfferValidationTest() throws Exception{
        String requestBody = "{"
                + "\"amount\":200000,"
                + "\"term\":12,"
                + "\"firstName\":\"an\","
                + "\"lastName\":\"lez\","
                + "\"middleName\":\"dre\","
                + "\"gender\":\"MALE\","
                + "\"birthdate\":\"1995-06-17\","
                + "\"passportSeries\":\"037\","
                + "\"passportNumber\":\"492684\","
                + "\"passportIssueDate\":\"2020-06-17\","
                + "\"passportIssueBranch\":\"string\","
                + "\"maritalStatus\":\"SINGLE\","
                + "\"dependentAmount\":0,"
                + "\"employment\":{"
                + "\"employmentStatus\":\"SELF_EMPLOYED\","
                + "\"employerINN\":\"string\","
                + "\"salary\":300000,"
                + "\"position\":\"DIRECTOR\","
                + "\"getWorkExperienceCurrent\":20,"
                + "\"workExperienceTotal\":20"
                + "},"
                + "\"accountNumber\":\"string\","
                + "\"isInsuranceEnabled\":true,"
                + "\"isSalaryClient\":true"
                + "}";

        mockMvc.perform(post("/scoring/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
    @Test
    void calculatePreOfferTest() throws Exception {
        mockMvc.perform(post("/calculator/offers)
                .andExpect(status().isOk());
    }
    @Test
    void calculatePreOfferValidationTest() throws Exception{
        mockMvc.perform(post("/preScoring/calculator/offers/{amount}/{term}/{firstName}/{lastName}/{middleName}/{email}/{birthdate}/{passportSeries}/{passportNumber}",
                        amount, term, firstName, lastName, middleName, email, birthdate, passportSeries, passportNumber))
                .andExpect(status().isBadRequest());
    }
}
