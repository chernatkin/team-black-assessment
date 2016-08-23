# inspired by https://spring.io/guides/gs/spring-boot-docker/
FROM jensfischerhh/docker-java:8u45-jre

RUN mkdir /app
ADD build/libs/delivery-0.0.1-SNAPSHOT.jar /app/

WORKDIR /app

ENTRYPOINT ["java","-jar","/app/delivery-0.0.1-SNAPSHOT.jar"]
