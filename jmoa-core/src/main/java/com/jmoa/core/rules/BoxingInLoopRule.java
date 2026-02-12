package com.jmoa.core.rules;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.AssignExpr;

import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.jmoa.core.AnalysisResult;
import com.jmoa.core.AnalysisRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BoxingInLoopRule implements AnalysisRule {

    // Simple heuristic: variables with wrapper types
    private static final Set<String> WRAPPER_TYPES = Set.of(
            "Integer", "Long", "Double", "Float", "Short", "Byte", "Character", "Boolean");

    @Override
    public String getName() {
        return "BoxingInLoop";
    }

    @Override
    public String getDescription() {
        return "Avoid boxing/unboxing in loops. Use primitive types for counters and accumulators.";
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
        // Detect +=, -=, *=, /= assignments on potential wrapper types
        loopNode.findAll(AssignExpr.class).forEach(assign -> {
            if (isArithmeticAssignment(assign.getOperator()) && isPotentialWrapper(assign.getTarget())) {
                results.add(new AnalysisResult(
                        getName(),
                        "Potential boxing/unboxing in loop. Check if '" + assign.getTarget() + "' is a wrapper type.",
                        assign.getBegin().map(pos -> pos.line).orElse(-1),
                        "WARNING"));
            }
        });

        // Detect ++, -- unary expressions
        loopNode.findAll(UnaryExpr.class).forEach(unary -> {
            if (isArithmeticUnary(unary.getOperator()) && isPotentialWrapper(unary.getExpression())) {
                results.add(new AnalysisResult(
                        getName(),
                        "Potential boxing/unboxing in loop. Check if '" + unary.getExpression()
                                + "' is a wrapper type.",
                        unary.getBegin().map(pos -> pos.line).orElse(-1),
                        "WARNING"));
            }
        });
    }

    private boolean isArithmeticAssignment(AssignExpr.Operator op) {
        return op == AssignExpr.Operator.PLUS || op == AssignExpr.Operator.MINUS ||
                op == AssignExpr.Operator.MULTIPLY || op == AssignExpr.Operator.DIVIDE;
    }

    private boolean isArithmeticUnary(UnaryExpr.Operator op) {
        return op == UnaryExpr.Operator.PREFIX_INCREMENT || op == UnaryExpr.Operator.POSTFIX_INCREMENT ||
                op == UnaryExpr.Operator.PREFIX_DECREMENT || op == UnaryExpr.Operator.POSTFIX_DECREMENT;
    }

    private boolean isPotentialWrapper(Node node) {
        // LIMITATION: Without TypeSolver, we can't certainly know if a variable is a
        // wrapper.
        // Heuristic: If we had a list of known variables and their types from the CU,
        // we could check.
        // For MVP, this is Hard.
        // Let's implement a very basic check: does the variable name hint at it? No.
        // Does the definition exist in the file?
        // We will TRY to find the type of the variable if it is declared in the same
        // CU.

        // This is a naive implementation that might have many false negatives (unknown
        // types)
        // or false positives (if we mistakenly assume).
        // Let's rely on finding the VariableDeclarator.

        if (node instanceof NameExpr) {
            String name = ((NameExpr) node).getNameAsString();
            return isDeclaredAsWrapper(node, name);
        }
        return false;
    }

    private boolean isDeclaredAsWrapper(Node node, String variableName) {
        // Walk up the tree to find declaration
        return node.findAncestor(CompilationUnit.class).map(cu -> {
            boolean[] foundWrapper = { false };
            cu.findAll(com.github.javaparser.ast.body.VariableDeclarator.class).forEach(var -> {
                if (var.getNameAsString().equals(variableName)) {
                    if (WRAPPER_TYPES.contains(var.getType().asString())) {
                        foundWrapper[0] = true;
                    }
                }
            });
            return foundWrapper[0];
        }).orElse(false);
    }
}
