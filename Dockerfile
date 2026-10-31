FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /workspace
COPY libraryapp/pom.xml libraryapp/pom.xml
COPY libraryapp/src libraryapp/src
RUN mvn -B -f libraryapp/pom.xml package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/libraryapp/target/libraryapp-1.0.0.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
