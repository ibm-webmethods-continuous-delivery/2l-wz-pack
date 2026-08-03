# 02-compile — Build Pipeline

This folder contains the tools to build the project from source.
The build is split into two sequential stages, each driven by a Docker Compose project:

```
util/clean-for-compile.sh   ← step 0 – clean generated artefacts
01-jar-build/               ← step 1 – compile Java source into JARs
02-jcode-build/             ← step 2 – compile IS packages (jcode)
```

## Overall process

```
clean  →  jar-build  →  jcode-build
```

1. **Clean** — removes stale `.frag`, `.class`, and `.jar` files from `01-code/is-packages/`.
2. **Jar build** — uses an MSR image as a source of IS runtime JARs, then runs Maven inside an Alpine container to compile the Java modules and drop the resulting JARs into each IS package's `code/jars/static/` folder.
3. **Jcode build** — mounts the IS packages directly into an MSR container and runs `jcode.sh all` to compile the IS package Java code.

## Util scripts

Located in [`util/`](util/).

| Script | Purpose |
|---|---|
| `clean-for-compile.sh` / `.ps1` | Removes `*.frag`, `*.class`, `*.jar` from `01-code/is-packages/` — run before any build |
| `remove-bak-files.sh` / `.ps1` | Removes `*.bak` files left by editors or tooling |

Run the clean step before both build stages to avoid stale artefacts interfering with the build.

## Common environment variables

Both build stages share the same variable conventions.

| Variable | Description |
|---|---|
| `WZP_MSR_IMAGE` | Full image reference (registry/name:tag) |
| `WZP_WM_HOME` | Product home inside the image (`/opt/softwareag` for 10.x/11.x, `/opt/webmethods` for 12.x) |
| `WZP_LOCAL_UID` | Host user ID — output of `id -u`; ensures mounted files are owned by the calling user |
| `WZP_LOCAL_GID` | Host group ID — output of `id -g` |

Copy `EXAMPLE.env` to `.env` in each sub-folder and fill in the values for your target MSR version before running.

## Test all versions

```sh
./test-all-versions.sh
```

Runs the full pipeline (clean → jar-build → jcode-build) against all supported
MSR versions in sequence and prints a consolidated summary:

```
========================================================================
  RESULTS SUMMARY
========================================================================
  PASS  ibmwebmethods.azurecr.io/webmethods-microservicesruntime:10.11.0.2-ubi
  FAIL  [jar-build]   ibmwebmethods.azurecr.io/webmethods-microservicesruntime:11.1.0.12
  PASS  ibmwebmethods.azurecr.io/webmethods-microservicesruntime:12.1.0.2
========================================================================
```

When a step fails the failed stage is shown in brackets (`[clean]`, `[jar-build]`,
`[jcode-build]`) and the remaining steps for that version are skipped. Exits
non-zero if any version fails.

## Supported MSR versions

| Version | Image | `WZP_WM_HOME` |
|---|---|---|
| 10.x | `ibmwebmethods.azurecr.io/webmethods-microservicesruntime:10.11.0.2-ubi` | `/opt/softwareag` |
| 11.x | `ibmwebmethods.azurecr.io/webmethods-microservicesruntime:11.1.0.12` | `/opt/softwareag` |
| 12.x | `ibmwebmethods.azurecr.io/webmethods-microservicesruntime:12.1.0.2` | `/opt/webmethods` |
