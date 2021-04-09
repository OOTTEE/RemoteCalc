## Stage 1 : build with maven builder image with native capabilities
FROM quay.io/quarkus/centos-quarkus-maven:21.0.0-java11 AS build
COPY pom.xml /usr/src/app/
COPY polish-calc-api /usr/src/app/polish-calc-api
COPY polish-calc-server /usr/src/app/polish-calc-server

RUN mvn -f /usr/src/app/pom.xml -P server -B de.qaware.maven:go-offline-maven-plugin:1.2.5:resolve-dependencies
#RUN mvn -f /usr/src/app/polish-calc-api/pom.xml -B de.qaware.maven:go-offline-maven-plugin:1.2.5:resolve-dependencies
#RUN mvn -f /usr/src/app/polish-calc-server/pom.xml -B de.qaware.maven:go-offline-maven-plugin:1.2.5:resolve-dependencies

USER root
RUN chown -R quarkus /usr/src/app
USER quarkus
RUN mvn -f /usr/src/app/polish-calc-api/pom.xml clean install \
    && mvn -f /usr/src/app/pom.xml -Pnative,server clean package

## Stage 2 : create the docker final image
FROM registry.access.redhat.com/ubi8/ubi-minimal
WORKDIR /work/
COPY --from=build /usr/src/app/polish-calc-server/target/*-runner /work/application

# set up permissions for user `1001`
RUN chmod 775 /work /work/application \
  && chown -R 1001 /work \
  && chmod -R "g+rwX" /work \
  && chown -R 1001:root /work

EXPOSE 41000
USER 1001

CMD ["./application"]