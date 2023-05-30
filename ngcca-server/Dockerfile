FROM openjdk:17-slim-buster AS builder

WORKDIR /build/

COPY ngcca-server/target/core-server.jar .
RUN java -Djarmode=layertools -jar core-server.jar extract

FROM openjdk:17-slim-buster
LABEL maintainer="<yaolianhua789@gmail.com>"

WORKDIR /core-server/
COPY --from=builder /build/dependencies/ .
COPY --from=builder /build/spring-boot-loader/ .
COPY --from=builder /build/snapshot-dependencies/ ./
COPY --from=builder /build/application/ .

ENV LANG="en_US.UTF-8"
ENV TERM=xterm
ENV TIMESTAMP currentTime
ENV JAVA_OPTS="-Xms256m -Xmx1024m"

EXPOSE 8080

CMD java $JAVA_OPTS -Dspring.profiles.active=container org.springframework.boot.loader.JarLauncher
