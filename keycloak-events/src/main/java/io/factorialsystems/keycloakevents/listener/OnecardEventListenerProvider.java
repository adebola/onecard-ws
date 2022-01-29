package io.factorialsystems.keycloakevents.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.keycloakevents.utils.User;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
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

            final ActiveMQConnectionFactory connectionFactory = createActiveMQConnectionFactory();
            final PooledConnectionFactory pooledConnectionFactory = createPooledConnectionFactory(connectionFactory);

            User user = User.builder()
                    .id(id)
                    .username(userModel.getUsername())
                    .firstName(userModel.getFirstName())
                    .lastName(userModel.getLastName())
                    .email(userModel.getEmail())
                    .emailVerified(userModel.isEmailVerified())
                    .enabled(userModel.isEnabled())
                    .build();

            try {
                sendMessage(pooledConnectionFactory, user);
            } catch (JMSException ex) {
                LOG.error(String.format("JMSException : %s", ex.getMessage()));
                ex.printStackTrace();
            } catch (JsonProcessingException e) {
                LOG.error(String.format("JsonProcessingException : %s", e.getMessage()));
                e.printStackTrace();
            } finally {
                pooledConnectionFactory.stop();
            }
        }
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {
    }

    @Override
    public void close() {
    }

    private ActiveMQConnectionFactory createActiveMQConnectionFactory() {
        String jmsUrl = System.getenv("JMS_URL");
        String jmsUser = System.getenv("JMS_USER");
        String jmsPassword = System.getenv("JMS_PASSWORD");

        LOG.info(String.format("URL: %s, User: %s, Password: %s", jmsUrl, jmsUser, jmsPassword));

        // Create a connection factory.
        final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(jmsUrl);

        // Pass the username and password.
        connectionFactory.setUserName(jmsUser);
        connectionFactory.setPassword(jmsPassword);
        return connectionFactory;
    }

    private PooledConnectionFactory createPooledConnectionFactory(ActiveMQConnectionFactory connectionFactory) {
        // Create a pooled connection factory.
        final PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setConnectionFactory(connectionFactory);
        pooledConnectionFactory.setMaxConnections(2);
        return pooledConnectionFactory;
    }

    private void sendMessage(PooledConnectionFactory pooledConnectionFactory, User user) throws JMSException, JsonProcessingException {
        // Establish a connection for the producer.
        final Connection producerConnection = pooledConnectionFactory.createConnection();
        producerConnection.start();

        // Create a session.
        final Session producerSession = producerConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Create a queue named "MyQueue".
        final Destination producerDestination = producerSession.createQueue("UserQueue");

        // Create a producer from the session to the queue.
        final MessageProducer producer = producerSession.createProducer(producerDestination);
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);


        // Create a message.
        ObjectMapper mapper = new ObjectMapper();
        final String text = mapper.writeValueAsString(user);
        final TextMessage producerMessage = producerSession.createTextMessage(text);

        // Send the message.
        producer.send(producerMessage);

        // Clean up the producer.
        producer.close();
        producerSession.close();
        producerConnection.close();
    }
}
