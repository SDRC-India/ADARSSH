#FROM openjdk:8-jdk-alpine
#RUN  apk update && apk upgrade && apk add netcat-openbsd
#VOLUME C:/Users/SDRC_DEV/dockerdata

#RUN mkdir -p /usr/local/rmnchawapp

#COPY ././target/rmnchawapp.jar /usr/local/rmnchawapp/
#WORKDIR C:/Users/SDRC_DEV/dockerworkdir

#ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/rmnchawapp.jar"]


FROM openjdk:8-jdk-alpine

RUN  apk update && apk upgrade && apk add netcat-openbsd

VOLUME C:/Users/SDRC_DEV/dockerdata

RUN mkdir -p /usr/local/rmnchawapp

COPY ./target/rmnchawapp.jar /usr/local/rmnchawapp/

EXPOSE 8080

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/usr/local/rmnchawapp/rmnchawapp.jar"]