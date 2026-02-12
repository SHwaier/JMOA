package com.jmoa.core.rules;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.jmoa.core.AnalysisResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BoxingInLoopRuleTest {

    @Test
    void testIntegerBoxingInLoop() {
        String code = """
                public class Test {
                    public void method() {
                        Integer sum = 0;
                        for (int i = 0; i < 10; i++) {
                            sum += i; // Detected: sum is Integer
                        }
                    }
                }
                """;
        CompilationUnit cu = StaticJavaParser.parse(code);
        BoxingInLoopRule rule = new BoxingInLoopRule();
        List<AnalysisResult> results = rule.analyze(cu);

        assertEquals(1, results.size());
    }

    @Test
    void testPrimitiveNoBoxing() {
        String code = """
                public class Test {
                    public void method() {
                        int sum = 0;
                        for (int i = 0; i < 10; i++) {
                            sum += i; // Not detected: sum is int
                        }
                    }
                }
                """;
        CompilationUnit cu = StaticJavaParser.parse(code);
        BoxingInLoopRule rule = new BoxingInLoopRule();
        List<AnalysisResult> results = rule.analyze(cu);

        assertEquals(0, results.size());
    }

    @Test
    void testDoubleBoxingInWhile() {
        String code = """
                public class Test {
                    public void method() {
                        Double total = 0.0;
                        while (true) {
                            total++; // Detected
                        }
                    }
                }
                """;
        CompilationUnit cu = StaticJavaParser.parse(code);
        BoxingInLoopRule rule = new BoxingInLoopRule();
        List<AnalysisResult> results = rule.analyze(cu);

        assertEquals(1, results.size());
    }
}
