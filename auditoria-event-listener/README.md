# Keycloak Event Listener para Azure Event Hub

Este módulo implementa um Event Listener para Keycloak que captura e envia eventos de autenticação para o Azure Event Hub, permitindo auditoria centralizada e monitoramento de eventos de segurança.

## Eventos Monitorados

O listener monitora eventos habilitados no Realm do Keycloak. Eventos comuns incluem:
- LOGIN
- LOGOUT
- REGISTER
- UPDATE_PROFILE
- UPDATE_PASSWORD
- DELETE_ACCOUNT

Veja outros tipos de eventos aqui:
https://chatgpt.com/share/681e6c34-5e98-8012-b3c9-c57b6a28ac2e

Verificação dos eventos habilitados é feita em tempo de execução, consultando a configuração do Realm.

## Configuração

1. **Variáveis de ambiente necessárias**:
   - `EVENT_HUB_CONNECTION_STRING`: String de conexão do Azure Event Hub
   - `EVENT_HUB_NAME`: Nome do Event Hub para envio dos eventos

2. **Compilação**:
   ```bash
   mvn clean package
   ```
   
   O plugin `maven-jar-plugin` automaticamente copia o arquivo JAR para o diretório `../keycloak/providers/`.

3. **Ativação no Keycloak**:
   - Acesse o console de administração do Keycloak
   - Vá para o Realm desejado → Realm Settings → Events
   - Na seção "Event Listeners", adicione "auditoria-event-listener"
   - Ative "Save Events" e configure a expiração dos eventos
   - Selecione os tipos de eventos que deseja monitorar em "Saved Types"

## Detalhes Técnicos

O projeto usa:
- Java 17
- Maven
- Keycloak 26.2.3
- Azure Event Hubs SDK 5.18.0

## Estrutura do Projeto

- `AuditoriaEventListenerProvider.java`: Implementação principal que processa e envia eventos para o Event Hub
- `AuditoriaEventListenerProviderFactory.java`: Factory para criar instâncias do provider
- Configuração SPI no arquivo `META-INF/services/org.keycloak.events.EventListenerProviderFactory`

## Integração com Docker Compose

O serviço está integrado ao ambiente através do docker-compose, que define as variáveis de ambiente necessárias e monta os diretórios adequados para o funcionamento do listener.

## Limitações

- Atualmente, eventos administrativos (`AdminEvent`) não são processados 