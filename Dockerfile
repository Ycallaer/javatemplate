FROM openjdk:8
MAINTAINER Yves Callaert
RUN apt-get update && apt-get install --only-upgrade bash && apt-get --assume-yes install sudo apt-transport-https ca-certificates telnet vim

RUN echo "deb https://artifacts.elastic.co/packages/6.x/apt stable main" | tee -a /etc/apt/sources.list.d/elastic-6.x.list
RUN wget -qO - https://artifacts.elastic.co/GPG-KEY-elasticsearch | apt-key add -

RUN apt-get update && apt-get install logstash


COPY logstash.conf /etc/logstash/conf.d/logstash.conf


#TODO: Get this file from nexus instead of locally


ENTRYPOINT [""]