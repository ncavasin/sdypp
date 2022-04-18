FROM adoptopenjdk/openjdk11:jre-11.0.9_11.1-alpine

MAINTAINER Nicolas Cavasin ncavasin97@gmail.com

COPY target/*.jar /app.jar

ENV ip=$ip
ENV port=$port

CMD java -jar /app.jar $ip $port