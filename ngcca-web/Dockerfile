FROM openjdk:17-slim-buster AS builder

WORKDIR /build/

COPY ngcca-web/target/web-server.jar .
RUN java -Djarmode=layertools -jar web-server.jar extract

FROM openjdk:17-slim-buster
LABEL maintainer="<yaolianhua789@gmail.com>"

WORKDIR /web-server/
COPY --from=builder /build/dependencies/ .
COPY --from=builder /build/spring-boot-loader/ .
COPY --from=builder /build/snapshot-dependencies/ ./
COPY --from=builder /build/application/ .

ENV LANG="en_US.UTF-8"
ENV TERM=xterm
ENV TIMESTAMP currentTime
ENV JAVA_OPTS="-Xms256m -Xmx1024m"

EXPOSE 4000

CMD java $JAVA_OPTS -Dspring.profiles.active=container org.springframework.boot.loader.JarLauncher
