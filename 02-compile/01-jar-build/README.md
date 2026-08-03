# 01-jar-build — Java JAR Compilation

Compiles the Java source modules in `01-code/java/` into JARs and places them
into the `code/jars/static/` folder of each corresponding IS package.

This is **step 1** of the build pipeline — run it after cleaning and before jcode-build.

## How it works

A two-stage Docker build is used:

1. **`jarsource`** stage — pulls the target MSR image to extract the IS runtime JARs (`wm-isclient.jar`, `wm-isserver.jar`, `jackson*.jar`) that Maven needs as `system`-scope dependencies.
2. **`jarbuilder`** stage — Alpine + Maven; copies the extracted JARs into `/cp`, mounts the Java source modules and IS packages tree from the host, then runs `mvn package` for each module.

The Java compilation target is determined automatically by `${java.specification.version}` in each `pom.xml` — it matches the JDK bundled in the chosen MSR image, requiring no manual version configuration.

## Setup

```sh
cp EXAMPLE.env .env
# edit .env — set WZP_MSR_IMAGE, WZP_WM_HOME, WZP_LOCAL_UID, WZP_LOCAL_GID
```

| Variable | Description |
|---|---|
| `WZP_MSR_IMAGE` | Full image reference for the target MSR version |
| `WZP_WM_HOME` | `/opt/softwareag` (10.x/11.x) or `/opt/webmethods` (12.x) |
| `WZP_LOCAL_UID` | Your host UID (`id -u`) — files written to mounted volumes will use this |
| `WZP_LOCAL_GID` | Your host GID (`id -g`) |

## Usage

```sh
./run.sh        # Linux / macOS
run.bat         # Windows
```

`run.sh` automatically injects your current `id -u` / `id -g` — no need to set
`WZP_LOCAL_UID` / `WZP_LOCAL_GID` in `.env` when running from the shell.

## Test all versions

```sh
./test-all-versions.sh
```

Runs the build against all three MSR versions in sequence and prints a summary:

```
========================================================================
  RESULTS SUMMARY
========================================================================
  PASS  ibmwebmethods.azurecr.io/webmethods-microservicesruntime:10.11.0.2-ubi
  PASS  ibmwebmethods.azurecr.io/webmethods-microservicesruntime:11.1.0.12
  PASS  ibmwebmethods.azurecr.io/webmethods-microservicesruntime:12.1.0.2
========================================================================
```

Exits non-zero if any version fails.

## Files

| File | Purpose |
|---|---|
| `Dockerfile` | Two-stage build: jarsource + jarbuilder |
| `docker-compose.yml` | Wires build args and mounts source volumes |
| `EXAMPLE.env` | Template — copy to `.env` |
| `run.sh` / `run.bat` | Build and run for a single configured version |
| `test-all-versions.sh` | Run against all supported MSR versions |
| `scripts/entrypoint.sh` | Runs `mvn package` for each Java module |
