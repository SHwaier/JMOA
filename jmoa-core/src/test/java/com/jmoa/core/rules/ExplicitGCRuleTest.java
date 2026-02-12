package com.jmoa.core.rules;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.jmoa.core.AnalysisResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExplicitGCRuleTest {

    @Test
    void testSystemGc() {
        String code = """
                public class Test {
                    public void method() {
                        System.gc(); // Detected
                    }
                }
                """;
        CompilationUnit cu = StaticJavaParser.parse(code);
        ExplicitGCRule rule = new ExplicitGCRule();
        List<AnalysisResult> results = rule.analyze(cu);

        assertEquals(1, results.size());
    }

    @Test
    void testOtherGc() {
        String code = """
                public class Test {
                    public void method() {
                        MyClass.gc(); // Not detected
                    }
                }
                """;
        CompilationUnit cu = StaticJavaParser.parse(code);
        ExplicitGCRule rule = new ExplicitGCRule();
        List<AnalysisResult> results = rule.analyze(cu);

        assertEquals(0, results.size());
    }
}
