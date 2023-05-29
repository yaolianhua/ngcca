FROM harbor.local:5000/library/alpine:latest
LABEL maintainer="<yaolianhua789@gmail.com>"

RUN apk add --no-cache wget
RUN apk add --no-cache bash
RUN apk add unzip
RUN apk add openjdk17
RUN apk add fontconfig
RUN apk add ttf-dejavu

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' > /etc/timezone

ENV LANG="en_US.UTF-8"
ENV TERM=xterm
ENV CATALINA_HOME /home/admin/tomcat
ENV ADMIN_HOME /home/admin
ENV ARTHAS_HOME /home/admin/arthas
ENV PATH $PATH:$CATALINA_HOME/bin

RUN mkdir -p /home/admin

ARG FILE_SERVER
RUN wget -q ${FILE_SERVER}/Downloads/apache-tomcat-10.1.5.tar.gz -O /tmp/apache-tomcat-10.1.5.tar.gz
RUN mkdir -p /home/admin/tomcat && tar -xf /tmp/apache-tomcat-10.1.5.tar.gz -C /home/admin/tomcat --strip-components 1
RUN rm /tmp/apache-tomcat-10.1.5.tar.gz && chmod +x $CATALINA_HOME/bin/*sh

RUN wget -q ${FILE_SERVER}/Downloads/arthas-packaging-3.6.7-bin.zip -O /tmp/arthas-packaging-3.6.7-bin.zip
RUN mkdir -p /home/admin/arthas && unzip /tmp/arthas-packaging-3.6.7-bin.zip -d $ARTHAS_HOME
RUN rm /tmp/arthas-packaging-3.6.7-bin.zip && chmod +x $ARTHAS_HOME/*sh

WORKDIR $ADMIN_HOME