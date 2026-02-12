package com.jmoa.core.rules;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.jmoa.core.AnalysisResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CollectionInitSizeRuleTest {

    @Test
    void testArrayListWithoutSize() {
        String code = """
                import java.util.ArrayList;
                public class Test {
                    public void method() {
                        var list = new ArrayList<String>(); // Detected
                    }
                }
                """;
        CompilationUnit cu = StaticJavaParser.parse(code);
        CollectionInitSizeRule rule = new CollectionInitSizeRule();
        List<AnalysisResult> results = rule.analyze(cu);

        assertEquals(1, results.size());
    }

    @Test
    void testArrayListWithSize() {
        String code = """
                import java.util.ArrayList;
                public class Test {
                    public void method() {
                        var list = new ArrayList<String>(10); // Not detected
                    }
                }
                """;
        CompilationUnit cu = StaticJavaParser.parse(code);
        CollectionInitSizeRule rule = new CollectionInitSizeRule();
        List<AnalysisResult> results = rule.analyze(cu);

        assertEquals(0, results.size());
    }

    @Test
    void testHashMapWithoutSize() {
        String code = """
                import java.util.HashMap;
                public class Test {
                    public void method() {
                        var map = new HashMap<String, String>(); // Detected
                    }
                }
                """;
        CompilationUnit cu = StaticJavaParser.parse(code);
        CollectionInitSizeRule rule = new CollectionInitSizeRule();
        List<AnalysisResult> results = rule.analyze(cu);

        assertEquals(1, results.size());
    }
}
