package com.jmoa.core;

import com.github.javaparser.ast.CompilationUnit;
import java.util.List;

public interface AnalysisRule {
    List<AnalysisResult> analyze(CompilationUnit cu);

    String getName();

    String getDescription();
}
