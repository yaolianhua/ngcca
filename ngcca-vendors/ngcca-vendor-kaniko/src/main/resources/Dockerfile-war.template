FROM #{[ JAVA_RUNTIME ]}

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' > /etc/timezone

ENV LANG="en_US.UTF-8"
ENV TERM=xterm
ENV TIMESTAMP currentTime

RUN wget -q '#{[ PACKAGE_URL ]}' -O /home/admin/tomcat/webapps/app.war

WORKDIR $ADMIN_HOME

CMD ["catalina.sh", "run"]