#!/usr/bin/env bash

pushd ..
mvn clean package
rm Docker/image/eloaa.war
cp target/gwt-eloaa-1.0.war Docker/image/eloaa.war
popd

docker build -t eloaa ./image
