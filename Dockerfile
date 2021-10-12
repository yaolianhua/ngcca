FROM maven:3.8.1-jdk-11 AS builder

WORKDIR /build/

COPY . .
RUN mvn -Dmaven.test.skip=true clean package
RUN cp hotCloud-starter/target/hotCloud-starter.jar .
RUN java -Djarmode=layertools -jar hotCloud-starter.jar extract

FROM openjdk:11.0.12-jre-slim-buster

LABEL maintainer="<yaolianhua789@gmail.com>"

ARG HOTCLOUD_VERSION
LABEL hotCloud.version="${HOTCLOUD_VERSION}"

WORKDIR /hotCloud
COPY --from=builder /build/dependencies/ .
COPY --from=builder /build/spring-boot-loader/ .
COPY --from=builder /build/application/ .


ENV ARG HOTCLOUD_VERSION=${HOTCLOUD_VERSION}

ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/hotCloud/starter"
EXPOSE 8080

CMD java $JAVA_OPTS -Dspring.profiles.active=production org.springframework.boot.loader.JarLauncher
