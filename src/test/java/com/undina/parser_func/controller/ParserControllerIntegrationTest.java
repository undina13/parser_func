package com.undina.parser_func.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.ParserModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ParserControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Test
    void getCalculationTestOk1() throws Exception {
        ParserModel parserModel = new ParserModel();
        parserModel.setExpression("abs(0.5 * A + 0.5 * B) < 1000");
        parserModel.setVariablesList(List.of("A", "B"));
        parserModel.setValuesList(List.of(500.0, 600.0));

        mockMvc.perform(post("/parser")
                        .content(mapper.writeValueAsString(parserModel))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().string("true"));
    }

    @Test
    void getCalculationTestOk2() throws Exception {
        ParserModel parserModel = new ParserModel();
        parserModel.setExpression("abs(0.5 * A + 0.5 * B) < 1000.0");
        parserModel.setVariablesList(List.of("A", "B"));
        parserModel.setValuesList(List.of(500.0, -6000.0));

        mockMvc.perform(post("/parser")
                        .content(mapper.writeValueAsString(parserModel))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().string("false"));
    }

    @Test
    void getCalculationTestOk3() throws Exception {
        ParserModel parserModel = new ParserModel();
        parserModel.setExpression("min(0.5 * A, abs(B)) > 100");
        parserModel.setVariablesList(List.of("A", "B"));
        parserModel.setValuesList(List.of(500.0, -6000.0));

        mockMvc.perform(post("/parser")
                        .content(mapper.writeValueAsString(parserModel))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().string("true"));
    }

    @Test
    void getCalculationTestOkSameNameVariableAndFunction() throws Exception {
        ParserModel parserModel = new ParserModel();
        parserModel.setExpression("abs(0.5 * ABS + 0.5 * B) < 1000");
        parserModel.setVariablesList(List.of("ABS", "B"));
        parserModel.setValuesList(List.of(500.0, 600.0));

        mockMvc.perform(post("/parser")
                        .content(mapper.writeValueAsString(parserModel))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().string("true"));
    }

    @Test
    void getCalculationTestFunctionException() throws Exception {
        ParserModel parserModel = new ParserModel();
        parserModel.setExpression("min(0.5 * A, abs(B, 67)) > 100");
        parserModel.setVariablesList(List.of("A", "B"));
        parserModel.setValuesList(List.of(500.0, -6000.0));

        mockMvc.perform(post("/parser")
                        .content(mapper.writeValueAsString(parserModel))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"error\":\"Wrong argument count for function abs\"}"));
    }

    @Test
    void getCalculationTestVariableNotExist() throws Exception {
        ParserModel parserModel = new ParserModel();
        parserModel.setExpression("min(0.5 * A, abs(B)) > 100");
        parserModel.setVariablesList(List.of("ABC", "B"));
        parserModel.setValuesList(List.of(500.0, -6000.0));

        mockMvc.perform(post("/parser")
                        .content(mapper.writeValueAsString(parserModel))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"error\":\"Variable A does not exist in the list\"}"));
    }

    @Test
    void getCalculationTestVariableListsNotEqualSize() throws Exception {
        ParserModel parserModel = new ParserModel();
        parserModel.setExpression("min(0.5 * A, abs(B)) > 100");
        parserModel.setVariablesList(List.of("A", "B"));
        parserModel.setValuesList(List.of(500.0, -6000.0, 89.0));

        mockMvc.perform(post("/parser")
                        .content(mapper.writeValueAsString(parserModel))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"error\":\"Sizes of the lists are not equal\"}"));
    }

    @Test
    void getCalculationTestOkEmptyLists() throws Exception {
        ParserModel parserModel = new ParserModel();
        parserModel.setExpression("abs(0.5 * 7 + 0.5 * 90) < 1000");
        parserModel.setVariablesList(Collections.emptyList());
        parserModel.setValuesList(Collections.emptyList());

        mockMvc.perform(post("/parser")
                        .content(mapper.writeValueAsString(parserModel))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().string("true"));
    }
}
