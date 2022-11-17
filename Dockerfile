FROM harbor.local:5000/library/openjdk:11.0.12-jre-slim-buster

WORKDIR /ngcca/
LABEL maintainer="<yaolianhua789@gmail.com>"

COPY ngcca-server/target/ngcca-server.jar .
RUN java -Djarmode=layertools -jar ngcca-server.jar extract && rm -rf ngcca-server.jar

ENV LANG="en_US.UTF-8"
ENV TERM=xterm
ENV TIMESTAMP currentTime
ENV JAVA_OPTS="-Xms256m -Xmx512m"
EXPOSE 8080

CMD java $JAVA_OPTS -Dspring.profiles.active=production org.springframework.boot.loader.JarLauncher
