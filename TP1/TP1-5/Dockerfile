FROM alpine:latest

MAINTAINER Nicolas Cavasin ncavasin97@gmail.com

RUN apk update && apk upgrade && apk add --update openjdk11 tzdata curl unzip bash && rm -rf /var/cache/apk/*

COPY target/*.jar /app.jar

CMD ["/usr/bin/java", "-jar", "/app.jar"]
