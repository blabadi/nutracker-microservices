FROM adoptopenjdk/openjdk11:jdk-11.0.1.13-alpine

#  unpacks spring boot uber jar to maker startup faster, see: https://spring.io/guides/gs/spring-boot-docker/
VOLUME /tmp

# this volume is to allow development live changes to the config server while running in docker container
# in the same time one can run config server locally without docker and it will work normally.
# see docker compose file for mounted dir.
VOLUME /app/local-config
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app

EXPOSE 8888

ENTRYPOINT ["java","-cp","app:app/lib/*","com.basharallabadi.nutracker.configserver.ConfigServerApplication"]

# alternative way to run the uber jar
#ADD target/configserver-server-0.0.1-SNAPSHOT.jar /app.jar
#ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]