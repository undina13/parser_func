package model;

import lombok.Data;

import java.util.List;

@Data
public class ParserModel {
    private String expression;

    private List<String> variablesList;

    private List<Double> valuesList;
}
