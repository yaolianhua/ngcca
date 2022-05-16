FROM openjdk:11.0.12-jre-slim-buster AS builder

LABEL maintainer="<yaolianhua789@gmail.com>"

COPY hotcloud-allinone-serverside/target/hotcloud-allinone.jar .
RUN java -Djarmode=layertools -jar hotcloud-allinone.jar extract

FROM openjdk:11.0.12-jre-slim-buster
WORKDIR /hotcloud/
COPY --from=builder dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder application/ ./

ENV JAVA_OPTS="-Xms256m -Xmx512m"
EXPOSE 8080

CMD java $JAVA_OPTS -Dspring.profiles.active=production org.springframework.boot.loader.JarLauncher