FROM 192.168.146.128:5000/base/java11:tomcat9.0-openjdk11

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' > /etc/timezone

ENV LANG="en_US.UTF-8"
ENV TERM=xterm
ENV TIMESTAMP currentTime

RUN wget -q 'http://192.168.146.128:28080/yaolianhua/java/kaniko-test/jenkins.war' -O /home/admin/tomcat/webapps/app.war

WORKDIR $ADMIN_HOME

CMD ["catalina.sh", "run"]