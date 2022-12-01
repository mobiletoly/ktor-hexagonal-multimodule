#!/bin/bash

rm -rf
openapi-generator-cli generate \
    -g kotlin \
    -i app/adapters/primary-web/src/main/resources/openapi/addrbook.yaml \
    -o ./app/adapters/primary-web \
    --global-property=models \
    --additional-properties=serializationLibrary=kotlinx_serialization \
    -p packageName=com.github.mobiletoly.addrbookhexktor.primaryweb
