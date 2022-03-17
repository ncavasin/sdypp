
FROM alpine:latest
RUN apk update && apk upgrade && apk add --update openjdk11 tzdata curl unzip bash && rm -rf /var/cache/apk/*

MAINTAINER Nicolas Cavasin ncavasin97@gmail.com

COPY /target/rmi-server-1.0-SNAPSHOT.jar /app.jar

CMD ["/usr/bin/java", "-jar", "-Dspring.profiles.active=test", "/app.jar"]
