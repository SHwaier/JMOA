# Java Memory Optimization Analyzer (JMOA) - Project Brain

**Source of Truth**: [PRD](PRD.md)
**Status**: Phase 3 - Reporting & Visualization

## Project Overview
The Java Memory Optimization Analyzer (JMOA) is a developer tool designed to automatically analyze Java applications and suggest safe, actionable edits that reduce RAM usage and improve memory efficiency.

## Current Context
We are implementing 'Advanced Reporting', moving beyond simple text/JSON output to rich HTML dashboards. This will help users visualize the impact of memory issues.

## Active Tasks
- [ ] Determine next phase from Roadmap
- [x] Create End User Documentation
- [x] Create Public GitHub Repository

## Strategic Roadmap (Capability Checklist)

### 1. Runtime Profiling (Measure the Right Things)
- [ ] Runtime collection (low-friction)
- [ ] One-command profiling run
- [ ] Capture allocation rate, GC pause time, promotion rate
- [ ] Track peak heap and steady-state heap
- [ ] Compare before/after runs automatically

### 2. Find Memory Waste
- [ ] **Retained-heap killers**: Memory leaks, accidental retention, duplicate data.
- [ ] **Allocation-rate killers**: Hot allocation sites, boxing/unboxing hotspots.
- [ ] **Container misuse**: Wrong data structures, missing capacity hints.

### 3. Leak Diagnosis
- [ ] Growth timeline & Retained-size ranking.
- [ ] Leak suspects report & Reference chain explanation.
- [ ] Leak pattern classification (Unbounded cache, ThreadLocal, etc.).

### 4. Automated Refactoring (Concrete Code Edits)
- [ ] **Safe auto-fix**: Pre-size collections, String concatenation to StringBuilder, Integer.valueOf caching.
- [ ] **Guided fix**: Bounded caching, Weak/Soft refs, Pooling decisions.

### 5. Better Data Representations
- [ ] Suggest primitive collections (int[], long[]).
- [ ] Compact enums/flags into bitfields.
- [ ] String deduplication suggestions.
- [ ] Reduce object header overhead.

### 6. Workload Awareness
- [ ] Optimize for Throughput vs Latency.
- [ ] Handle burst vs steady patterns.
- [ ] Trade-off analysis (CPU vs Memory).

### 7. RAM Cost Framing
- [ ] Translate savings to $/GB or Container size reduction.
- [ ] "Memory headroom" score.

### 8. Automated Verification
- [ ] Apply patches -> Run tests -> Re-profile.
- [ ] Rollback if regression detected.

### 9. CI/CD Integration (Regressions)
- [ ] Memory budgets (Fail PR if live set > X%).
- [ ] JSON/SARIF/HTML reports for pipelines.

### 10. Workflow Integration
- [ ] IDE Plugins (IntelliJ).
- [ ] Build tool tasks (Maven/Gradle).
- [ ] Docker/K8s support.

### 11. Modern Java & Frameworks
- [ ] Spring/Hibernate/Netty/Jackson specific support.
- [ ] Detect framework-specific leaks.

### 12. Correctness & Guardrails
- [ ] Confidence scoring.
- [ ] Avoid "bad optimizations".

### 13. Advanced Capabilities
- [ ] "What-if" simulator.
- [ ] Auto-minimization of reproduction cases.

## Development Log
- **2026-02-12**: Added comprehensive Project Roadmap based on user Capability Checklist.
- **2026-02-12**: Added file path information to JSON, HTML, and Text reports.
- **2026-02-12**: Completed Phase 3: Added HTML reporting with dashboards and charts.
- **2026-02-12**: Published project to GitHub: [JMOA](https://github.com/SHwaier/JMOA).
- **2026-02-12**: Initialized `brain.md` and read PRD. Established `brain.md` as the single source of truth.
