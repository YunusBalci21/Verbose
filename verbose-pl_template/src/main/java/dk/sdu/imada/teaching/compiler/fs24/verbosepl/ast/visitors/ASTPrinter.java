package dk.sdu.imada.teaching.compiler.fs24.verbosepl.ast.visitors;

import dk.sdu.imada.teaching.compiler.fs24.verbosepl.ast.Stmt;

public class ASTPrinter implements ASTVisitor<String> {

    public String print(Expr expr) {
        return expr != null ? expr.accept(this) : "(null expression)";
    }

    public String print(Stmt stmt) {
        return stmt != null ? stmt.accept(this) : "(null statement)";
    }

    @Override
    public String visitVarStmt(Stmt.Var stmt) {
        if (stmt.initializer != null) {
            return "VarDecl ==> " + stmt.name.lexeme + " : " + stmt.type.lexeme + " = " + print(stmt.initializer);
        } else {
            return "VarDecl ==> " + stmt.name.lexeme + " : " + stmt.type.lexeme;
        }
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize("group", expr.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value == null) return "nil";
        return expr.value.toString();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }

    @Override
    public String visitVariableExpr(Expr.Variable expr) {
        return expr.name.lexeme;
    }

    @Override
    public String visitAssignmentExpr(Expr.Assignment expr) {
        return parenthesize("assign", expr.name.lexeme, expr.value);
    }

    @Override
    public String visitLogicalExpr(Expr.Logical expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitExpressionStmt(Stmt.Expression stmt) {
        return print(stmt.expression);
    }

    @Override
    public String visitPrintStmt(Stmt.Print stmt) {
        return parenthesize("print", stmt.expression);
    }

    @Override
    public String visitBlockStmt(Stmt.Block stmt) {
        StringBuilder builder = new StringBuilder();
        for (Stmt statement : stmt.statements) {
            builder.append(statement.accept(this)).append("\n");
        }
        return builder.toString();
    }

    @Override
    public String visitIfStmt(Stmt.If stmt) {
        if (stmt.elseBranch != null) {
            return parenthesize("if", stmt.condition, stmt.thenBranch, stmt.elseBranch);
        } else {
            return parenthesize("if", stmt.condition, stmt.thenBranch);
        }
    }

    @Override
    public String visitWhileStmt(Stmt.While stmt) {
        return parenthesize("while", stmt.condition, stmt.body);
    }

    @Override
    public String visitReturnStmt(Stmt.Return stmt) {
        if (stmt.value != null) {
            return parenthesize("return", stmt.value);
        }
        return "(return)";
    }

    private String parenthesize(String name, Object... parts) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);
        for (Object part : parts) {
            builder.append(" ");
            if (part instanceof Expr) {
                builder.append(((Expr) part).accept(this));
            } else if (part instanceof Stmt) {
                builder.append(((Stmt) part).accept(this));
            } else {
                builder.append(part.toString());
            }
        }
        builder.append(")");
        return builder.toString();
    }
}
