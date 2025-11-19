#!/bin/bash

SOURCE_DIR="/home/lociuser/TestProject/scripts"
DEST_DIR="/home/lociuser/apposing_tut/qupath/TestProject/scripts"

echo "Copying files from $SOURCE_DIR to $DEST_DIR..."

rsync -a --ignore-existing "$SOURCE_DIR/" "$DEST_DIR/"

if [ $? -eq 0 ]; then
    echo "--- Copy Complete ---"
else
    echo "--- Copy Failed (check permissions) ---"
fi
