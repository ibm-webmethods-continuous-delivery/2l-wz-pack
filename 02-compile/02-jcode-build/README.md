# 02-jcode-build — IS Package Compilation

Compiles the IS packages in `01-code/is-packages/` using the `jcode.sh` tool
bundled with the MSR image.

This is **step 2** of the build pipeline — run it after jar-build has placed the
static JARs into each package's `code/jars/static/` folder.

## How it works

A Dockerfile wraps the target MSR image and re-maps its default user (`sagadmin`, UID 1724)
to the local host UID/GID. The IS packages are then bind-mounted directly into
the container's packages folder, and `jcode.sh all` is executed to compile all
package Java code in place.

If the compilation fails the container stays alive (`tail -f /dev/null`) so the
state can be inspected interactively.

## Setup

```sh
cp EXAMPLE.env .env
# edit .env — set WZP_MSR_IMAGE, WZP_WM_HOME, WZP_GUEST_IS_HOME, WZP_LOCAL_UID, WZP_LOCAL_GID
```

| Variable | Description |
|---|---|
| `WZP_MSR_IMAGE` | Full image reference for the target MSR version |
| `WZP_WM_HOME` | `/opt/softwareag` (10.x/11.x) or `/opt/webmethods` (12.x) |
| `WZP_GUEST_IS_HOME` | IS home inside the image (e.g. `$WZP_WM_HOME/IntegrationServer`) |
| `WZP_LOCAL_UID` | Your host UID (`id -u`) |
| `WZP_LOCAL_GID` | Your host GID (`id -g`) |

`WZP_GUEST_PACKAGES_FOLDER` is derived automatically as `${WZP_GUEST_IS_HOME}/packages`.

## Usage

```sh
./run.sh        # Linux / macOS
run.bat         # Windows
```

`run.sh` automatically injects your current `id -u` / `id -g`.

## Files

| File | Purpose |
|---|---|
| `Dockerfile` | Wraps MSR image; re-maps user to host UID/GID |
| `docker-compose.yml` | Wires build args, mounts IS packages and scripts |
| `EXAMPLE.env` | Template — copy to `.env` |
| `run.sh` / `run.bat` | Build and run |
| `scripts/entrypoint.sh` | Validates jcode presence and runs `jcode.sh all` |
