FROM openjdk:11-jdk
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ARG CREDENTIAL=build/resources/main/hf016-394001-2f7043693e67.json
COPY ${CREDENTIAL} /key
ENTRYPOINT ["java", "-jar", "app.jar"]