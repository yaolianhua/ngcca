FROM harbor.local:5000/library/maven:3.8-openjdk-11-slim AS builder

WORKDIR /workspace

COPY . .
RUN mvn clean package -Dmaven.test.skip=true

FROM harbor.local:5000/library/java11-runtime:latest

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' > /etc/timezone

ENV LANG="en_US.UTF-8"
ENV TERM=xterm
ENV TIMESTAMP currentTime

RUN mkdir -p /home/admin/app/
COPY --from=builder /workspace/target/*.jar /home/admin/app/app.jar
RUN echo -e 'exec java -Xms128m -Xmx512m -jar  /home/admin/app/app.jar' > /home/admin/start.sh && chmod +x /home/admin/start.sh

WORKDIR $ADMIN_HOME

CMD ["/bin/bash", "/home/admin/start.sh"]