FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /src

COPY pom.xml .
RUN mvn -B -q -DskipTests dependency:go-offline

COPY . .
RUN mvn -B -DskipTests clean package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /src/target/*.jar /app/app.jar
ENV JAVA_OPTS=""
EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar /app/app.jar"]