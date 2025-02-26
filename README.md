# Autenticação e Autorização com Keycloak

## Objetivo
Esta projeto tem como objetivo demonstrar fluxos de autenticação e autorização utilizando o Keycloak, aproveitando a biblioteca [Keycloak.AuthService](https://nikiforovall.github.io/keycloak-authorization-services-dotnet).

## Como subir o ambiente

1. Certifique-se de ter o Docker e Docker Compose instalados.

2. No diretório raiz do projeto, execute o seguinte comando para subir o ambiente:
   ```sh
   docker-compose up -d
   ```
3. O docker-compose irá subir a stack `keycloakauthorization` com:  
    - O container __keycloak__ na porta `5000` com o usuário `admin` e senha `admin` com as configurações predefinidas em `bem-realm-config.json`
    - O container __keycloak-authorization-api__ na porta 5001

## Como testar as permissões
Esta POC foi desenvolvida com base [neste exemplo de autorização](https://nikiforovall.github.io/keycloak-authorization-services-dotnet/authorization/resources.html) do Keycloak.AuthServices.  
Para validar todos esses cenários, acesse a [keycloak-authorization-api](http://localhost:5001/swagger/index.html):

1. __Usuário Reader - Consulta de Workspace__  
Dado que o usuário bem_user1 se autentica e obtém um token  
Quando ele tenta consultar um workspace com esse token  
Então a aplicação deve retornar um __Ok__.

2. __Usuário Reader - Exclusão de Workspace__  
Dado que o usuário bem_user1 se autentica e obtém um token  
Quando ele tenta excluir um workspace com esse token  
Então a aplicação deve negar a ação e retornar um status __403 (Forbidden)__.

3. __Usuário Admin - Consulta de Workspace__  
Dado que o usuário bem_admin1 se autentica e obtém um token  
Quando ele tenta consultar um workspace com esse token  
Então a aplicação deve retornar um __Ok__.

4. __Usuário Admin - Exclusão de Workspace__  
Dado que o usuário bem_admin1 se autentica e obtém um token  
Quando ele tenta excluir um workspace com esse token  
Então a aplicação deve retornar um __Ok__.

Observação  
O endpoint /Token foi criado para garantir o correto funcionamento em ambientes Docker, evitando problemas de validação do issuer. Isso ocorre porque, em alguns casos, o issuer é gerado como localhost, enquanto a API utiliza a rede local via bridge do Keycloak para autenticação e autorização.

## Configuração do Keycloak
Para facilitar a configuração do ambiente, o arquivo `bem-realm-config.json` de configuração do realm foi incluído na pasta `keycloak_imports` desta POC e é importado automaticamente ao subir o `docker-compose`.

### Configurações do Realm __BEM__
- **[Usuários](http://localhost:5000/admin/master/console/#/bem/users)**
  - `bem_user1`, senha `bem`
  - `bem_admin1`, senha `bem`
- **[Client](http://localhost:5000/admin/master/console/#/bem/clients)**
  - `bem-client` com as roles: `Reader` e `Admin`
- **[Recursos, Scopes, Policies e Permissions](http://localhost:5000/admin/master/console/#/bem/clients/f0c3aa03-f470-4fc9-8ad1-102479c03dea/authorization)**

    - **Resource: my-workspace**
        - Representa um workspace no sistema.
        - Escopos disponíveis: read, write
    - **Policy: Require Admin Role**
        - Define que a permissão Delete Workspace requer a role "Admin".
        - Permissão: Delete Workspace
        - Escopo associado: write
    - **Policy: Require Reader Role**
        - Define que a permissão Read Workspace requer a role "Reader".
        - Permissão: Read  Workspace
        - Escopo associado: read
    - **Permission: Read Workspace**
        - Permite a leitura do recurso my-workspace.
        - Escopo necessário: read
    - **Permission: Delete Workspace**
        - Permite a exclusão do recurso my-workspace.
        - Escopo necessário: write

## Conceitos de Autorização no Keycloak

O Keycloak fornece um sistema de autorização baseado em políticas, permitindo controle detalhado de acesso a recursos protegidos. Os principais conceitos são:

- **[Recurso](http://localhost:5000/admin/master/console/#/bem/clients/f0c3aa03-f470-4fc9-8ad1-102479c03dea/authorization/resources)**  

    Recurso (Resource): Representa uma entidade protegida dentro do sistema, como um endpoint, um documento ou um serviço. No contexto de controle de acesso, um recurso pode possuir diferentes níveis de permissões e escopos associados.

    Ex.: /orders, /orders/:id, my-workspace

- **[Scope](http://localhost:5000/admin/master/console/#/bem/clients/f0c3aa03-f470-4fc9-8ad1-102479c03dea/authorization/scopes)**

    Escopo (Scope): Define os tipos de ações que podem ser realizadas sobre um recurso. Cada escopo representa uma categoria de acesso, como leitura (read) ou escrita (write).

- **[Policy](http://localhost:5000/admin/master/console/#/bem/clients/f0c3aa03-f470-4fc9-8ad1-102479c03dea/authorization/policies)**

    Política (Policy): Define condições para conceder ou negar permissões, podendo ser baseadas em roles, grupos, atributos de usuário ou expressões. 

    Ex.: a política Require Reader Role exige que um usuário tenha a role "Reader" para acessar a permissão Read Workspace.

- **[Permission](http://localhost:5000/admin/master/console/#/bem/clients/f0c3aa03-f470-4fc9-8ad1-102479c03dea/authorization/permissions)**

    Permissão (Permission): Uma Permissão no Keycloak define quais usuários ou clientes podem acessar um Recurso com determinados Escopos. Ela vincula um recurso a uma ou mais Políticas que determinam as condições de acesso.

## Tecnologias Utilizadas
- [Keycloak](https://www.keycloak.org/)
- [Keycloak.AuthService](https://nikiforovall.github.io/keycloak-authorization-services-dotnet)

