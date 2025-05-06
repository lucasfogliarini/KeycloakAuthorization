package com.auditoria;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventDataBatch;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Set;
import java.util.stream.Collectors;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuditoriaEventListenerProvider implements EventListenerProvider {
    private static final Logger logger = LoggerFactory.getLogger(AuditoriaEventListenerProvider.class);
    private final KeycloakSession session;
    private final EventHubProducerClient producer;
    private final ObjectMapper objectMapper;
    private final String eventHubName;

    public AuditoriaEventListenerProvider(KeycloakSession session) {
        this.session = session;
        this.objectMapper = new ObjectMapper();
        
        // Configuração do Event Hub
        String connectionString = System.getenv("EVENT_HUB_CONNECTION_STRING");
        this.eventHubName = System.getenv("EVENT_HUB_NAME");
        
        this.producer = new EventHubClientBuilder()
            .connectionString(connectionString, eventHubName)
            .buildProducerClient();
    }

    @Override
    public void onEvent(Event event) {
        RealmModel realm = session.getContext().getRealm();
        Set<String> enabledEventTypes = realm.getEnabledEventTypesStream()
                .collect(Collectors.toSet());

        if (!enabledEventTypes.contains(event.getType().name())) {
            logger.warn("Evento id '{}' do tipo '{}' não é uma evento habilitado e não será enviado para o Event Hub '{}'", event.getId(), event.getType(), eventHubName);
            logger.warn("Eventos habilitados: {}", enabledEventTypes);
            return;
        }

        logger.info("Enviando evento id '{}' do tipo '{}' para o Event Hub ...", event.getId(), event.getType());

        try {
            String eventJson = objectMapper.writeValueAsString(event);
            EventData eventData = new EventData(eventJson);
            
            EventDataBatch batch = producer.createBatch();
            batch.tryAdd(eventData);
            
            producer.send(batch);
            
            logger.info("Evento id '{}' do tipo '{}' enviado para o Event Hub '{}'", event.getId(), event.getType(), eventHubName);
        } catch (Exception e) {
            logger.error("Erro ao enviar evento para o Event Hub", e);
        }
    }

    @Override
    public void close() {
        if (producer != null) {
            producer.close();
        }
    }

    @Override
    public void onEvent(AdminEvent arg0, boolean arg1) {
        // TODO Auto-generated method stub
    }
} 