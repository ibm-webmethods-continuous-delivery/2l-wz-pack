# syntax=docker/dockerfile:1
# ---------------------------------------------------------------------------
# Wraps the MSR image and re-maps the default image user (1724) to the local
# host UID/GID so that files written back into the mounted IS packages tree
# are owned by the calling user.
#
# __wzp_wm_home differs by MSR version:
#   10.x / 11.x  ->  /opt/softwareag
#   12.x         ->  /opt/webmethods
# ---------------------------------------------------------------------------
ARG __wzp_compiler_image=ibmwebmethods.azurecr.io/webmethods-microservicesruntime:11.1.0.12

FROM ${__wzp_compiler_image}

ARG __wzp_wm_home=/opt/softwareag
ARG __wzp_local_uid=1001
ARG __wzp_local_gid=1001
ARG __wzp_img_user=sagadmin
ARG __wzp_img_group=sagadmin

USER root

RUN { getent group ${__wzp_local_gid} >/dev/null 2>&1 \
        || groupmod -g ${__wzp_local_gid} ${__wzp_img_group}; } \
    && { getent passwd ${__wzp_local_uid} >/dev/null 2>&1 \
        || usermod -u ${__wzp_local_uid} ${__wzp_img_user}; } \
    && chown -R ${__wzp_local_uid}:${__wzp_local_gid} ${__wzp_wm_home}

USER ${__wzp_local_uid}
