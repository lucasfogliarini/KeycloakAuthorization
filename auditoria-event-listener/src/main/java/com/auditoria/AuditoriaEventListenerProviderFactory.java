package com.auditoria;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class AuditoriaEventListenerProviderFactory implements EventListenerProviderFactory {

    private AuditoriaEventListenerProvider auditoriaEventListener;

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        auditoriaEventListener = new AuditoriaEventListenerProvider(session);
        return auditoriaEventListener;
    }

    @Override
    public void init(Config.Scope config) {
        // Inicialização, caso necessário
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // Pós-inicialização, caso necessário
    }

    @Override
    public void close() {
        if (auditoriaEventListener != null) {
            auditoriaEventListener.close();
        }
    }

    @Override
    public String getId() {
        return "auditoria-event-listener";
    }
}
