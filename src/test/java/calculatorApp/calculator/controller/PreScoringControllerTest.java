package calculatorApp.calculator.controller;

import calculatorApp.calculator.service.PreScoringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PreScoringControllerTest {
    @Mock
    private PreScoringService preScoringService;

    @InjectMocks
    private PreScoringController preScoringController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(preScoringController).build();
    }

    double amount = 100000;
    int term = 12;
    String firstName = "Ivan";
    String lastName = "Ivanov";
    String middleName = "Ivanovich";
    String email = "ivanov@example.com";
    String birthdate = "1990-01-01";
    String passportSeries = "4444";
    String passportNumber = "123456";

    @Test
    void calculatePreOfferTest() throws Exception {
        mockMvc.perform(post("/preScoring/calculator/offers/{amount}/{term}/{firstName}/{lastName}/{middleName}/{email}/{birthdate}/{passportSeries}/{passportNumber}",
                        amount, term, firstName, lastName, middleName, email, birthdate, passportSeries, passportNumber))
                .andExpect(status().isOk());
    }
    @Test
    void calculatePreOfferValidationTest() throws Exception{
        mockMvc.perform(post("/preScoring/calculator/offers/{amount}/{term}/{firstName}/{lastName}/{middleName}/{email}/{birthdate}/{passportSeries}/{passportNumber}",
                        amount, term, firstName, lastName, middleName, email, birthdate, passportSeries, passportNumber))
                .andExpect(status().isBadRequest());
    }

}