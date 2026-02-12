# JMOA - Java Memory Optimization Analyzer

JMOA is a static analysis tool designed to identify memory optimization opportunities and performance pitfalls in your Java code.

## Key Features

- **Avoid String Concatenation in Loops**: Detects inefficient string building.
- **Collection Capacity Pre-sizing**: Suggests initial capacities for collections.
- **Boxing/Unboxing Overhead**: Identifies hidden costs in loops.
- **Explicit Garbage Collection**: Warns against `System.gc()` usage.

## Quick Start

1. **Build**: `mvn clean install`
2. **Run**: `java -jar jmoa-cli/target/jmoa-cli-1.0-SNAPSHOT.jar <path-to-source>`

For more detailed instructions, see the [User Guide](docs/user_guide.md).

## Documentation

- [User Guide](docs/user_guide.md)
- [Project Documentation](docs/brain.md)
