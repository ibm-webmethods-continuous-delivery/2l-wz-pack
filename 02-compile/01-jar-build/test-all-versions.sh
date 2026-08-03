#!/bin/sh
# test-all-versions.sh
# Builds and runs the jar-builder against every supported MSR version.
# Prints a summary table at the end showing which versions passed/failed.
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

# ---------------------------------------------------------------------------
# Matrix: "IMAGE|WM_HOME"
# ---------------------------------------------------------------------------
MATRIX="
ibmwebmethods.azurecr.io/webmethods-microservicesruntime:10.11.0.2-ubi|/opt/softwareag
ibmwebmethods.azurecr.io/webmethods-microservicesruntime:11.1.0.12|/opt/softwareag
ibmwebmethods.azurecr.io/webmethods-microservicesruntime:12.1.0.2|/opt/webmethods
iwhicr.azurecr.io/webmethods-edge-runtime:12.0.3|/opt/webmethods
"

PASS_LIST=""
FAIL_LIST=""

for entry in $MATRIX; do
    IMAGE="${entry%%|*}"
    WM_HOME="${entry##*|}"

    echo ""
    echo "========================================================================"
    echo "  Testing: $IMAGE"
    echo "  WM_HOME: $WM_HOME"
    echo "========================================================================"

    if WZP_MSR_IMAGE="$IMAGE" WZP_WM_HOME="$WM_HOME" \
        WZP_LOCAL_UID="$(id -u)" WZP_LOCAL_GID="$(id -g)" \
        docker compose build --no-cache --progress=plain 2>&1 \
        && WZP_MSR_IMAGE="$IMAGE" WZP_WM_HOME="$WM_HOME" \
        WZP_LOCAL_UID="$(id -u)" WZP_LOCAL_GID="$(id -g)" \
        docker compose run --rm jarbuilder; then
        PASS_LIST="${PASS_LIST}  PASS  $IMAGE\n"
    else
        FAIL_LIST="${FAIL_LIST}  FAIL  $IMAGE\n"
    fi
done

# ---------------------------------------------------------------------------
# Summary
# ---------------------------------------------------------------------------
echo ""
echo "========================================================================"
echo "  RESULTS SUMMARY"
echo "========================================================================"

if [ -n "$PASS_LIST" ]; then
    printf "%b" "$PASS_LIST"
fi

if [ -n "$FAIL_LIST" ]; then
    printf "%b" "$FAIL_LIST"
fi

echo "========================================================================"

# Exit non-zero if any version failed
if [ -n "$FAIL_LIST" ]; then
    exit 1
fi
