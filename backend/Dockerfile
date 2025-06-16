# Stage 1: Cache Gradle dependencies
FROM gradle:8.5-jdk17 AS cache
RUN mkdir -p /home/gradle/cache_home
ENV GRADLE_USER_HOME=/home/gradle/cache_home
COPY settings.gradle.kts build.gradle.kts gradle.properties /home/gradle/app/
COPY gradle /home/gradle/app/gradle
WORKDIR /home/gradle/app
RUN gradle dependencies --no-daemon

# Stage 2: Build Application
FROM gradle:8.5-jdk17 AS build
COPY --from=cache /home/gradle/cache_home /home/gradle/.gradle
COPY . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle shadowJar --no-daemon

# Stage 3: Runtime Image
FROM amazoncorretto:22-alpine-jdk AS runtime
EXPOSE 8080
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/backend-all.jar /app/ktor-docker-sample.jar
ENTRYPOINT ["java", "-jar", "/app/ktor-docker-sample.jar"]
