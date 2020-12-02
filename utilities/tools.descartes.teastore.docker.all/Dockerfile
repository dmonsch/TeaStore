FROM descartesresearch/teastore-base:latest
MAINTAINER Chair of Software Engineering <se2-it@informatik.uni-wuerzburg.de>

RUN apt-get update
RUN apt-get install libhyperic-sigar-java

COPY target/*.war /usr/local/tomcat/webapps/