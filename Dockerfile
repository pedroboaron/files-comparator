FROM building5/dumb-init:1.2.1 as init

FROM maven:3.6.3-jdk-11-slim AS build
RUN mkdir /project
COPY . /project
WORKDIR /project
RUN mvn clean package -DskipTests -B

FROM adoptopenjdk/openjdk11:jre-11.0.9.1_1-alpine
RUN apk add dumb-init
RUN mkdir /app
RUN addgroup --system javauser && adduser -S -s /bin/false -G javauser javauser
COPY --from=init /dumb-init /usr/local/bin/
COPY --from=build /project/target/files-service-0.0.1-SNAPSHOT.jar /app/files-service.jar
WORKDIR /app
RUN chown -R javauser:javauser /app
USER javauser

EXPOSE 8082

CMD "dumb-init" "java" "-jar" "/app/files-service.jar"