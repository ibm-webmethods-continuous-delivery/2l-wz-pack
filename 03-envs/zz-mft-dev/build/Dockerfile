ARG __ibm_wm_mft_image=ibmwebmethods.azurecr.io/webmethods-activetransfer:11.1.0.4
FROM ibmwebmethods.azurecr.io/webmethods-activetransfer:11.1.0.0 as at-zero
FROM ${__ibm_wm_mft_image}

# For local laboratories we :
# - use a different user id
# - mount the logs folder as a volume
# - already declare a default volume for virtual file system based on local folder

ARG __new_user_id=1001
ARG __new_user_id=1001
ARG __img_user_name=sagadmin
# It seems MFT comes with group root
# ARG __img_grp_name=sagadmin

ARG __at_vfs_folder=/mnt/our/vfs
ARG __at_injected_config_folder=/mnt/injected/config
ARG __at_injected_certs_folder=/mnt/injected/certs

# Mind var dependencies
ARG __at_guest_is_home=/opt/softwareag/IntegrationServer/instances/default
ARG __at_logs_folder=${__at_guest_is_home}/logs
ARG __at_cf_pck_path=${__at_guest_is_home}/packages/ZzMftConfig
ARG __at_cf_pck_lt_path=${__at_guest_is_home}/packages/ZzMftConfigLocalTest

USER root

## Work around bug MAT-25094 - to remove when fixed
COPY --from=at-zero \
  /opt/softwareag/IntegrationServer/instances/default/packages/WmCDS/pub/assets/i18n/cds/en.json \
  /opt/softwareag/IntegrationServer/instances/default/packages/WmCDS/pub/assets/i18n/cds/en.json

RUN mkdir -p \
      "${__at_cf_pck_lt_path}" \
      "${__at_cf_pck_path}" \
      "${__at_injected_certs_folder}" \
      "${__at_injected_config_folder}" \
      "${__at_logs_folder}" \
      "${__at_vfs_folder}" \
    && usermod -u ${__new_user_id} ${__img_user_name} \
    && chown -R ${__new_user_id}:0 \
      "${__at_cf_pck_lt_path}" \
      "${__at_cf_pck_path}" \
      "${__at_injected_certs_folder}" \
      "${__at_injected_config_folder}" \
      "${__at_logs_folder}" \
      "${__at_vfs_folder}" \
      /opt/softwareag

USER sagadmin

VOLUME \
      "${__at_cf_pck_lt_path}" \
      "${__at_cf_pck_path}" \
      "${__at_injected_certs_folder}" \
      "${__at_injected_config_folder}" \
      "${__at_logs_folder}" \
      "${__at_vfs_folder}"