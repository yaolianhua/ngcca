FROM maven:3.8.3-jdk-11-slim AS builder

WORKDIR /build/

COPY . .
RUN mvn clean package
RUN cp hotcloud-starter/target/hotcloud-starter.jar .
RUN java -Djarmode=layertools -jar hotcloud-starter.jar extract

FROM openjdk:11.0.12-jre-slim-buster

LABEL maintainer="<yaolianhua789@gmail.com>"

WORKDIR /hotcloud/
COPY --from=builder /build/dependencies/ .
COPY --from=builder /build/snapshot-dependencies/ .
COPY --from=builder /build/spring-boot-loader/ .
COPY --from=builder /build/application/ .


ENV JAVA_OPTS="-Xms128m -Xmx256m"
EXPOSE 8080

CMD java $JAVA_OPTS -Dspring.profiles.active=production org.springframework.boot.loader.JarLauncher
