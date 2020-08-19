FROM openjdk:8-jdk-alpine
COPY *.jar app.jar
RUN mkdir -p config
RUN mkdir -p logs
CMD java ${JAVA_OPTIONS} -jar ${SPRING_BOOT_OPTIONS} app.jar