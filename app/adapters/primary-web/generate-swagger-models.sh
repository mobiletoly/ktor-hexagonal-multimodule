#!/bin/bash

rm -rf app/adapters/primary-web/src/main/kotlin/adapters/primaryweb/gen/models
openapi-generator-cli generate \
    -g kotlin \
    -i app/adapters/primary-web/src/main/resources/openapi/addrbook.yaml \
    -o ./app/adapters/primary-web \
    --global-property=models \
    --additional-properties=serializationLibrary=kotlinx_serialization \
    -p packageName=adapters.primaryweb.gen
