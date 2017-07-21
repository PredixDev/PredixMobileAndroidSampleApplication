#!/bin/bash

PM_HOST=$(pm api | cut -d "/" -f 3)
echo "Host identified as: ${PM_HOST}"

FILE="app/src/main/assets/preference_defaults.properties"

if [ -f "$FILE" ]
then
    echo "$FILE found."
    source $FILE
    VERIFY_SETTINGS=$server_hostname
    if [ ${VERIFY_SETTINGS} != "Server" ]; then
      echo "Settings properties structure different from expected. Unable to set pm host"
    else
        sed -i '' "/server_hostname=/ s/=.*/=${PM_HOST}/" $FILE
        echo "pm host set to: ${PM_HOST}"
    fi
else
    echo "$FILE not found."
fi