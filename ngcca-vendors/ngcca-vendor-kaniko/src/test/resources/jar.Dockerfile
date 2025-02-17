FROM 192.168.146.128:5000/base/java11:tomcat9.0-openjdk11

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' > /etc/timezone

ENV LANG="en_US.UTF-8"
ENV TERM=xterm
ENV TIMESTAMP currentTime

RUN mkdir -p /home/admin/app/
RUN wget -q 'http://120.78.225.168:28080/files/java/demo.jar' -O /home/admin/app/app.jar
RUN echo -e 'exec java -Xms128m -Xmx512m -jar  /home/admin/app/app.jar' > /home/admin/start.sh && chmod +x /home/admin/start.sh

WORKDIR $ADMIN_HOME

CMD ["/bin/bash", "/home/admin/start.sh"]