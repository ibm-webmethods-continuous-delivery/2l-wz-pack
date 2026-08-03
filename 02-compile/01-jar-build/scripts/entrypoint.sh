#!/bin/sh
set -e

MVN_MODULES="
  /workspace/01-code/java/2l-wz-pipelinelogger
  /workspace/01-code/java/2l-zz-shaman
"

for module in $MVN_MODULES; do
    echo "==> Building $module"
    cd "$module"
    mvn package
done

echo "==> All JAR modules built successfully."
