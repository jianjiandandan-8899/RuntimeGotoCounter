
# Goto Statement Counter Project

This project uses the Soot framework to instrument Java bytecode to count the number of `goto` statements executed at runtime. The project is implemented as a Java agent that can be attached to any Java application.

## Features

- Counts the number of `goto` statements executed at runtime.
- Uses the Soot framework for bytecode analysis and instrumentation.
- Can be used as a Java agent to instrument any Java application.

## Requirements

- Java Development Kit (JDK) 8 or higher
- Apache Maven 3.6.0 or higher

## Installation

1. Clone the repository:
   ```sh
   git clone https://github.com/yourusername/goto-counter.git
   cd goto-counter
   ```

2. Build the project using Maven:
   ```sh
   mvn clean compile assembly:single
   ```

## Usage

### Running the Instrumented Application

1. Compile your Java application (if not already compiled):
   ```sh
   javac -d target/classes src/main/java/your/package/ExampleWithGoto.java
   ```

2. Run the application with the Java agent:
   ```sh
   java -javaagent:target/GotoCounterProject-1.0-SNAPSHOT-jar-with-dependencies.jar -cp target/classes your.package.ExampleWithGoto
   ```

### Example

The example application (`ExampleWithGoto.java`) contains `goto` statements implemented using Java control flow structures such as `if-else` and loops. The instrumented application will print the number of `goto` statements executed during the run.

## Project Structure

```
.
├── pom.xml                     # Maven project file
├── src
│   └── main
│       └── java
│           └── your
│               └── package
│                   ├── GotoAgent.java          # Java agent for instrumenting bytecode
│                   ├── ExampleWithGoto.java    # Example application with goto statements
│                   └── SootUtils.java          # Utility class for using Soot to instrument bytecode
└── target
    └── GotoCounterProject-1.0-SNAPSHOT-jar-with-dependencies.jar  # Generated JAR file
```

## How It Works

1. **GotoAgent**: This class is the entry point for the Java agent. It sets up the `ClassFileTransformer` that uses `SootUtils` to modify the bytecode.

2. **ExampleWithGoto**: This class contains example code with `goto` statements represented by control flow structures like loops and conditionals.

3. **SootUtils**: This class uses the Soot framework to instrument the bytecode of the example application. It inserts instructions to update the `gotoCount` field each time a `goto` statement is encountered.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request with your changes.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact

If you have any questions or suggestions, please feel free to contact me at [your.email@example.com](mailto:your.email@example.com).
