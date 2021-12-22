package io.factorialsystems.keycloakevents.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.keycloakevents.utils.User;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.jms.client.ActiveMQJMSConnectionFactory;
import org.jboss.logging.Logger;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RealmProvider;
import org.keycloak.models.UserModel;

import javax.jms.*;

public class OnecardEventListenerProvider implements EventListenerProvider {

    private final RealmProvider model;
    private final KeycloakSession session;
    private static final Logger LOG = Logger.getLogger(OnecardEventListenerProvider.class);

    public OnecardEventListenerProvider(KeycloakSession session) {
        this.session = session;
        this.model = session.realms();
    }

    @Override
    public void onEvent(Event event) {

        LOG.infov("Event Type {0} Captured", event.getType().toString());

         if (EventType.REGISTER.equals(event.getType())) {
            String id = event.getUserId();
            RealmModel realmModel = model.getRealm(event.getRealmId());
            UserModel userModel = session.users().getUserById(id, realmModel);

            String jmsUrl = System.getenv("JMS_URL");
            String jmsUser = System.getenv("JMS_USER");
            String jmsPassword = System.getenv("JMS_PASSWORD");

            ActiveMQJMSConnectionFactory connectionFactory =
                    new ActiveMQJMSConnectionFactory(jmsUrl, jmsUser , jmsPassword);
            Queue userQueue = ActiveMQJMSClient.createQueue("UserQueue");

            User user = User.builder()
                    .id(id)
                    .username(userModel.getUsername())
                    .firstName(userModel.getFirstName())
                    .lastName(userModel.getLastName())
                    .email(userModel.getEmail())
                    .emailVerified(userModel.isEmailVerified())
                    .enabled(userModel.isEnabled())
                    .build();

            ObjectMapper mapper = new ObjectMapper();

            try {
                Connection connection = connectionFactory.createConnection();
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                MessageProducer producer = session.createProducer(userQueue);
                connection.start();

                String text = mapper.writeValueAsString(user);
                TextMessage message = session.createTextMessage(text);
                producer.send(message);
                connection.close();
            } catch (Exception e) {
                LOG.info("Exception Thrown " + e.getMessage());
                e.printStackTrace();
            }
         }
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {

    }

    @Override
    public void close() {

    }
}
