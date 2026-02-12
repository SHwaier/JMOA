package com.jmoa.core.rules;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.jmoa.core.AnalysisResult;
import com.jmoa.core.AnalysisRule;

import java.util.ArrayList;
import java.util.List;

public class ExplicitGCRule implements AnalysisRule {

    @Override
    public String getName() {
        return "ExplicitGC";
    }

    @Override
    public String getDescription() {
        return "Do not explicitly call System.gc(). It is unpredictable and can cause performance issues.";
    }

    @Override
    public List<AnalysisResult> analyze(CompilationUnit cu) {
        List<AnalysisResult> results = new ArrayList<>();

        cu.findAll(MethodCallExpr.class).forEach(methodCall -> {
            if (isSystemGc(methodCall)) {
                results.add(new AnalysisResult(
                        getName(),
                        "Explicit Garbage Collection detected. Remove System.gc() calls and let the JVM manage memory.",
                        methodCall.getBegin().map(pos -> pos.line).orElse(-1),
                        "WARNING"));
            }
        });

        return results;
    }

    private boolean isSystemGc(MethodCallExpr methodCall) {
        // Check for System.gc()
        if (methodCall.getNameAsString().equals("gc")) {
            if (methodCall.getScope().isPresent()) {
                return methodCall.getScope().get().toString().equals("System");
            }
        }
        return false;
    }
}
