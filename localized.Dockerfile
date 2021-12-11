FROM openjdk:11.0.12-jre-slim-buster

LABEL maintainer="<yaolianhua789@gmail.com>"

WORKDIR /hotcloud/

COPY hotCloud-starter/target/hotCloud-starter.jar hotCloud-starter.jar

ENV JAVA_OPTS="-Xms128m -Xmx256m"
EXPOSE 8080

CMD java $JAVA_OPTS -jar hotCloud-starter.jar