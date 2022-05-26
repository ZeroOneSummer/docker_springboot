FROM java:8
MAINTAINER aurora
WORKDIR /ROOT
ARG JAR_DIR=/lib \
    LOG_DIR=/logs \
    CONFIG_DIR=/conf
VOLUME $JAR_DIR $LOG_DIR $CONFIG_DIR
EXPOSE 8088
ADD /zero_test-1.0-SNAPSHOT.jar $JAR_DIR/application.jar
ENTRYPOINT ["java", "-Xmx350m", "-jar", "/lib/application.jar"]
CMD ["java", "-version"]
