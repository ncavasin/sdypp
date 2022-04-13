FROM alpine:latest

MAINTAINER Nicolas Cavasin ncavasin97@gmail.com

RUN apk update && apk upgrade && apk add --update openjdk11 && rm -rf /var/cache/apk/*

COPY target/*.jar /app.jar

ENV ip=$ip
ENV port=$port

CMD ["/usr/bin/java", "-jar", "/app.jar", "$ip", "$port"]
