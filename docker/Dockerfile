FROM openjdk:8-jdk-alpine
COPY *.jar app.jar
CMD java ${JAVA_OPTIONS} -jar app.jar --spring.config.location=file:./config/${PROPERTIES_FILENAME}.properties