FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw -q -B dependency:go-offline
COPY src ./src
RUN ./mvnw -q -B package -DskipTests

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S openhrm && adduser -S openhrm -G openhrm
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
USER openhrm
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
