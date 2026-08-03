#!/bin/sh
set -e

WZP_LOCAL_UID="$(id -u)" WZP_LOCAL_GID="$(id -g)" docker compose build --no-cache
WZP_LOCAL_UID="$(id -u)" WZP_LOCAL_GID="$(id -g)" docker compose run --rm msr

echo "result is $?"
