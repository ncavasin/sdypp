FROM alpine:latest AS build

MAINTAINER Nicolas Cavasin ncavasin97@gmail.com

RUN apk update && apk upgrade && apk add --update maven openjdk11 && rm -rf /var/cache/apk/*

RUN mkdir -p /workspace
WORKDIR /workspace
COPY pom.xml /workspace
COPY src /workspace/src
RUN mvn -B package --file pom.xml -DskipTests


FROM openjdk:14-slim
COPY --from=build /workspace/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]