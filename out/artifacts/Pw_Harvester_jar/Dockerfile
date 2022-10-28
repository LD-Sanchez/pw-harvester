
FROM mcr.microsoft.com/playwright:v1.27.1-focal
RUN apt-get -y update
RUN mkdir -p /java
RUN apt-get install -y openjdk-11-jdk
RUN apt-get install -y openjdk-11-jre
RUN update-alternatives --config java
RUN update-alternatives --config javac
COPY * /tmp/
RUN mkdir -p /tmp/csv
WORKDIR /tmp
EXPOSE 8080
ENTRYPOINT ["java","-jar","Pw-Harvester.jar"]