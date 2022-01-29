package io.factorialsystems.keycloakevents.listener;

import io.factorialsystems.keycloakevents.utils.Utils;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class OnecardEventListenerProviderFactory implements EventListenerProviderFactory {

    private static final String ID = "onecard-event-listener";
    private static final Logger LOG = Logger.getLogger(OnecardEventListenerProviderFactory.class);

    @Override
    public EventListenerProvider create(KeycloakSession keycloakSession) {
        OnecardEventListenerProvider provider = new OnecardEventListenerProvider(keycloakSession);
        keycloakSession.enlistForClose(provider);

        return provider;
    }

    @Override
    public void init(Config.Scope scope) {
        LOG.infov("Init Keycloak extension component={0}", Utils.toComponentIdString(this));

        String jmsUrl = System.getenv("JMS_URL");
        String jmsUser = System.getenv("JMS_USER");
        String jmsPassword = System.getenv("JMS_PASSWORD");

        if (jmsUrl == null || jmsUser == null || jmsPassword == null) {
            throw new RuntimeException("Environment Variables JMS_USER, JMS_PASSWORD & JMS_URL must be set, please set and restart the server");
        }

        LOG.infov("JMS_URL {0}, JMS_USER {1}, JMS_PASSWORD {2}",jmsUrl, jmsUser, jmsPassword);
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
        LOG.infov("PostInit Keycloak extension component={0}", Utils.toComponentIdString(this));
    }

    @Override
    public void close() {
        LOG.infov("Closing Keycloak extension component={0}", Utils.toComponentIdString(this));
    }

    @Override
    public String getId() {
        return ID;
    }
}
