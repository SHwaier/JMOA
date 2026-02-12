package com.jmoa.cli;

import com.jmoa.core.AnalysisEngine;
import com.jmoa.core.AnalysisResult;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "jmoa", mixinStandardHelpOptions = true, version = "jmoa 1.0", description = "Java Memory Optimization Analyzer")
public class JmoaCli implements Callable<Integer> {

    @Parameters(index = "0", description = "The file or directory to analyze.")
    private File file;

    @Option(names = { "-f", "--format" }, description = "Output format: text (default) or json.")
    private String format = "text";

    @Option(names = { "-o", "--output" }, description = "Output file.")
    private File outputFile;

    @Override
    public Integer call() throws Exception {
        if (!format.equals("json") && outputFile == null) {
            System.out.println("JMOA - Java Memory Optimization Analyzer");
            System.out.println("Target: " + file.getAbsolutePath());
        }

        AnalysisEngine engine = new AnalysisEngine();
        List<AnalysisResult> results = engine.analyze(file.getAbsolutePath());

        String outputContent;
        if ("json".equalsIgnoreCase(format)) {
            outputContent = generateJson(results);
        } else {
            outputContent = generateText(results);
        }

        if (outputFile != null) {
            java.nio.file.Files.writeString(outputFile.toPath(), outputContent);
            if (!format.equals("json")) {
                System.out.println("Results written to " + outputFile.getAbsolutePath());
            }
        } else {
            System.out.println(outputContent);
        }

        return 0;
    }

    private String generateText(List<AnalysisResult> results) {
        StringBuilder sb = new StringBuilder();
        if (results.isEmpty()) {
            sb.append("No issues found.");
        } else {
            for (AnalysisResult result : results) {
                sb.append(result).append(System.lineSeparator());
            }
        }
        return sb.toString();
    }

    private String generateJson(List<AnalysisResult> results) {
        StringBuilder json = new StringBuilder();
        json.append("[\n");
        for (int i = 0; i < results.size(); i++) {
            AnalysisResult result = results.get(i);
            json.append("  {\n");
            json.append("    \"rule\": \"").append(escapeJson(result.getRuleName())).append("\",\n");
            json.append("    \"message\": \"").append(escapeJson(result.getMessage())).append("\",\n");
            json.append("    \"line\": ").append(result.getLine()).append(",\n");
            json.append("    \"severity\": \"").append(escapeJson(result.getSeverity())).append("\"\n");
            json.append("  }");
            if (i < results.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("]");
        return json.toString();
    }

    private String escapeJson(String s) {
        if (s == null)
            return "";
        return s.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    public static void main(String... args) {
        int exitCode = new CommandLine(new JmoaCli()).execute(args);
        System.exit(exitCode);
    }
}
