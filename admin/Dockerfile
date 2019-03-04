# this base image has extra stuff, see /run/docker/base-images
FROM basharlabadi/openjdk11-11.0.13-alpine

#  unpacks spring boot uber jar to maker startup faster, see: https://spring.io/guides/gs/spring-boot-docker/
VOLUME /tmp
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app

EXPOSE 9009

ENTRYPOINT ["java","-cp","app:app/lib/*","com.basharallabadi.nutracker.admin.AdminApplication"]
