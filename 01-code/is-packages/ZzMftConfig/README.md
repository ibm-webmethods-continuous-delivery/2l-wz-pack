# ZzMftConfig

Declarative webMethods MFT configuration for containerized and Kubernetes-based deployments.

## Overview

ZzMftConfig is a webMethods Integration Server package that reads an external JSON file at startup and declares selected MFT configuration in the target runtime.

The package is intended for infrastructure-level, pod-level, or container-level configuration that must be injected from outside the package, while application-level configuration remains managed in the MFT database.

### Current scope

Version `v0.0.1` focuses on the minimum indispensable MFT configuration needed for containerized deployments:

- MFT certificates
- MFT ports
- MFT gateways
- MFT virtual file systems (VFS)

### Declarative behavior

The package follows a **declare-if-not-exists** pattern:

- If a configuration item does not exist, it is created from the JSON input.
- If a configuration item already exists, database values take precedence.
- Existing configuration is not updated to match the JSON file.
- Processing continues on error and reports item-level results.

This makes the package safe to run at container startup, including repeated startups.

## Primary use case

The primary target is:

- Docker-based deployments
- Kubernetes deployments
- Cloud-native runtime setups where configuration is injected by the environment

Typical patterns include:

- mounting the JSON configuration file as a Kubernetes Secret
- mounting referenced certificate or key files separately
- setting the startup path through `application.properties` and environment variables

## Prerequisites

- webMethods Integration Server `11.1` or later
- webMethods MFT module
- Standard MFT database schema
- An external JSON configuration file available to the runtime

No additional custom package dependencies are required beyond standard webMethods MFT packages.

## How configuration is applied

The package registers the startup service:

- `zz.config.mft.startup:loadFromFile`

At startup, the service reads the global variable:

- `ZzMftConfigStartupTimeJsonFile`

If the variable points to a valid JSON file, the package loads the file and applies the configuration.

If the variable is missing or loading fails:

- startup is not blocked
- failures are logged
- affected MFT configuration may remain unavailable until corrected

## Quick start

1. Install the `ZzMftConfig` package in Integration Server.
2. Place the MFT configuration JSON file outside the package.
3. Mount or inject any referenced certificate, keystore, truststore, or SSH key files into the runtime.
4. Configure `application.properties` to set the startup global variable.
5. Start or restart the container or Integration Server runtime.
6. Review logs for configuration results.

## Startup configuration

Set the global variable through `application.properties`.

Example:

```properties
globalvariable.ZzMftConfigStartupTimeJsonFile=${MFT_CONFIG_FILE_PATH}
```

Then provide the environment variable in the runtime:

```properties
MFT_CONFIG_FILE_PATH=/mnt/config/mft-config.json
```

A hardcoded file path can also be used if environment-variable indirection is not needed.

## Configuration file location

The JSON configuration file should be stored **separately from the package**.

Recommended pattern:

- package contains reusable code
- JSON file contains environment-specific values
- each environment has its own JSON file

Examples:

- `/mnt/config/mft-config-dev.json`
- `/mnt/config/mft-config-test.json`
- `/mnt/config/mft-config-prod.json`

## Supported configuration types

## 1. Certificates

Supported certificate declarations include:

- SSH private keys
- JKS keystores
- truststores

Certificate files are referenced by absolute path in the JSON and are expected to be injected separately into the container or pod.

Example fields:

- `certificateId`
- `certificateAlias`
- `type`
- `path`
- `keyPassword`
- `keyStorePassword`

## 2. Ports

Supported port declarations include:

- SFTP ports
- HTTPS ports

Ports can reference declared certificate IDs where applicable.

Example fields:

- `portName`
- `host`
- `port`
- `protocol`
- `active`

## 3. Gateways

Gateway declarations support core instance details and anti-virus settings.

Example fields:

- `instanceName`
- `host`
- `port`
- `active`
- `autoConnect`
- `antiVirusDetails`

## 4. Virtual File Systems

VFS declarations include VFS details and partner information.

Important behavior:

- VFS requires partner context by product design.
- If no partner is provided, MFT may place the VFS under the special internal category corresponding to “No partner”.
- Users should provide `declaredPartnerInfo` when declaring VFS entries.

## Configuration structure

The JSON structure is based on the Integration Server record:

- `zz.config.mft:allConfigurations`

Top-level sections:

- `declareMftCertificateList`
- `declarePortInfoList`
- `declareGateways`
- `declareVfs`

For creating new configuration instances, the recommended approach is to use webMethods Designer and the example service referenced in the consolidation notes:

- `zz.test.config.mft:getTypicalConfigRecord` in package `ZzMftConfigLocalTest`

## Minimal application flow

```text
Startup
  -> read ZzMftConfigStartupTimeJsonFile
  -> load JSON file
  -> parse configuration
  -> declare certificates
  -> declare ports
  -> declare gateways
  -> declare VFS
  -> return per-item results
```

## Security model for v0.0.1

For `v0.0.1`, the JSON file is expected to be handled as a runtime secret boundary.

Recommended model:

- treat the entire JSON file as a Kubernetes Secret
- do not commit environment-specific JSON files to version control
- inject certificate and key files separately as mounted secrets
- use absolute paths in JSON to point to mounted files
- keep file permissions restricted in the container

In this version:

- passwords may appear in plain text in the JSON file
- passphrases for injected key material may also appear in the JSON file
- this is acceptable only when the JSON file itself is handled as a secret

## Error handling and logging

Current behavior for `v0.0.1`:

- continue on error
- log failures through existing logging/debug mechanisms
- return structured item-level results from application services
- do not block server startup if configuration application fails

## Operational notes

- Configuration is applied at container or server startup.
- Manual changes in the MFT UI persist in the database.
- JSON does not override existing database values.
- To reapply startup JSON, restart the container or runtime.

## Example configuration excerpt

```json
{
  "declareMftCertificateList": [
    {
      "certificateId": "sshKeyRsaExample1",
      "certificateAlias": "sshKeyRsaExample",
      "location": "File",
      "type": "SSH_PrivateKey",
      "keyPassword": "N0t-Manag3",
      "path": "/mnt/injected/certs/manual/server/Key1/TestFixtureKey_RSA.OpenSSH.private.txt",
      "enabled": "true"
    }
  ],
  "declarePortInfoList": [
    {
      "portName": "SFTP1",
      "host": "0.0.0.0",
      "port": "55022",
      "protocol": "SFTP",
      "active": "true"
    }
  ]
}
```

## Out of scope for v0.0.1

The following are intentionally not included in this initial release:

- JSON schema validation
- dry-run validation service
- health check service
- rollback mechanism
- drift detection
- reload without restart
- multi-file configuration merging
- migration or export tooling

## License

Apache 2.0

## Version

`v0.0.1` initial release

## Summary

ZzMftConfig provides a practical bridge between webMethods MFT and container-era deployment practices by allowing external, startup-time declaration of selected MFT runtime configuration from a single environment-specific JSON file.