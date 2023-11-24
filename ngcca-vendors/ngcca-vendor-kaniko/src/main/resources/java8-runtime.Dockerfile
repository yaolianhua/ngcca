FROM 119.91.214.25:5000/library/alpine:latest
LABEL maintainer="<yaolianhua789@gmail.com>"

RUN apk add --no-cache wget
RUN apk add --no-cache bash
RUN apk add --no-cache openjdk8
RUN apk add --no-cache unzip
RUN apk add --no-cache fontconfig
RUN apk add --no-cache ttf-dejavu

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' > /etc/timezone

ENV LANG="en_US.UTF-8"
ENV TERM=xterm
ENV CATALINA_HOME /home/admin/tomcat
ENV ARTHAS_HOME /home/admin/arthas
ENV ADMIN_HOME /home/admin
ENV PATH $PATH:$CATALINA_HOME/bin

RUN mkdir -p /home/admin

RUN wget -q http://119.91.214.25:8888/java/tomcat-9.0.83.tar.gz -O /tmp/apache-tomcat-9.0.83.tar.gz
RUN mkdir -p /home/admin/tomcat && tar -xf /tmp/apache-tomcat-9.0.83.tar.gz -C /home/admin/tomcat --strip-components 1
RUN rm -rf /tmp/apache-tomcat-9.0.83.tar.gz && chmod +x $CATALINA_HOME/bin/*sh

RUN wget -q http://119.91.214.25:8888/java/arthas-bin-3.6.9.zip -O /tmp/arthas-bin-3.6.9.zip
RUN mkdir -p /home/admin/arthas && unzip /tmp/arthas-bin-3.6.9.zip -d $ARTHAS_HOME
RUN rm -rf /tmp/arthas-bin-3.6.9.zip && chmod +x $ARTHAS_HOME/*sh

WORKDIR $ADMIN_HOME