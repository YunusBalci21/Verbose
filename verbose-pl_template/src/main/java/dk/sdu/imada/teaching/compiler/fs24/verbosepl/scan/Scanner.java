package dk.sdu.imada.teaching.compiler.fs24.verbosepl.scan;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

import dk.sdu.imada.teaching.compiler.fs24.verbosepl.VerbosePL;
import dk.sdu.imada.teaching.compiler.fs24.verbosepl.parse.Parser;

import static dk.sdu.imada.teaching.compiler.fs24.verbosepl.scan.TokenType.*;

public class Scanner {
    // Keyword-map
    private static final Map<String, TokenType> keywords;
    private StringBuilder errorStrings = new StringBuilder();

    public StringBuilder getErrorStrings() {
        return errorStrings;
    };

    static {
        keywords = new HashMap<>();
        keywords.put("and",    AND);
        keywords.put("class",  CLASS);
        keywords.put("else",   ELSE);
        keywords.put("false",  FALSE);
        keywords.put("for",    FOR);
        keywords.put("fun",    FUN);
        keywords.put("if",     IF);
        keywords.put("nil",    NIL);
        keywords.put("OR",     OR);
        keywords.put("print",  PRINT);
        keywords.put("return", RETURN);
        keywords.put("super",  SUPER);
        keywords.put("this",   THIS);
        keywords.put("true",   TRUE);
        keywords.put("var",    VAR);
        keywords.put("while",  WHILE);
        keywords.put("of_type", OF_TYPE);
        keywords.put("is", ASSIGN);
        keywords.put("Bool", BOOL_TYPE);
        keywords.put("Number", NUMBER_TYPE);
        keywords.put("String", STRING_TYPE);
        keywords.put("equals", EQUALS);
        keywords.put("NOT", NOT);
        keywords.put("less_than", LESS);
    }

    // In- and output
    private final String source;
    private final List<Token> tokens = new LinkedList<>();

    // Scanning state
    private int start = 0;
    private int current = 0;
    private int line = 1;

    public Scanner(String source) {
        this.source = source;
    }

    // Scan tokens
    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            // Set the start of the next lexeme
            start = current;  // Reset start to current before scanning the next token
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));  // Add the EOF token at the end
        return tokens;
    }


    private void scanToken() {
        char c = advance();
        System.out.println("Character: '" + c + "', Line: " + line);  // Debug print
        switch (c) {
            // Handle single-character tokens
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            case '/': addToken(DIV); break;

            // Handle double-character tokens like '!=' or '=='
            case '!': addToken(match('=') ? BANG_EQUAL : BANG); break;
            case '=': addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;
            case '<': addToken(match('=') ? LESS_EQUAL : LESS); break;
            case '>': addToken(match('=') ? GREATER_EQUAL : GREATER); break;

            // Handle hash for full-line comments
            case '#':
                while (peek() != '\n' && !isAtEnd()) advance();
                break;

            // Handle newlines
            case '\n':
                line++;  // Increment line number on newline
                break;

            // Handle strings, numbers, and identifiers
            case '"': string(); break;

            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    repportError(line, "Unexpected character.");
                }
                break;
        }
    }

    private void repportError(int line, String message) {
        errorStrings.append("Error at line ").append(line).append(": ").append(message).append("\n");
    }

    // For strings
    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        // Handle unterminated string
        if (isAtEnd()) {
            repportError(line, "Unterminated string.");
            return;
        }

        // The closing quote.
        advance();

        // Extract the string content
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);  // Assign the literal value
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private void number() {
        while (isDigit(peek())) advance();

        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();

            while (isDigit(peek())) advance();
        }

        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();
        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) {
            type = IDENTIFIER;
        }
        addToken(type);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));

        // Print in the correct format:
        System.out.println("<" + type + "," + text + "> Literal: " + literal + ", Line: " + line);
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    public static void main(String[] args) {
        // Define the input source as a string
        String source = "var x of_type Number is 10;\n";  // Example input with a manual newline for testing

        // Create a Scanner instance
        Scanner scanner = new Scanner(source);

        // Print the input source to verify
        System.out.println("Input: " + source);

        // Scan the tokens
        List<Token> tokens = scanner.scanTokens();

        // Print the tokens
        for (Token token : tokens) {
            System.out.println(token);  // This assumes your Token class has a proper toString() method
        }
    }
}
