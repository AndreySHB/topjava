FROM tomcat:9-jre11
COPY topjava-docker.war                           /usr/local/tomcat/webapps/
COPY config/messages/app_ru.properties     /config/messages/
COPY config/messages/app.properties        /config/messages/
