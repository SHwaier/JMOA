# JMOA - Java Memory Optimization Analyzer User Guide

JMOA is a static analysis tool designed to identify memory optimization opportunities and performance pitfalls in your Java code.

## Installation

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Building from Source

1. Clone the repository:
   ```bash
   git clone https://github.com/SHwaier/JMOA.git
   cd JMOA
   ```

2. Build the project using Maven:
   ```bash
   mvn clean install
   ```

3. The executable JAR file will be located at: `jmoa-cli/target/jmoa-cli-1.0-SNAPSHOT.jar`

## Usage

Run the tool from the command line using the `java -jar` command.

### Basic Usage

Analyze a single file or a directory:

```bash
java -jar jmoa-cli/target/jmoa-cli-1.0-SNAPSHOT.jar <path-to-source>
```

**Example:**
```bash
java -jar jmoa-cli/target/jmoa-cli-1.0-SNAPSHOT.jar src/main/java
```

### Options

| Option | Description | Default |
|--------|-------------|---------|
| `-f`, `--format` | Output format. Options: `text`, `json` | `text` |
| `-o`, `--output` | Save results to a file. | (Prints to stdout) |
| `-h`, `--help` | Show help message. | |

### Examples

**Output as JSON:**
```bash
java -jar jmoa-cli/target/jmoa-cli-1.0-SNAPSHOT.jar --format=json src/
```

**Save results to a file:**
```bash
java -jar jmoa-cli/target/jmoa-cli-1.0-SNAPSHOT.jar --output=report.txt src/
```

**Save JSON report to a file:**
```bash
java -jar jmoa-cli/target/jmoa-cli-1.0-SNAPSHOT.jar --format=json --output=report.json src/
```

## Supported Rules

JMOA currently checks for the following issues:

### 1. Avoid String Concatenation in Loops (`AvoidStringConcatInLoop`)
**Description:** Detects string concatenation (`+` or `+=`) inside loops.
**Why:** String concatenation in loops creates unnecessary temporary `StringBuilder` and `String` objects, leading to high GC pressure.
**Recommendation:** Use a `StringBuilder` explicitly.

### 2. Collection Capacity Pre-sizing (`CollectionInitSize`)
**Description:** Detects initialization of collections (e.g., `ArrayList`, `HashMap`) without specifying an initial capacity.
**Why:** Default constructors use small initial sizes. As you add elements, the collection resizes (copying arrays), which is expensive.
**Recommendation:** If you know the approximate size, specify it: `new ArrayList<>(100)`.

### 3. Boxing/Unboxing Overhead (`BoxingInLoop`)
**Description:** Detects auto-boxing and unboxing of primitive types (like `int`, `double`) within loops.
**Why:** Converts primitives to wrapper objects (e.g., `Integer`, `Double`), creating unnecessary object allocations.
**Recommendation:** Use primitive types for counters and accumulators where possible.

### 4. Explicit Garbage Collection (`ExplicitGC`)
**Description:** Detects calls to `System.gc()`.
**Why:** Explicit GC calls are unpredictable and can trigger full GC cycles, pausing the application and hurting performance.
**Recommendation:** Remove `System.gc()` calls and let the JVM manage memory.
