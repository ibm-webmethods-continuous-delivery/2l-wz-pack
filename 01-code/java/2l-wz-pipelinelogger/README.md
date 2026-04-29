# Pipeline Logger for webMethods Integration Server

A flexible, production-ready pipeline logging solution for webMethods Integration Server that intercepts service invocations and logs input/output pipeline data in multiple configurable formats.

## Overview

This library provides an `InvokeChainProcessor` that intercepts service calls and logs their pipeline data (input and output) using configurable serialization strategies. It supports three serialization formats optimized for different use cases:

- **Verbose Serializer**: Multi-line, indented output with full type information (for local development)
- **JSON Compact Serializer**: Single-line JSON with complete type information (for production logging)
- **JSON Simple Serializer**: Single-line JSON with key-value pairs only (for simplified production logging)

## Features

- ✅ **Multiple Serialization Formats**: Choose between verbose, compact JSON, or simple JSON
- ✅ **Non-Exclusive Toggles**: Enable multiple serializers simultaneously
- ✅ **Enhanced Password Protection**: Automatically masks fields containing "password" (case-insensitive)
- ✅ **Comprehensive Type Support**: Handles IData, arrays, nested structures, dates, and more
- ✅ **Production-Ready**: Single-line JSON output suitable for log aggregation systems
- ✅ **Runtime Configuration**: Control via environment variables without code changes
- ✅ **Error Resilient**: Graceful handling of serialization errors
- ✅ **Performance Conscious**: Only logs top-level service calls to avoid recursion overhead

## Architecture

### Components

```
com.ibm.tel.wm.pipelinelogger/
├── PipelineSerializer.java          # Interface for serialization strategies
├── VerboseSerializer.java           # Multi-line verbose output
├── JsonCompactSerializer.java       # Single-line JSON with type info
├── JsonSimpleSerializer.java        # Single-line JSON without type info
├── Config.java                      # Configuration management
└── InvokeChainInterceptor.java      # Main interceptor
```

### Design Pattern

The implementation uses the **Strategy Pattern** for serialization:

1. `PipelineSerializer` interface defines the contract
2. Three concrete implementations provide different serialization strategies
3. `InvokeChainInterceptor` manages the list of serializers and executes enabled ones
4. `Config` provides centralized configuration management

## Configuration

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `ZX_PIPELINE_LOGGER_ENABLED` | `false` | Master toggle - must be `true` for any logging |
| `ZX_PIPELINE_LOGGER_VERBOSE_ENABLED` | `false` | Enable verbose multi-line serializer |
| `ZX_PIPELINE_LOGGER_JSON_COMPACT_ENABLED` | `false` | Enable JSON compact serializer (with type info) |
| `ZX_PIPELINE_LOGGER_JSON_SIMPLE_ENABLED` | `true` | Enable JSON simple serializer (key-value only) |

### Configuration Examples

#### Local Development (Verbose Output)
```bash
export ZX_PIPELINE_LOGGER_ENABLED=true
export ZX_PIPELINE_LOGGER_VERBOSE_ENABLED=true
export ZX_PIPELINE_LOGGER_JSON_COMPACT_ENABLED=false
export ZX_PIPELINE_LOGGER_JSON_SIMPLE_ENABLED=false
```

#### Production (Simple JSON - Default)
```bash
export ZX_PIPELINE_LOGGER_ENABLED=true
# JSON Simple is enabled by default
```

#### Production (Compact JSON with Full Type Info)
```bash
export ZX_PIPELINE_LOGGER_ENABLED=true
export ZX_PIPELINE_LOGGER_JSON_COMPACT_ENABLED=true
export ZX_PIPELINE_LOGGER_JSON_SIMPLE_ENABLED=false
```

#### Multiple Serializers (Both JSON Formats)
```bash
export ZX_PIPELINE_LOGGER_ENABLED=true
export ZX_PIPELINE_LOGGER_JSON_COMPACT_ENABLED=true
export ZX_PIPELINE_LOGGER_JSON_SIMPLE_ENABLED=true
```

## Output Examples

### Verbose Serializer Output

```
Service call
Service         : com.example.MyService
Duration millis : 123
== Input  Pipeline ==============================
+ {com.wm.data.IDataImpl} inputPipeline
+ + {java.lang.String} username = john
+ + {java.lang.String} userPassword = *
+ + {java.lang.String[]} roles
+ + + [0] = admin
+ + + [1] = user
== Output Pipeline ==============================
+ {com.wm.data.IDataImpl} outputPipeline
+ + {java.lang.String} status = success
```

### JSON Compact Serializer Output

```json
{"service":"com.example.MyService","durationMillis":123,"inputPipeline":{"username":{"type":"java.lang.String","value":"john"},"userPassword":{"type":"java.lang.String","value":"*"},"roles":{"type":"java.lang.String[]","value":["admin","user"]}},"outputPipeline":{"status":{"type":"java.lang.String","value":"success"}}}
```

### JSON Simple Serializer Output

```json
{"service":"com.example.MyService","durationMillis":123,"inputPipeline":{"username":"john","userPassword":"*","roles":["admin","user"]},"outputPipeline":{"status":"success"}}
```

## Security Features

### Password Protection

All serializers automatically mask fields containing "password" (case-insensitive):

- `password` → masked
- `userPassword` → masked
- `dbPassword` → masked
- `apiPasswordField` → masked
- `PASSWORD` → masked (case-insensitive)

Masked values are replaced with `*` in all output formats.

### Binary Data Protection

Byte arrays (`byte[]`) are automatically masked with `*` to prevent logging of sensitive binary data.

## Supported Data Types

All serializers handle the following IData types:

- ✅ `String` - Direct string values
- ✅ `String[]` - String arrays
- ✅ `String[][]` - 2D string arrays
- ✅ `IData` - Nested documents (recursive)
- ✅ `IData[]` - Arrays of IData documents
- ✅ `IDataCodable` - Codable objects (converted to IData)
- ✅ `IDataCodable[]` - Arrays of codable objects
- ✅ `byte[]` - Binary data (masked)
- ✅ `Object[]` - Generic object arrays
- ✅ `java.util.Date` - Formatted as ISO 8601
- ✅ `null` - Explicit null values
- ✅ All other types - Via `toString()`

## Building

### Prerequisites

- Java 11 or higher
- Maven 3.6+
- webMethods Integration Server JARs (configured via environment variables)

### Environment Setup

Set the following environment variables before building:

```bash
# Path to directory containing IS JAR files
export WZP_IS_JARS_DIR=/path/to/IntegrationServer/lib/jars

# Path to workspace root (for copying built JAR)
export WZP_WORKSPACE=/path/to/workspace
```

### Build Commands

```bash
# Clean build
mvn clean compile

# Run tests
mvn test

# Package (creates JAR and copies to IS package)
mvn package

# Clean everything (including copied JARs)
mvn clean
```

### Build Output

The build process:
1. Compiles Java sources to `target/classes/`
2. Runs unit tests
3. Creates JAR: `target/2l-wz-pipeline-logger-1.0-SNAPSHOT.jar`
4. Copies JAR to: `${WZP_WORKSPACE}/01-code/is-packages/ZxPipelineLogger/code/jars/static/`

## Development

### Adding a New Serializer

1. Create a new class implementing `PipelineSerializer`:

```java
public class MySerializer implements PipelineSerializer {
    @Override
    public String serialize(String serviceNS, long duration, 
                          IData inboundPipeline, IData outboundPipeline) {
        // Your serialization logic
        return "...";
    }
    
    @Override
    public boolean isEnabled() {
        return Config.INSTANCE.isMySerializerEnabled();
    }
}
```

2. Add configuration support in `Config.java`:

```java
private static boolean bMySerializerEnabled;

public boolean isMySerializerEnabled() {
    return bMySerializerEnabled;
}

// In constructor:
String myEnv = System.getenv("ZX_PIPELINE_LOGGER_MY_ENABLED");
if (myEnv != null) {
    bMySerializerEnabled = "true".equalsIgnoreCase(myEnv);
} else {
    bMySerializerEnabled = false;
}
```

3. Register in `InvokeChainInterceptor` constructor:

```java
serializers.add(new MySerializer());
```

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=JsonSimpleSerializerTest

# Run specific test method
mvn test -Dtest=JsonSimpleSerializerTest#testSerialize_WithPasswordField_MasksValue
```

## Known Issues

### Cursor Empty Detection

Sometimes IDataCursor instances report as empty (`cursor.first()` returns `false`) even when they contain data. This may occur due to:

- Convention violations in pipeline manipulation code
- Keys injected after the interceptor's initial clone
- Concurrent modifications to the pipeline

**Impact**: May result in incomplete logging of pipeline contents.

**Workaround**: Serializers handle empty cursors gracefully by producing empty pipeline objects.

See `KNOWN_ISSUES.md` in the session folder for detailed analysis.

## Dependencies

### Runtime Dependencies

- **webMethods IS Server**: `wm-isserver.jar` (provided by IS)
- **webMethods IS Client**: `wm-isclient.jar` (provided by IS)
- **Jackson Core**: `jackson-core.jar` (provided by IS)
- **Jackson Databind**: `jackson-databind.jar` (provided by IS)
- **Jackson Annotations**: `jackson-annotations.jar` (provided by IS)

### Test Dependencies

- **JUnit 5**: `junit-jupiter` 5.10.1

All dependencies use `system` scope and reference JARs from the Integration Server installation.

## Deployment

1. Build the project: `mvn package`
2. The JAR is automatically copied to the IS package location
3. Deploy the `ZxPipelineLogger` package to Integration Server
4. Configure environment variables as needed
5. Restart Integration Server
6. Verify in server logs:
   ```
   Expert Labs Pipeline logger processor registered
   Pipeline Logger Initialized: masterToggle=true; verboseToggle=false; simpleJsonToggle=true; compactJsonToggle=false
   ```

## Performance Considerations

- Only top-level service calls are logged (not nested calls)
- Pipeline cloning occurs once per service invocation
- Serialization is performed after service execution (doesn't affect service timing)
- Multiple serializers execute sequentially (minimal overhead)
- Failed serializers don't affect other serializers or service execution

## Troubleshooting

### No Logs Appearing

1. Check master toggle: `ZX_PIPELINE_LOGGER_ENABLED=true`
2. Check at least one serializer is enabled
3. Verify environment variables are set before IS startup
4. Check IS server logs for initialization messages
5. Ensure service being called is a top-level service

### Incomplete Pipeline Data

1. Check for cursor empty detection issue (see Known Issues)
2. Verify pipeline is not being modified concurrently
3. Check for proper cursor lifecycle management in service code

### Serialization Errors

Check server logs for error messages. Each serializer has independent error handling and will log specific errors without affecting other serializers.

## License

See LICENSE file in the repository root.

## Contributing

See CONTRIBUTING.md in the repository root.

## Version History

### 1.0-SNAPSHOT (Current)
- Initial implementation with three serializers
- Enhanced password protection (contains "password")
- Non-exclusive serializer toggles
- Comprehensive type support
- Production-ready JSON output
