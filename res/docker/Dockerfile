FROM jenkins/jenkins:latest
ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false
ENV CASC_JENKINS_CONFIG /var/jenkins_home/casc.yaml
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/ref/plugins.txt --verbose
#COPY casc.yaml /var/jenkins_home/casc.yaml

# install Ant, jacoco, maven
USER root
RUN apt-get update && apt-get install -y ant libjacoco-java maven libmaven-clean-plugin-java libmaven-jar-plugin-java libsurefire-java
USER jenkins
