FROM openjdk:11-jdk
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ARG CREDENTIAL=build/resources/main/hf016-394001-2f7043693e67.json
COPY ${CREDENTIAL} /key
ARG CREDENTIAL2=build/resources/main/hf016-394001-1a57feae89db.json
COPY ${CREDENTIAL2} /key2
ENTRYPOINT ["java", "-jar", "app.jar"]