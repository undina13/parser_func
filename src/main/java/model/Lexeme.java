package model;

import lombok.Data;

@Data

public class Lexeme {
    private final LexemeType type;
    private final String value;

    public Lexeme(LexemeType type, Character value) {
        this.type = type;
        this.value = value.toString();
    }

    public Lexeme(LexemeType type, String value) {
        this.type = type;
        this.value = value;
    }
}
