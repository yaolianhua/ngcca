FROM openjdk:11.0.12-jre-slim-buster

LABEL maintainer="<yaolianhua789@gmail.com>"

WORKDIR /web/

COPY hotcloud-web/target/hotcloud-web.jar .
ENV JAVA_OPTS="-Xms128m -Xmx512m"
EXPOSE 4000

CMD java $JAVA_OPTS -Dspring.profiles.active=production -jar hotcloud-web.jar