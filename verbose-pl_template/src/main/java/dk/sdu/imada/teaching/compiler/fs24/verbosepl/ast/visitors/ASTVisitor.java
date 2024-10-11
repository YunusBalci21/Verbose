package dk.sdu.imada.teaching.compiler.fs24.verbosepl.ast.visitors;

import dk.sdu.imada.teaching.compiler.fs24.verbosepl.ast.Stmt;

// ASTVisitor.java
public interface ASTVisitor<T> extends Expr.Visitor<T>, Stmt.Visitor<T> {
    // This interface unifies the visitation of both expressions and statements
}

