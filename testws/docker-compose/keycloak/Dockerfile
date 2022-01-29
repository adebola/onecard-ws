FROM quay.io/keycloak/keycloak:15.0.2
COPY keycloak-events.jar /opt/jboss/keycloak/standalone/deployments

#RUN mkdir -p /opt/jboss/keycloak/modules/system/layers/keycloak/com/mysql/main

#COPY mysql-connector-java-8.0.26.jar /opt/jboss/keycloak/modules/system/layers/keycloak/com/mysql/main
#COPY module.xml /opt/jboss/keycloak/modules/system/layers/keycloak/com/mysql/main
#EXPOSE 8090
#ENTRYPOINT ["/opt/jboss/keycloak/bin/standalone.sh", "-b 0.0.0.0", "-Djboss.socket.binding.port-offset=10"]
