package com.undina.parser_func.service;

import com.undina.parser_func.exception.CalculationException;
import com.undina.parser_func.exception.VariableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.Lexeme;
import model.LexemeBuffer;
import model.LexemeType;
import model.ParserModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ParserService {
    private final HashMap<String, Function> functionMap = FunctionMap.getFunctionMap();

    public Boolean getCalculation(ParserModel parserModel) {
        String[] parseExpression;
        boolean isLess = false;

        if (parserModel.getExpression().contains("<")) {
            parseExpression = parserModel.getExpression().split("<");
            isLess = true;
        } else {
            parseExpression = parserModel.getExpression().split(">");
        }

        String expression = parseExpression[0];
        double valueExpression = Double.parseDouble(parseExpression[1]);
        log.info("valueExpression  {}", valueExpression);
        List<Lexeme> lexemes = lexAnalyze(expression,
                getVariablesMap(parserModel.getVariablesList(), parserModel.getValuesList()));
        LexemeBuffer lexemeBuffer = new LexemeBuffer(lexemes);
        double result = expr(lexemeBuffer);
        log.info("result  {}", result);

        if (isLess) {
            return result < valueExpression;
        } else {
            return result > valueExpression;
        }
    }

    private List<Lexeme> lexAnalyze(String expText, Map<String, Double> variablesMap) {
        ArrayList<Lexeme> lexemes = new ArrayList<>();
        int pos = 0;
        while (pos < expText.length()) {
            char c = expText.charAt(pos);
            switch (c) {
                case '(':
                    lexemes.add(new Lexeme(LexemeType.LEFT_BRACKET, c));
                    pos++;
                    continue;
                case ')':
                    lexemes.add(new Lexeme(LexemeType.RIGHT_BRACKET, c));
                    pos++;
                    continue;
                case '+':
                    lexemes.add(new Lexeme(LexemeType.OP_PLUS, c));
                    pos++;
                    continue;
                case '-':
                    lexemes.add(new Lexeme(LexemeType.OP_MINUS, c));
                    pos++;
                    continue;
                case '*':
                    lexemes.add(new Lexeme(LexemeType.OP_MUL, c));
                    pos++;
                    continue;
                case '/':
                    lexemes.add(new Lexeme(LexemeType.OP_DIV, c));
                    pos++;
                    continue;
                case ',':
                    lexemes.add(new Lexeme(LexemeType.COMMA, c));
                    pos++;
                    continue;
                default:
                    if (c <= '9' && c >= '0' || c == '.') {
                        StringBuilder sb = new StringBuilder();
                        do {
                            sb.append(c);
                            pos++;
                            if (pos >= expText.length()) {
                                break;
                            }
                            c = expText.charAt(pos);
                        } while (c <= '9' && c >= '0' || c == '.');
                        lexemes.add(new Lexeme(LexemeType.NUMBER, sb.toString()));
                    } else {
                        if (c != ' ') {
                            if (c >= 'A' && c <= 'Z') {
                                StringBuilder sb = new StringBuilder();
                                do {
                                    sb.append(c);
                                    pos++;
                                    if (pos >= expText.length()) {
                                        break;
                                    }
                                    c = expText.charAt(pos);
                                } while (c >= 'A' && c <= 'Z');
                                if (!variablesMap.containsKey(sb.toString())) {
                                    throw new VariableException("Variable " + sb + " does not exist in the list");
                                }
                                lexemes.add(new Lexeme(LexemeType.NUMBER, variablesMap.get(sb.toString()).toString()));
                                log.info("VARIABLE  {}", sb);
                            } else if (c >= 'a' && c <= 'z') {
                                StringBuilder sb = new StringBuilder();
                                do {
                                    sb.append(c);
                                    pos++;
                                    if (pos >= expText.length()) {
                                        break;
                                    }
                                    c = expText.charAt(pos);
                                } while (c >= 'a' && c <= 'z');
                                log.info("NAME  {}", sb);
                                if (functionMap.containsKey(sb.toString())) {
                                    lexemes.add(new Lexeme(LexemeType.NAME, sb.toString()));
                                } else {
                                    throw new CalculationException("Unexpected character: " + c);
                                }
                            }
                        } else {
                            pos++;
                        }
                    }
            }
        }
        lexemes.add(new Lexeme(LexemeType.EOF, ""));

        return lexemes;
    }

    private double expr(LexemeBuffer lexemes) {
        Lexeme lexeme = lexemes.next();
        if (lexeme.getType() == LexemeType.EOF) {
            return 0;
        } else {
            lexemes.back();
            return plusminus(lexemes);
        }
    }

    private double plusminus(LexemeBuffer lexemes) {
        double value = multdiv(lexemes);
        while (true) {
            Lexeme lexeme = lexemes.next();
            switch (lexeme.getType()) {
                case OP_PLUS:
                    value += multdiv(lexemes);
                    break;
                case OP_MINUS:
                    value -= multdiv(lexemes);
                    break;
                case EOF:
                case RIGHT_BRACKET:
                case COMMA:
                    lexemes.back();
                    return value;
                default:
                    throw new CalculationException("Unexpected token: " + lexeme.getValue()
                            + " at position: " + lexemes.getPos());
            }
        }
    }

    private double multdiv(LexemeBuffer lexemes) {
        double value = factor(lexemes);
        while (true) {
            Lexeme lexeme = lexemes.next();
            switch (lexeme.getType()) {
                case OP_MUL:
                    value *= factor(lexemes);
                    break;
                case OP_DIV:
                    value /= factor(lexemes);
                    break;
                case EOF:
                case RIGHT_BRACKET:
                case COMMA:
                case OP_PLUS:
                case OP_MINUS:
                    lexemes.back();
                    return value;
                default:
                    throw new CalculationException("Unexpected token: " + lexeme.getValue()
                            + " at position: " + lexemes.getPos());
            }
        }
    }

    private double factor(LexemeBuffer lexemes) {
        Lexeme lexeme = lexemes.next();
        switch (lexeme.getType()) {
            case NAME:
                lexemes.back();
                return func(lexemes);
            case OP_MINUS:
                double value = factor(lexemes);
                return -value;
            case NUMBER:
                return Double.parseDouble(lexeme.getValue());
            case LEFT_BRACKET:
                value = plusminus(lexemes);
                lexeme = lexemes.next();
                if (lexeme.getType() != LexemeType.RIGHT_BRACKET) {
                    throw new CalculationException("Unexpected token: " + lexeme.getValue()
                            + " at position: " + lexemes.getPos());
                }
                return value;
            default:
                throw new CalculationException("Unexpected token: " + lexeme.getValue()
                        + " at position: " + lexemes.getPos());
        }
    }

    private double func(LexemeBuffer lexemeBuffer) {
        String name = lexemeBuffer.next().getValue();
        Lexeme lexeme = lexemeBuffer.next();

        if (lexeme.getType() != LexemeType.LEFT_BRACKET) {
            throw new CalculationException("Wrong function call syntax at " + lexeme.getValue());
        }

        ArrayList<Double> args = new ArrayList<>();

        lexeme = lexemeBuffer.next();
        if (lexeme.getType() != LexemeType.RIGHT_BRACKET) {
            lexemeBuffer.back();
            do {
                args.add(expr(lexemeBuffer));
                lexeme = lexemeBuffer.next();

                if (lexeme.getType() != LexemeType.COMMA && lexeme.getType() != LexemeType.RIGHT_BRACKET) {
                    throw new CalculationException("Wrong function call syntax at " + lexeme.getValue());
                }

            } while (lexeme.getType() == LexemeType.COMMA);
        }
        return functionMap.get(name).apply(args);
    }

    private Map<String, Double> getVariablesMap(List<String> variablesList, List<Double> valuesList) {
        if (variablesList.size() != valuesList.size()) {
            throw new VariableException("Sizes of the lists are not equal");
        }
        Map<String, Double> variablesMap = new HashMap<>();
        for (int i = 0; i < variablesList.size(); i++) {
            variablesMap.put(variablesList.get(i), valuesList.get(i));
        }
        return variablesMap;
    }
}
