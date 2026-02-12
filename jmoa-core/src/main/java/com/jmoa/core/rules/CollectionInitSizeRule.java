package com.jmoa.core.rules;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.jmoa.core.AnalysisResult;
import com.jmoa.core.AnalysisRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CollectionInitSizeRule implements AnalysisRule {

    private static final Set<String> TARGET_TYPES = Set.of(
            "ArrayList", "HashSet", "HashMap", "LinkedHashMap", "Vector", "Hashtable");

    @Override
    public String getName() {
        return "CollectionInitSize";
    }

    @Override
    public String getDescription() {
        return "Initialize collections with a size if known to avoid resizing overhead.";
    }

    @Override
    public List<AnalysisResult> analyze(CompilationUnit cu) {
        List<AnalysisResult> results = new ArrayList<>();

        cu.findAll(ObjectCreationExpr.class).forEach(expr -> {
            if (isTargetCollection(expr) && expr.getArguments().isEmpty()) {
                results.add(new AnalysisResult(
                        getName(),
                        "Collection initialized without size. If the size is known, specify it to avoid resizing.",
                        expr.getBegin().map(pos -> pos.line).orElse(-1),
                        "INFO"));
            }
        });

        return results;
    }

    private boolean isTargetCollection(ObjectCreationExpr expr) {
        return TARGET_TYPES.contains(expr.getType().getNameAsString());
    }
}
