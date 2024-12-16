FROM maven:3.9.9-amazoncorretto-23-alpine AS build
LABEL authors="Saravanakumar Arunachalam"

COPY . .

RUN mvn clean package

FROM amazoncorretto:23-jdk
WORKDIR /app
COPY --from=build target/video-stream.jar .
COPY --from=build target/classes/video/toystory.mp4 ./video/
EXPOSE 8080

ENTRYPOINT java $JAVA_OPTS -jar ./video-stream.jar