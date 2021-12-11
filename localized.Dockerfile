FROM openjdk:11.0.12-jre-slim-buster AS builder

LABEL maintainer="<yaolianhua789@gmail.com>"

COPY hotCloud-starter/target/hotCloud-starter.jar .
RUN java -Djarmode=layertools -jar hotCloud-starter.jar extract

ARG HOTCLOUD_VERSION
LABEL hotCloud.version="${HOTCLOUD_VERSION}"

FROM openjdk:11.0.12-jre-slim-buster
WORKDIR /hotcloud/
COPY --from=builder dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder application/ ./

ENV ARG HOTCLOUD_VERSION=${HOTCLOUD_VERSION}

ENV JAVA_OPTS="-Xms128m -Xmx256m"
EXPOSE 8080

CMD java $JAVA_OPTS -Dspring.profiles.active=production org.springframework.boot.loader.JarLauncher