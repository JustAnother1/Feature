#!/bin/bash
# Jenkins is fully up and running

docker run --name jenkins \
--rm -p 8080:8080 \
--env JENKINS_ADMIN_ID=admin --env JENKINS_ADMIN_PASSWORD=ihdul \
--volume $HOME/jenkins:/var/jenkins_home \
jenkins:jcasc

