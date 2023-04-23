FROM #{[ JAVA_RUNTIME ]}

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' > /etc/timezone

ENV LANG="en_US.UTF-8"
ENV TERM=xterm
ENV TIMESTAMP currentTime

RUN mkdir -p /home/admin/app/
RUN wget -q '#{[ PACKAGE_URL ]}' -O /home/admin/app/app.jar
RUN echo -e 'exec java #{[ JAR_START_OPTIONS ]} -jar #{[ JAR_START_ARGS ]} /home/admin/app/app.jar' > /home/admin/start.sh && chmod +x /home/admin/start.sh

WORKDIR $ADMIN_HOME

CMD ["/bin/bash", "/home/admin/start.sh"]