FROM tomcat:7-jre7
MAINTAINER eloaa@gmail.com

ADD eloaa.war /usr/local/tomcat/webapps/

ENV ELOAA_DOWNLOAD_DIR=/downloads
ENV ELOAA_MOVIES_DIR=/movies
ENV ELOAA_CONFIG_DIR=/config

VOLUME ["/config", "/downloads", "/movies", "/usr/local/tomcat/logs"]

# Web Server
EXPOSE 8080

CMD ["catalina.sh", "run"]