#!/bin/sh
# test-all-versions.sh
# Runs the full build pipeline (clean → jar-build → jcode-build) for every
# supported MSR version and prints a pass/fail summary at the end.

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# ---------------------------------------------------------------------------
# Matrix: "IMAGE|WM_HOME|IS_HOME"
# ---------------------------------------------------------------------------
MATRIX="
ibmwebmethods.azurecr.io/webmethods-microservicesruntime:10.11.0.2-ubi|/opt/softwareag|/opt/softwareag/IntegrationServer
ibmwebmethods.azurecr.io/webmethods-microservicesruntime:11.1.0.12|/opt/softwareag|/opt/softwareag/IntegrationServer
ibmwebmethods.azurecr.io/webmethods-microservicesruntime:12.1.0.3|/opt/webmethods|/opt/webmethods/IntegrationServer
iwhicr.azurecr.io/webmethods-edge-runtime:12.0.3|/opt/webmethods|/opt/webmethods/IntegrationServer
"

PASS_LIST=""
FAIL_LIST=""

LOCAL_UID="$(id -u)"
LOCAL_GID="$(id -g)"

for entry in $MATRIX; do
    IMAGE="$(echo "$entry"  | cut -d'|' -f1)"
    WM_HOME="$(echo "$entry" | cut -d'|' -f2)"
    IS_HOME="$(echo "$entry" | cut -d'|' -f3)"

    echo ""
    echo "========================================================================"
    echo "  Testing: $IMAGE"
    echo "  WM_HOME: $WM_HOME"
    echo "========================================================================"

    # ---- step 0: clean -------------------------------------------------------
    echo "  --> step 0: clean"
    if ! "${SCRIPT_DIR}/util/clean-for-compile.sh"; then
        FAIL_LIST="${FAIL_LIST}  FAIL  [clean]       $IMAGE\n"
        continue
    fi

    # ---- step 1: jar-build ---------------------------------------------------
    echo "  --> step 1: jar-build"
    if ! ( cd "${SCRIPT_DIR}/01-jar-build" \
           && WZP_MSR_IMAGE="$IMAGE" WZP_WM_HOME="$WM_HOME" \
              WZP_LOCAL_UID="$LOCAL_UID" WZP_LOCAL_GID="$LOCAL_GID" \
              docker compose build --no-cache --progress=plain \
           && WZP_MSR_IMAGE="$IMAGE" WZP_WM_HOME="$WM_HOME" \
              WZP_LOCAL_UID="$LOCAL_UID" WZP_LOCAL_GID="$LOCAL_GID" \
              docker compose run --rm jarbuilder ); then
        FAIL_LIST="${FAIL_LIST}  FAIL  [jar-build]   $IMAGE\n"
        continue
    fi

    # ---- step 2: jcode-build -------------------------------------------------
    echo "  --> step 2: jcode-build"
    if ! ( cd "${SCRIPT_DIR}/02-jcode-build" \
           && WZP_MSR_IMAGE="$IMAGE" WZP_WM_HOME="$WM_HOME" \
              WZP_GUEST_IS_HOME="$IS_HOME" \
              WZP_GUEST_PACKAGES_FOLDER="${IS_HOME}/packages" \
              WZP_LOCAL_UID="$LOCAL_UID" WZP_LOCAL_GID="$LOCAL_GID" \
              docker compose build --no-cache --progress=plain \
           && WZP_MSR_IMAGE="$IMAGE" WZP_WM_HOME="$WM_HOME" \
              WZP_GUEST_IS_HOME="$IS_HOME" \
              WZP_GUEST_PACKAGES_FOLDER="${IS_HOME}/packages" \
              WZP_LOCAL_UID="$LOCAL_UID" WZP_LOCAL_GID="$LOCAL_GID" \
              docker compose run --rm msr ); then
        FAIL_LIST="${FAIL_LIST}  FAIL  [jcode-build]  $IMAGE\n"
        continue
    fi

    PASS_LIST="${PASS_LIST}  PASS  $IMAGE\n"
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
