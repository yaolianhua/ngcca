FROM harbor.local:5000/library/openjdk:17-slim-buster

LABEL maintainer="<yaolianhua789@gmail.com>"
WORKDIR /k8sAgent/

COPY ngcca-kubernetes-starter/target/k8sAgent.jar .

ENV LANG="en_US.UTF-8"
ENV TERM=xterm
ENV TIMESTAMP currentTime
ENV JAVA_OPTS="-Xms128m -Xmx256m"

EXPOSE 1400

CMD java $JAVA_OPTS -jar k8sAgent.jar
