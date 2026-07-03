#!/bin/sh

if [ ! -f "${WZP_GUEST_IS_HOME}/bin/jcode.sh" ]; then
    echo "ERROR - jcode tool is not present."
    echo "ERROR - Please ensure you are using the correct image and the WZP_GUEST_IS_HOME env var is set appropriately!"
    exit 1
fi

cd "${WZP_GUEST_IS_HOME}/bin/" || exit 2
./jcode.sh all

result=$?

if [ $result -ne 0 ]; then
    echo "Stopping for debug..."
    tail -f /dev/null
fi

exit $result
