#!/usr/bin/env bash

docker run --rm -it \
    -p 8090:8080 \
    -v /home/seans/unRAID/mnt/user/apps/eloaa:/config \
    -v /home/seans/unRAID/mnt/user/media/downloads:/downloads \
    -v /home/seans/unRAID/mnt/user/media/movies:/movies \
    -v /home/seans/unRAID/mnt/user/apps/eloaa_logs:/usr/local/tomcat/logs \
    stuckless/eloaa

echo "use http://localhost:8090/eloaa/ to access server"

