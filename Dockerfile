FROM bellsoft/liberica-openjdk-alpine:17.0.5

# Default to UTF-8 file.encoding
ENV LANG C.UTF-8

RUN apk --no-cache upgrade && apk --no-cache add ca-certificates

# Amazon's certificate is required to access AWS RDS services
# You can comment this lines out if you don't use AWS RDS
RUN wget -qO - https://s3.amazonaws.com/rds-downloads/rds-ca-2019-root.pem       | keytool -importcert -noprompt -cacerts -storepass changeit -alias rds-ca-2019-root \
 && wget -qO - https://s3.amazonaws.com/rds-downloads/rds-combined-ca-bundle.pem | keytool -importcert -noprompt -cacerts -storepass changeit -alias rds-combined-ca-bundle

# Add apiservver user and group
RUN addgroup apiserver && adduser -D -G apiserver apiserver
# Create apiserver directory to run application
RUN mkdir -p /home/apiserver && chown -R apiserver:apiserver /home/apiserver
# Set user to apiserver:apiserver
USER apiserver:apiserver
# Copy apiserver JAR as apiserver user and group
COPY --chown=apiserver:apiserver ./app/infra/build/libs/infra-all.jar /home/apiserver/apiserver.jar
# Set work directory to apiserver
WORKDIR /home/apiserver

ENV PORT 8080
EXPOSE 8080

CMD ["java", "-server", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=100", "-XX:+UseStringDeduplication", "-jar", "apiserver.jar"]
