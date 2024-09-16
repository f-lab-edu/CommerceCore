FROM openjdk:17-jdk-alpine
WORKDIR /core/src/app
COPY build/libs/CommerceCore-0.0.1-SNAPSHOT.jar CommerceCore.jar
ENTRYPOINT ["java", "-jar" , "CommerceCore.jar"]
EXPOSE 8090
