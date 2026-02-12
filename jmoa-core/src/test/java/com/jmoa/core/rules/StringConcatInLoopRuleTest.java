package com.jmoa.core.rules;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.jmoa.core.AnalysisResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringConcatInLoopRuleTest {

    @Test
    void testStringConcatenationInForLoop() {
        String code = """
                public class Test {
                    public void method() {
                        String s = "";
                        for (int i = 0; i < 10; i++) {
                            s += i; // Detected
                        }
                    }
                }
                """;
        CompilationUnit cu = StaticJavaParser.parse(code);
        StringConcatInLoopRule rule = new StringConcatInLoopRule();
        List<AnalysisResult> results = rule.analyze(cu);

        assertEquals(1, results.size());
        assertEquals("WARNING", results.get(0).getSeverity());
    }

    @Test
    void testStringConcatenationInWhileLoop() {
        String code = """
                public class Test {
                    public void method() {
                        String s = "";
                        while (true) {
                            s = s + "a"; // Detected
                        }
                    }
                }
                """;
        CompilationUnit cu = StaticJavaParser.parse(code);
        StringConcatInLoopRule rule = new StringConcatInLoopRule();
        List<AnalysisResult> results = rule.analyze(cu);

        assertEquals(1, results.size());
    }

    @Test
    void testNoConcatenation() {
        String code = """
                public class Test {
                    public void method() {
                        int x = 0;
                        for (int i = 0; i < 10; i++) {
                            x += i; // Not a string concat
                        }
                    }
                }
                """;
        CompilationUnit cu = StaticJavaParser.parse(code);
        StringConcatInLoopRule rule = new StringConcatInLoopRule();
        List<AnalysisResult> results = rule.analyze(cu);

        // This was failing before because of naive implementation
        assertEquals(0, results.size());
    }

    @Test
    void testDoubleAdditionFalsePositive() {
        String code = """
                public class Test {
                    public void render() {
                        double fpsSamplingNanoTimer = 0.0;
                        double deltaNanos = 1.0;
                        for (int i=0; i<10; i++) {
                             fpsSamplingNanoTimer += deltaNanos; // Should NOT be detected
                        }
                    }
                }
                """;
        CompilationUnit cu = StaticJavaParser.parse(code);
        StringConcatInLoopRule rule = new StringConcatInLoopRule();
        List<AnalysisResult> results = rule.analyze(cu);

        assertEquals(0, results.size(), "Should not detect double addition as string concatenation");
    }
}
