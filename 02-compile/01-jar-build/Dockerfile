# syntax=docker/dockerfile:1
# ---------------------------------------------------------------------------
# Stage 1 – extract IS JARs from the MSR image so Maven can compile against them
# ---------------------------------------------------------------------------
ARG __wzp_compiler_image=ibmwebmethods.azurecr.io/webmethods-microservicesruntime:12.1.0.2

FROM ${__wzp_compiler_image} AS jarsource

# ---------------------------------------------------------------------------
# Stage 2 – Maven build (Alpine + OpenJDK)
#
# WZP_WM_HOME differs by MSR version:
#   10.x / 11.x  ->  /opt/softwareag
#   12.x         ->  /opt/webmethods
#
# The JDK installed by apk matches the version bundled in the MSR image so
# that ${java.specification.version} in the pom.xml files resolves correctly.
# ---------------------------------------------------------------------------
FROM alpine:3.24 AS jarbuilder

ARG __wzp_wm_home=/opt/webmethods
# Local host UID/GID so that files written into the mounted volumes are owned
# by the calling user rather than by the image's default root or image user.
ARG __wzp_local_uid=1001
ARG __wzp_local_gid=1001

ENV WZP_IS_JARS_DIR=/cp
ENV WZP_WORKSPACE=/workspace

# Copy the IS JARs Maven needs as system-scope dependencies.
# Jackson JARs may live in either location depending on the MSR version.
COPY --from=jarsource ${__wzp_wm_home}/common/lib/wm-isclient.jar                   /cp/
COPY --from=jarsource ${__wzp_wm_home}/IntegrationServer/lib/wm-isserver.jar        /cp/
COPY --from=jarsource ${__wzp_wm_home}/IntegrationServer/lib/jars/jackson*.jar      /cp/
COPY --from=jarsource ${__wzp_wm_home}/common/lib/ext/jackson*.jar                  /cp/

# hadolint ignore=DL3018
RUN apk --no-cache add maven \
    && { getent group ${__wzp_local_gid} >/dev/null 2>&1 \
        || addgroup -g ${__wzp_local_gid} buildgrp; } \
    && { getent passwd ${__wzp_local_uid} >/dev/null 2>&1 \
        || adduser -D -u ${__wzp_local_uid} -G "$(getent group ${__wzp_local_gid} | cut -d: -f1)" builduser; } \
    && mkdir -p /cp /workspace \
    && chown -R ${__wzp_local_uid}:${__wzp_local_gid} /cp /workspace

COPY scripts/entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh \
    && chown ${__wzp_local_uid}:${__wzp_local_gid} /entrypoint.sh

USER ${__wzp_local_uid}

ENTRYPOINT ["/entrypoint.sh"]
