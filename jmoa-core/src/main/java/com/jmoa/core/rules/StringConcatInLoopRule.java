package com.jmoa.core.rules;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.jmoa.core.AnalysisResult;
import com.jmoa.core.AnalysisRule;

import java.util.ArrayList;
import java.util.List;

public class StringConcatInLoopRule implements AnalysisRule {

    @Override
    public String getName() {
        return "AvoidStringConcatInLoop";
    }

    @Override
    public String getDescription() {
        return "String concatenation in loops creates unnecessary temporary objects. Use StringBuilder instead.";
    }

    @Override
    public List<AnalysisResult> analyze(CompilationUnit cu) {
        List<AnalysisResult> results = new ArrayList<>();

        cu.findAll(ForStmt.class).forEach(loop -> checkLoop(loop, results));
        cu.findAll(ForEachStmt.class).forEach(loop -> checkLoop(loop, results));
        cu.findAll(WhileStmt.class).forEach(loop -> checkLoop(loop, results));
        cu.findAll(DoStmt.class).forEach(loop -> checkLoop(loop, results));

        return results;
    }

    private void checkLoop(Node loopNode, List<AnalysisResult> results) {
        // Find all assignment expressions in the loop
        List<AssignExpr> assignments = loopNode.findAll(AssignExpr.class);

        for (AssignExpr assign : assignments) {
            // Check if it's a string concatenation: s += ... or s = s + ...
            if (isStringConcatenation(assign)) {
                results.add(new AnalysisResult(
                        getName(),
                        "String concatenation detected in loop. Consider using StringBuilder.",
                        assign.getBegin().map(pos -> pos.line).orElse(-1),
                        "WARNING"));
            }
        }
    }

    private boolean isStringConcatenation(AssignExpr assign) {
        // Case 1: s += "..." (PLUS_ASSIGN)
        if (assign.getOperator() == AssignExpr.Operator.PLUS) {
            return isStringType(assign.getTarget());
        }

        // Case 2: s = s + "..."
        if (assign.getOperator() == AssignExpr.Operator.ASSIGN) {
            if (assign.getValue().isBinaryExpr()) {
                BinaryExpr binaryExpr = assign.getValue().asBinaryExpr();
                if (binaryExpr.getOperator() == BinaryExpr.Operator.PLUS) {
                    // Simplified check: if target name appears in binary expression
                    // Ideally we need type resolution, but we'll stick to heuristic for MVP
                    if (assign.getTarget().isNameExpr()) {
                        String targetName = assign.getTarget().asNameExpr().getNameAsString();
                        // Check if left or right operand is the same variable
                        if ((binaryExpr.getLeft().isNameExpr()
                                && binaryExpr.getLeft().asNameExpr().getNameAsString().equals(targetName)) ||
                                (binaryExpr.getRight().isNameExpr()
                                        && binaryExpr.getRight().asNameExpr().getNameAsString().equals(targetName))) {
                            return isStringType(assign.getTarget());
                        }
                    }
                }
            }
        }

        return false;
    }

    private boolean isStringType(Node node) {
        if (node instanceof com.github.javaparser.ast.expr.NameExpr) {
            String name = ((com.github.javaparser.ast.expr.NameExpr) node).getNameAsString();
            return isDeclaredAsString(node, name);
        }
        return false;
    }

    private boolean isDeclaredAsString(Node node, String variableName) {
        // Walk up the tree to find declaration
        return node.findAncestor(CompilationUnit.class).map(cu -> {
            final boolean[] isString = { false };
            cu.findAll(com.github.javaparser.ast.body.VariableDeclarator.class).forEach(var -> {
                if (var.getNameAsString().equals(variableName)) {
                    String type = var.getType().asString();
                    if (type.equals("String") || type.equals("java.lang.String")) {
                        isString[0] = true;
                    }
                }
            });
            return isString[0];
        }).orElse(false);
    }
}
