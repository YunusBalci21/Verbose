package dk.sdu.imada.teaching.compiler.fs24.verbosepl.parse;

import dk.sdu.imada.teaching.compiler.fs24.verbosepl.scan.Token;
import dk.sdu.imada.teaching.compiler.fs24.verbosepl.scan.TokenType;
import static dk.sdu.imada.teaching.compiler.fs24.verbosepl.scan.TokenType.*;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

import dk.sdu.imada.teaching.compiler.fs24.verbosepl.ast.Stmt;
import dk.sdu.imada.teaching.compiler.fs24.verbosepl.ast.visitors.Expr;

public class Parser {

    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Stmt> parse() {
        List<Stmt> statements = new LinkedList<>();
        while (!isAtEnd()) {
            Stmt stmt = declaration();
            if (stmt != null) {
                statements.add(stmt);
            }
        }
        return statements;
    }

    private Expr expression() {
        return assignment();
    }

    private Stmt declaration() {
        try {
            if (match(VAR)) return varDeclaration();
            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Stmt statement() {
        if (match(WHILE)) return whileStatement();  // Add this line to handle 'while' loops
        if (match(IF)) return ifStatement();
        if (match(PRINT)) return printStatement();
        if (match(LEFT_BRACE)) return new Stmt.Block(block());
        return expressionStatement();
    }

    private Stmt ifStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'if'.");
        Expr condition = expression();  // Parse the condition expression
        consume(RIGHT_PAREN, "Expect ')' after if condition.");  // Ensure the closing parenthesis is consumed
        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (match(ELSE)) {
            elseBranch = statement();  // Parse the 'else' branch if it exists
        }
        return new Stmt.If(condition, thenBranch, elseBranch);
    }

    private Stmt whileStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'while'.");
        Expr condition = expression();  // Parse the loop condition
        consume(RIGHT_PAREN, "Expect ')' after condition.");
        Stmt body = statement();  // Parse the loop body
        return new Stmt.While(condition, body);
    }

    private Stmt printStatement() {
        Expr value = expression();
        consume(SEMICOLON, "Expect ';' after value.");
        return new Stmt.Print(value);
    }

    private Stmt varDeclaration() {
        Token name = consume(IDENTIFIER, "Expect variable name.");
        Token ofType = consume(OF_TYPE, "Expect 'of_type' after variable name.");

        Token type;
        if (match(BOOL_TYPE, NUMBER_TYPE, STRING_TYPE)) {
            type = previous();
        } else {
            throw error(peek(), "Expect type name (Bool, Number, String) after 'of_type'.");
        }

        Expr initializer = null;
        if (match(ASSIGN)) {
            initializer = expression();
        }

        consume(SEMICOLON, "Expect ';' after variable declaration.");  // Ensure semicolon consumption

        return new Stmt.Var(name, type, initializer);
    }


    private Stmt expressionStatement() {
        Expr expr = expression();  // Parse the expression
        consume(SEMICOLON, "Expect ';' after expression.");  // Ensure the semicolon is consumed
        return new Stmt.Expression(expr);
    }

    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();
        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            Stmt stmt = declaration();  // Parse each statement in the block
            if (stmt != null) {
                statements.add(stmt);
            }
        }
        consume(RIGHT_BRACE, "Expect '}' after block.");  // Ensure closing brace is consumed
        return statements;
    }
    private Expr assignment() {
        Expr expr = logicalOr();

        if (match(ASSIGN)) {
            Token equals = previous();
            Expr value = assignment();  // Recursively parse the right-hand side of the assignment

            if (expr instanceof Expr.Variable) {
                return new Expr.Assignment(((Expr.Variable) expr).name, value);
            }
            error(equals, "Invalid assignment target.");
        }

        return expr;
    }

    private Expr logicalOr() {
        Expr expr = logicalAnd();

        while (match(TokenType.OR)) {
            Token operator = previous();
            Expr right = logicalAnd();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    private Expr logicalAnd() {
        Expr expr = equality();

        while (match(TokenType.AND)) {
            Token operator = previous();
            Expr right = equality();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    private Expr equality() {
        Expr expr = comparison();  // Parse left-hand side of comparison

        while (match(EQUAL_EQUAL, BANG_EQUAL)) {  // Matches '==' or '!='
            Token operator = previous();
            Expr right = comparison();  // Parse right-hand side of comparison
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }
    private Expr comparison() {
        Expr expr = term();

        while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr term() {
        Expr expr = factor();

        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr factor() {
        Expr expr = unary();

        while (match(STAR, SLASH)) {  // Handles multiplication and division
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() {
        if (match(BANG, MINUS, NOT)) {  // Add `NOT` here
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }
        return primary();
    }

    private Expr primary() {
        if (match(TokenType.FALSE)) return new Expr.Literal(false);
        if (match(TokenType.TRUE)) return new Expr.Literal(true);
        if (match(TokenType.NUMBER)) return new Expr.Literal(previous().literal);
        if (match(TokenType.STRING)) return new Expr.Literal(previous().literal);

        if (match(TokenType.IDENTIFIER)) {
            return new Expr.Variable(previous());
        }

        if (match(TokenType.LEFT_PAREN)) {
            Expr expr = expression();
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        throw error(peek(), "Expect expression.");
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) {
            Token token = advance();
            System.out.println("Consumed token: " + token.lexeme);  // Debugging print
            return token;
        }
        throw error(peek(), message);
    }

    private ParseError error(Token token, String message) {
        reportError(token, message);
        return new ParseError();
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().type == SEMICOLON) return;

            switch (peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }

            advance();
        }
    }

    private void reportError(Token token, String message) {
        System.err.println("[line " + token.line + "] Error at '" + token.lexeme + "': " + message);
    }

    class ParseError extends RuntimeException {}

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }
}
