#! /bin/sh

git pull origin master
~/tomcat/catalina.sh stop -force
rm -rf ~/tomcat/logs/*
rm -rf ~/tomcat/webapps/*
grails dev war ~/tomcat/webapps/api.war
~/tomcat/catalina.sh start