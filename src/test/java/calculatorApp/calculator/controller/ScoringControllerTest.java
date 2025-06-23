package calculatorApp.calculator.controller;

import calculatorApp.calculator.service.PreScoringService;
import calculatorApp.calculator.service.ScoringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ScoringControllerTest {
    @Mock
    private ScoringService scoringService;
    @Mock
    private PreScoringService preScoringService;

    @InjectMocks
    private ScoringController scoringController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(scoringController).build();
    }

    @Test
    void calculateOfferTest() throws Exception {
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

        mockMvc.perform(post("/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    void calculateOfferValidationTest() throws Exception {
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

        mockMvc.perform(post("/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void calculatePreOfferTest() throws Exception {
        String requestBody = "{"
                + "\"amount\":200000,"
                + "\"term\":12,"
                + "\"firstName\":\"an\","
                + "\"lastName\":\"lez\","
                + "\"middleName\":\"dre\","
                + "\"email\":\"Ivanov@mail.ru\","
                + "\"birthdate\":\"1995-06-17\","
                + "\"passportSeries\":\"0374\","
                + "\"passportNumber\":\"492684\""
                + "}";
        mockMvc.perform(post("/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    void calculatePreOfferValidationTest() throws Exception {
        String requestBody = "{"
                + "\"amount\":200000,"
                + "\"term\":12,"
                + "\"firstName\":\"an\","
                + "\"lastName\":\"lez\","
                + "\"middleName\":\"dre\","
                + "\"email\":\"Ivanov@mail.ru\","
                + "\"birthdate\":\"1995-06-17\","
                + "\"passportSeries\":\"074\","
                + "\"passportNumber\":\"492684\""
                + "}";
        mockMvc.perform(post("/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
    @Test
    void calculatePreOfferWithInvalidDataTest() throws Exception {
        String invalidRequestBody = "{"
                + "\"amount\":1000,"
                + "\"term\":3,"
                + "\"firstName\":\"фыаф\", "
                + "\"lastName\":\"ывф\", "
                + "\"middleName\":\"вфф\", "
                + "\"email\":\"invalid-email\","
                + "\"birthdate\":\"1990-13-01\"," // некорректная дата (13 месяц)
                + "\"passportSeries\":\"12\","
                + "\"passportNumber\":\"12345\""
                + "}";

        mockMvc.perform(post("/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field=='amount')].message").value("сумма должна быть не менее 20000"))
                .andExpect(jsonPath("$.term").value("Срок должен не иенее 6 месяцев"))
                .andExpect(jsonPath("$.firstName").value("Имя должно быть от 2 до 30 латинских букв"))
                .andExpect(jsonPath("$.lastName").value("Фамилия должна быть от 2 до 30 латинских букв"))
                .andExpect(jsonPath("$.middleName").doesNotExist()) // так как поле опционально, можно пропустить или проверить на null
                .andExpect(jsonPath("$.email").value("Неправильный формат Email"))
                .andExpect(jsonPath("$.birthdate").value("Некорректная дата рождения")) // если есть кастомная обработка ошибок для даты
                .andExpect(jsonPath("$.passportSeries").value("Серия паспорта должна состоять из 4 цифр"))
                .andExpect(jsonPath("$.passportNumber").value("Номер паспорта должен состоять из 6 цифр"));

        MvcResult result = mockMvc.perform(post("/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody))
                .andReturn();

        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Content: " + result.getResponse().getContentAsString());


    }
}