package com.jmoa.core;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.jmoa.core.rules.StringConcatInLoopRule;
import com.jmoa.core.rules.CollectionInitSizeRule;
import com.jmoa.core.rules.BoxingInLoopRule;
import com.jmoa.core.rules.ExplicitGCRule;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AnalysisEngine {
    private final List<AnalysisRule> rules = new ArrayList<>();

    public AnalysisEngine() {
        // Register rules
        rules.add(new StringConcatInLoopRule());
        rules.add(new CollectionInitSizeRule());
        rules.add(new BoxingInLoopRule());
        rules.add(new ExplicitGCRule());
    }

    public List<AnalysisResult> analyze(String path) {
        System.out.println("AnalysisEngine: Starting analysis on " + path);
        File file = new File(path);
        List<AnalysisResult> allResults = new ArrayList<>();

        if (!file.exists()) {
            System.err.println("Error: File or directory does not exist: " + path);
            return allResults;
        }

        if (file.isDirectory()) {
            analyzeDirectory(file, allResults);
        } else {
            analyzeFile(file, allResults);
        }

        System.out.println("AnalysisEngine: Analysis complete.");
        return allResults;
    }

    private void analyzeDirectory(File directory, List<AnalysisResult> allResults) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    analyzeDirectory(file, allResults);
                } else {
                    analyzeFile(file, allResults);
                }
            }
        }
    }

    private void analyzeFile(File file, List<AnalysisResult> allResults) {
        if (!file.getName().endsWith(".java")) {
            return;
        }

        try (FileInputStream in = new FileInputStream(file)) {
            CompilationUnit cu = StaticJavaParser.parse(in);

            for (AnalysisRule rule : rules) {
                List<AnalysisResult> results = rule.analyze(cu);
                allResults.addAll(results);
            }
        } catch (IOException e) {
            System.err.println("Error reading file " + file.getName() + ": " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error parsing file " + file.getName() + ": " + e.getMessage());
        }
    }
}
