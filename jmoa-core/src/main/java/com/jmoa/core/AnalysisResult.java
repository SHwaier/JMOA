package com.jmoa.core;

public class AnalysisResult {
    private final String ruleName;
    private final String message;
    private final int line;
    private final String severity;
    private String file;

    public AnalysisResult(String ruleName, String message, int line, String severity) {
        this.ruleName = ruleName;
        this.message = message;
        this.line = line;
        this.severity = severity;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getFile() {
        return file;
    }

    public String getRuleName() {
        return ruleName;
    }

    public String getMessage() {
        return message;
    }

    public int getLine() {
        return line;
    }

    public String getSeverity() {
        return severity;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s %s(Line %d): %s", severity, ruleName, (file != null ? "(" + file + ") " : ""),
                line, message);
    }
}
