# ZxPipelineLogger - Integration Server Package

A webMethods Integration Server package that provides flexible pipeline logging capabilities through an invoke chain interceptor. This package logs input and output pipeline data for service invocations in multiple configurable formats.

## Overview

ZxPipelineLogger automatically intercepts top-level service calls and logs their pipeline data using configurable serialization strategies. It's designed for both development debugging and production monitoring scenarios.

## Features

- 🔍 **Automatic Pipeline Logging**: Intercepts service calls without code changes
- 📊 **Multiple Output Formats**: Verbose (development) and JSON (production)
- 🔒 **Security Built-in**: Automatic password field masking
- ⚙️ **Runtime Configuration**: Control via environment variables
- 🎯 **Top-Level Only**: Logs only top-level services to avoid recursion
- 🛡️ **Error Resilient**: Failures don't affect service execution

## Package Contents

```
ZxPipelineLogger/
├── code/
│   └── jars/
│       └── static/
│           └── 2l-wz-pipeline-logger-1.0-SNAPSHOT.jar
├── config/
├── lib/
├── ns/
├── resources/
├── manifest.v3
└── README.md (this file)
```

## Installation

### Prerequisites

- webMethods Integration Server 10.x or higher
- Java 11 runtime (included with IS 10.15+)
- Jackson JSON libraries (included with IS)

### Deployment Steps

1. **Copy Package to IS**:
   ```bash
   cp -r ZxPipelineLogger $SAG_HOME/IntegrationServer/packages/
   ```

2. **Set Environment Variables** (before starting IS):
   
   **Linux/Unix** (`$SAG_HOME/profiles/IS_default/bin/setenv.sh`):
   ```bash
   export ZX_PIPELINE_LOGGER_ENABLED=true
   export ZX_PIPELINE_LOGGER_JSON_SIMPLE_ENABLED=true
   ```
   
   **Windows** (`%SAG_HOME%\profiles\IS_default\bin\setenv.bat`):
   ```batch
   set ZX_PIPELINE_LOGGER_ENABLED=true
   set ZX_PIPELINE_LOGGER_JSON_SIMPLE_ENABLED=true
   ```

3. **Restart Integration Server**

4. **Verify Installation**:
   
   Check the server log for initialization messages:
   ```
   Expert Labs Pipeline logger processor registered
   Pipeline Logger Initialized: masterToggle=true; verboseToggle=false; simpleJsonToggle=true; compactJsonToggle=false
   ```

## Configuration

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `ZX_PIPELINE_LOGGER_ENABLED` | `false` | **Master toggle** - Must be `true` for any logging |
| `ZX_PIPELINE_LOGGER_VERBOSE_ENABLED` | `false` | Multi-line verbose output (development) |
| `ZX_PIPELINE_LOGGER_JSON_COMPACT_ENABLED` | `false` | Single-line JSON with type info (production) |
| `ZX_PIPELINE_LOGGER_JSON_SIMPLE_ENABLED` | `true` | Single-line JSON without type info (production) |

### Configuration Profiles

#### Development Environment

For detailed debugging with readable multi-line output:

```bash
export ZX_PIPELINE_LOGGER_ENABLED=true
export ZX_PIPELINE_LOGGER_VERBOSE_ENABLED=true
export ZX_PIPELINE_LOGGER_JSON_COMPACT_ENABLED=false
export ZX_PIPELINE_LOGGER_JSON_SIMPLE_ENABLED=false
```

#### Production Environment (Recommended)

For log aggregation systems with simplified JSON:

```bash
export ZX_PIPELINE_LOGGER_ENABLED=true
export ZX_PIPELINE_LOGGER_JSON_SIMPLE_ENABLED=true
```

#### Production with Full Type Information

When you need complete type information in logs:

```bash
export ZX_PIPELINE_LOGGER_ENABLED=true
export ZX_PIPELINE_LOGGER_JSON_COMPACT_ENABLED=true
export ZX_PIPELINE_LOGGER_JSON_SIMPLE_ENABLED=false
```

#### Multiple Formats Simultaneously

Enable multiple serializers for different analysis needs:

```bash
export ZX_PIPELINE_LOGGER_ENABLED=true
export ZX_PIPELINE_LOGGER_JSON_COMPACT_ENABLED=true
export ZX_PIPELINE_LOGGER_JSON_SIMPLE_ENABLED=true
```

### Runtime Configuration Changes

⚠️ **Important**: Environment variables are read only during package initialization. To change configuration:

1. Update environment variables in `setenv.sh` or `setenv.bat`
2. Restart Integration Server
3. Verify new settings in server log

## Output Formats

### Verbose Format (Development)

Multi-line, indented output with full type information:

```
Service call
Service         : com.example.services:processOrder
Duration millis : 245
== Input  Pipeline ==============================
+ {com.wm.data.IDataImpl} inputPipeline
+ + {java.lang.String} orderId = ORD-12345
+ + {java.lang.String} customerPassword = *
+ + {java.lang.String[]} items
+ + + [0] = ITEM-001
+ + + [1] = ITEM-002
== Output Pipeline ==============================
+ {com.wm.data.IDataImpl} outputPipeline
+ + {java.lang.String} status = SUCCESS
+ + {java.lang.String} confirmationId = CONF-67890
```

### JSON Simple Format (Production - Default)

Single-line JSON with key-value pairs only:

```json
{"service":"com.example.services:processOrder","durationMillis":245,"inputPipeline":{"orderId":"ORD-12345","customerPassword":"*","items":["ITEM-001","ITEM-002"]},"outputPipeline":{"status":"SUCCESS","confirmationId":"CONF-67890"}}
```

### JSON Compact Format (Production - Full Info)

Single-line JSON with complete type information:

```json
{"service":"com.example.services:processOrder","durationMillis":245,"inputPipeline":{"orderId":{"type":"java.lang.String","value":"ORD-12345"},"customerPassword":{"type":"java.lang.String","value":"*"},"items":{"type":"java.lang.String[]","value":["ITEM-001","ITEM-002"]}},"outputPipeline":{"status":{"type":"java.lang.String","value":"SUCCESS"},"confirmationId":{"type":"java.lang.String","value":"CONF-67890"}}}
```

## Security Features

### Automatic Password Masking

All fields containing "password" (case-insensitive) are automatically masked:

- `password` → `*`
- `userPassword` → `*`
- `dbPassword` → `*`
- `apiPasswordField` → `*`
- `PASSWORD` → `*`

This applies to:
- String values
- String arrays (all elements masked)
- 2D string arrays (all elements masked)
- Object arrays (all elements masked)

### Binary Data Protection

Byte arrays (`byte[]`) are automatically masked with `*` to prevent logging of sensitive binary data.

## Log Location

Pipeline logs are written to the Integration Server log using the `JournalLogger` facility:

- **Log Level**: `LOG_EXCEPTION` (always logged, even in production)
- **Facility**: `FAC_LICENSE_MGR`
- **Location**: `$SAG_HOME/IntegrationServer/logs/server.log`

### Viewing Logs

**Real-time monitoring**:
```bash
tail -f $SAG_HOME/IntegrationServer/logs/server.log | grep "Pipeline"
```

**Search for specific service**:
```bash
grep "com.example.services:processOrder" $SAG_HOME/IntegrationServer/logs/server.log
```

**Extract JSON logs**:
```bash
grep '{"service":' $SAG_HOME/IntegrationServer/logs/server.log > pipeline-logs.json
```

## Performance Impact

### Minimal Overhead

- ✅ Only top-level services are logged (nested calls are skipped)
- ✅ Pipeline cloning occurs once per service invocation
- ✅ Serialization happens after service execution (doesn't affect service timing)
- ✅ Failed serializers don't affect service execution

### Performance Characteristics

| Aspect | Impact |
|--------|--------|
| Service Execution Time | No impact (logging is post-execution) |
| Memory Usage | One pipeline clone per logged service |
| CPU Usage | Minimal (serialization is efficient) |
| Log Volume | Depends on service call frequency and pipeline size |

### Recommendations

- **Development**: Use verbose format for detailed debugging
- **Production**: Use JSON simple format for minimal overhead
- **Monitoring**: Use JSON compact format when type information is needed
- **High-Volume Systems**: Consider enabling only for specific services or time periods

## Troubleshooting

### No Logs Appearing

**Symptom**: Services execute but no pipeline logs appear.

**Solutions**:
1. Verify master toggle is enabled:
   ```bash
   echo $ZX_PIPELINE_LOGGER_ENABLED
   # Should output: true
   ```

2. Verify at least one serializer is enabled:
   ```bash
   echo $ZX_PIPELINE_LOGGER_JSON_SIMPLE_ENABLED
   # Should output: true
   ```

3. Check package is loaded:
   - Navigate to IS Admin Console → Packages
   - Verify `ZxPipelineLogger` is listed and enabled

4. Check initialization in server log:
   ```bash
   grep "Pipeline logger processor registered" $SAG_HOME/IntegrationServer/logs/server.log
   ```

5. Ensure environment variables were set **before** IS startup

### Incomplete Pipeline Data

**Symptom**: Some fields are missing from logged pipeline data.

**Possible Causes**:
- Cursor empty detection issue (see Known Issues below)
- Pipeline modified concurrently by another thread
- Improper cursor lifecycle management in service code

**Solutions**:
1. Check for cursor lifecycle violations in service code
2. Ensure proper IData/IDataCursor usage patterns
3. Review service code for concurrent pipeline modifications

### Serialization Errors

**Symptom**: Error messages in server log about serialization failures.

**Solutions**:
1. Check server log for specific error details
2. Verify Jackson libraries are available (included with IS)
3. Check for custom objects that don't implement `toString()` properly
4. Consider using a different serializer format

### High Log Volume

**Symptom**: Server logs grow rapidly, disk space issues.

**Solutions**:
1. Disable logging temporarily:
   ```bash
   export ZX_PIPELINE_LOGGER_ENABLED=false
   ```
   Then restart IS.

2. Implement log rotation in IS configuration

3. Use log filtering to capture only specific services

4. Consider enabling logging only during specific time windows

## Known Issues

### Cursor Empty Detection

**Issue**: Sometimes IDataCursor instances report as empty (`cursor.first()` returns `false`) even when they contain data.

**Impact**: May result in incomplete logging of pipeline contents.

**Workaround**: Serializers handle empty cursors gracefully by producing empty pipeline objects in the output.

**Status**: Under investigation. Does not affect service execution.

For detailed analysis, see the session documentation in the source repository.

## Integration with Log Management Systems

### Splunk

```splunk
index=webmethods sourcetype=is_server "service"="*" 
| spath 
| table _time service durationMillis inputPipeline.* outputPipeline.*
```

### ELK Stack (Elasticsearch, Logstash, Kibana)

**Logstash Configuration**:
```ruby
filter {
  if [message] =~ /^\{\"service\":/ {
    json {
      source => "message"
      target => "pipeline"
    }
  }
}
```

### CloudWatch Logs

Use CloudWatch Logs Insights:
```
fields @timestamp, service, durationMillis
| filter @message like /{"service":/
| parse @message '{"service":"*","durationMillis":*' as service, duration
| stats avg(duration) by service
```

## Uninstallation

1. **Disable Logging**:
   ```bash
   export ZX_PIPELINE_LOGGER_ENABLED=false
   ```

2. **Restart Integration Server**

3. **Remove Package**:
   - Navigate to IS Admin Console → Packages
   - Select `ZxPipelineLogger`
   - Click "Delete"
   
   Or manually:
   ```bash
   rm -rf $SAG_HOME/IntegrationServer/packages/ZxPipelineLogger
   ```

4. **Remove Environment Variables** from `setenv.sh` or `setenv.bat`

5. **Restart Integration Server** (final cleanup)

## Support and Documentation

### Source Code

The source code for this package is maintained in the `2l-wz-pack` repository:
- Maven Project: `01-code/java/2l-wz-pipelinelogger/`
- IS Package: `01-code/is-packages/ZxPipelineLogger/`

### Building from Source

See the Maven project README for build instructions:
```bash
cd 01-code/java/2l-wz-pipelinelogger
cat README.md
```

### Reporting Issues

Report issues through your organization's standard support channels or the repository issue tracker.

## Version History

### 1.0-SNAPSHOT (Current)
- Initial release
- Three serialization formats (Verbose, JSON Compact, JSON Simple)
- Enhanced password protection
- Non-exclusive serializer toggles
- Comprehensive type support
- Production-ready JSON output

## License

See LICENSE file in the repository root.

## Credits

Developed by IBM Expert Labs for webMethods Integration Server pipeline monitoring and debugging.
