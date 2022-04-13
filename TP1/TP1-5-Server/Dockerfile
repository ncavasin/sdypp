FROM adoptopenjdk/openjdk11:jre-11.0.9_11.1-alpine

MAINTAINER Nicolas Cavasin ncavasin97@gmail.com

RUN #apk update && apk upgrade && apk add --update openjdk11-jre && rm -rf /var/cache/apk/*

COPY target/*.jar /app.jar

ENV ip=$ip
ENV port=$port

CMD java -jar /app.jar $ip $port