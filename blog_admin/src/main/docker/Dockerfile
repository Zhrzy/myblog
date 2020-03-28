FROM frolvlad/alpine-oraclejdk8:slim
VOLUME /tmp
ADD blog_admin-1.0-SNAPSHOT.jar app.jar
#RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar","--spring.profiles.active=prod"]
EXPOSE 8001

