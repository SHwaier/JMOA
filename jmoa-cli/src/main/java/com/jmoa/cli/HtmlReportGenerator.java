package com.jmoa.cli;

import com.jmoa.core.AnalysisResult;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HtmlReportGenerator {

        public String generate(List<AnalysisResult> results) {
                Map<String, Long> severityCounts = results.stream()
                                .collect(Collectors.groupingBy(AnalysisResult::getSeverity, Collectors.counting()));

                long high = severityCounts.getOrDefault("ERROR", 0L);
                long medium = severityCounts.getOrDefault("WARNING", 0L);
                long low = severityCounts.getOrDefault("INFO", 0L);
                long total = results.size();

                StringBuilder html = new StringBuilder();
                html.append("<!DOCTYPE html>\n");
                html.append("<html lang=\"en\">\n");
                html.append("<head>\n");
                html.append("    <meta charset=\"UTF-8\">\n");
                html.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
                html.append("    <title>JMOA Analysis Report</title>\n");
                html.append("    <script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>\n");
                html.append("    <style>\n");
                html.append(
                                "        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 0; background-color: #f4f7f6; color: #333; }\n");
                html.append("        .container { max-width: 1200px; margin: 40px auto; padding: 20px; }\n");
                html.append(
                                "        header { background-color: #2c3e50; color: white; padding: 20px 0; text-align: center; margin-bottom: 40px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }\n");
                html.append("        h1 { margin: 0; font-weight: 300; }\n");
                html.append(
                                "        .stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; margin-bottom: 40px; }\n");
                html.append(
                                "        .card { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.05); text-align: center; }\n");
                html.append("        .card h2 { margin: 0; font-size: 2.5em; color: #2c3e50; }\n");
                html.append(
                                "        .card p { margin: 5px 0 0; color: #7f8c8d; text-transform: uppercase; font-size: 0.9em; letter-spacing: 1px; }\n");
                html.append(
                                "        .chart-container { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.05); margin-bottom: 40px; height: 300px; position: relative; }\n");
                html.append(
                                "        table { width: 100%; border-collapse: collapse; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }\n");
                html.append("        th, td { padding: 15px; text-align: left; border-bottom: 1px solid #eee; }\n");
                html.append(
                                "        th { background-color: #ecf0f1; color: #7f8c8d; font-weight: 600; text-transform: uppercase; font-size: 0.85em; }\n");
                html.append("        tr:last-child td { border-bottom: none; }\n");
                html.append("        tr:hover { background-color: #f9f9f9; }\n");
                html.append("        .severity-high { color: #e74c3c; font-weight: bold; }\n");
                html.append("        .severity-medium { color: #f39c12; font-weight: bold; }\n");
                html.append("        .severity-low { color: #3498db; font-weight: bold; }\n");
                html.append("    </style>\n");
                html.append("</head>\n");
                html.append("<body>\n");
                html.append("    <header>\n");
                html.append("        <h1>JMOA Analysis Report</h1>\n");
                html.append("    </header>\n");
                html.append("    <div class=\"container\">\n");
                html.append("        <div class=\"stats-grid\">\n");
                html.append("            <div class=\"card\"><h2>").append(total)
                                .append("</h2><p>Total Issues</p></div>\n");
                html.append("            <div class=\"card\"><h2 style=\"color: #e74c3c\">").append(high)
                                .append("</h2><p>High Priority</p></div>\n");
                html.append("            <div class=\"card\"><h2 style=\"color: #f39c12\">").append(medium)
                                .append("</h2><p>Medium Priority</p></div>\n");
                html.append("            <div class=\"card\"><h2 style=\"color: #3498db\">").append(low)
                                .append("</h2><p>Low Priority</p></div>\n");
                html.append("        </div>\n");
                html.append("        <div class=\"chart-container\">\n");
                html.append("            <canvas id=\"severityChart\"></canvas>\n");
                html.append("        </div>\n");
                html.append("        <table>\n");
                html.append("            <thead>\n");
                html.append("                <tr>\n");
                html.append("                    <th>Severity</th>\n");
                html.append("                    <th>File</th>\n");
                html.append("                    <th>Rule</th>\n");
                html.append("                    <th>Message</th>\n");
                html.append("                    <th>Line</th>\n");
                html.append("                </tr>\n");
                html.append("            </thead>\n");
                html.append("            <tbody>\n");

                for (AnalysisResult result : results) {
                        String severityClass = "severity-" + mapSeverityToClass(result.getSeverity());
                        html.append("                <tr>\n");
                        html.append("                    <td class=\"").append(severityClass).append("\">")
                                        .append(result.getSeverity()).append("</td>\n");
                        html.append("                    <td>").append(escapeHtml(result.getFile())).append("</td>\n");
                        html.append("                    <td>").append(escapeHtml(result.getRuleName()))
                                        .append("</td>\n");
                        html.append("                    <td>").append(escapeHtml(result.getMessage()))
                                        .append("</td>\n");
                        html.append("                    <td>").append(result.getLine()).append("</td>\n");
                        html.append("                </tr>\n");
                }

                html.append("            </tbody>\n");
                html.append("        </table>\n");
                html.append("    </div>\n");
                html.append("    <script>\n");
                html.append("        const ctx = document.getElementById('severityChart').getContext('2d');\n");
                html.append("        new Chart(ctx, {\n");
                html.append("            type: 'doughnut',\n");
                html.append("            data: {\n");
                html.append("                labels: ['High', 'Medium', 'Low'],\n");
                html.append("                datasets: [{\n");
                html.append("                    data: [").append(high).append(", ").append(medium).append(", ")
                                .append(low)
                                .append("],\n");
                html.append("                    backgroundColor: ['#e74c3c', '#f39c12', '#3498db'],\n");
                html.append("                    borderWidth: 0\n");
                html.append("                }]\n");
                html.append("            },\n");
                html.append("            options: {\n");
                html.append("                responsive: true,\n");
                html.append("                maintainAspectRatio: false,\n");
                html.append("                plugins: {\n");
                html.append("                    legend: { position: 'bottom' }\n");
                html.append("                }\n");
                html.append("            }\n");
                html.append("        });\n");
                html.append("    </script>\n");
                html.append("</body>\n");
                html.append("</html>");

                return html.toString();
        }

        private String mapSeverityToClass(String severity) {
                if (severity == null)
                        return "low";
                switch (severity.toUpperCase()) {
                        case "ERROR":
                                return "high";
                        case "WARNING":
                                return "medium";
                        default:
                                return "low";
                }
        }

        private String escapeHtml(String s) {
                if (s == null)
                        return "";
                return s.replace("&", "&amp;")
                                .replace("<", "&lt;")
                                .replace(">", "&gt;")
                                .replace("\"", "&quot;")
                                .replace("'", "&#39;");
        }
}
