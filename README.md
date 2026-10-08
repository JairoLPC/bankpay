# BankPay API — Sistema Simplificado de Pagamentos e Transferências

> **Projeto de Caráter Acadêmico e Prático**  
> **Tema:** Desenvolvimento de API RESTful para Transações Financeiras com Arquitetura em Camadas, Integridade Transacional e Consumo de Serviços Externos.  
> **Tecnologias:** Java 17, Spring Boot 3, Spring Data JPA, H2 Database, RestTemplate, Lombok.

---

## 1. Visão Geral do Projeto

O **BankPay API** é uma solução de backend desenvolvida para simular uma plataforma simplificada de transferências financeiras entre usuários comuns e lojistas. O sistema contempla cadastro de contas, validação de regras de negócio estritas, garantia de consistência de dados (operações ACID), autorização por serviço terceiro e notificação simulada.

---

## 2. Requisitos e Regras de Negócio

### 2.1 Tipos de Usuários
* **Comum (`COMUM`)**: Podem enviar e receber dinheiro para quaisquer usuários/lojistas.
* **Lojista (`MERCHANT`)**: Apenas recebem transferências; não podem enviar dinheiro.

### 2.2 Regras de Transferência
1. **Validação de Saldo:** O usuário pagador deve possuir saldo suficiente antes de efetivar o débito.
2. **Autorização Externa:** Antes de concretizar a transação, a aplicação consulta um serviço autorizador externo via HTTP REST.
3. **Atomicidade e Consistência (ACID):** O débito do pagador, o crédito do recebedor e a persistência do registro da transação ocorrem dentro do mesmo escopo transacional (`@Transactional`).
4. **Notificação:** Após a transação concluída, o serviço de notificação é acionado para notificar as partes de forma resiliente.
5. **Unicidade de Registros:** Documentos (CPF/CNPJ) e e-mails são únicos no banco de dados.

---

## 3. Arquitetura e Estrutura de Pacotes

O projeto segue a **Arquitetura em Camadas (Layered Architecture)** com responsabilidades bem definidas:

```
com.bankpay.api
├── controllers/            # Camada de Apresentação (Endpoints REST)
│   ├── TransactionController.java
│   └── UserController.java
├── domain/                 # Entidades de Domínio e Modelagem de Dados
│   ├── transaction/
│   │   └── Transaction.java
│   └── user/
│       ├── User.java
│       └── UserType.java
├── dtos/                   # Objetos de Transferência de Dados (Records)
│   ├── ExceptionDto.java
│   ├── NotificationDto.java
│   ├── TransactionDto.java
│   └── UserDto.java
├── infra/                  # Configurações Globais e Tratamento de Exceções
│   ├── AppConfig.java
│   └── ControllerExceptionHandler.java
├── repositories/           # Camada de Acesso a Dados (Spring Data JPA)
│   ├── TransactionRepository.java
│   └── UserRepository.java
├── services/               # Camada de Regras de Negócio e Serviços Externos
│   ├── NotificationService.java
│   ├── TransactionService.java
│   └── UserService.java
└── ApiApplication.java     # Classe Principal / Bootstrap Spring Boot
```

---

## 4. Tecnologias e Ferramentas Utilizadas

| Tecnologia | Descrição / Finalidade |
| :--- | :--- |
| **Java 17 (LTS)** | Linguagem base com uso de recursos modernos (e.g., *Records*, *Pattern Matching*). |
| **Spring Boot 3.x** | Framework base para produtividade e convenção sobre configuração. |
| **Spring Data JPA / Hibernate** | Mapeamento Objeto-Relacional (ORM) e persistência de dados. |
| **H2 Database (In-Memory)** | Banco de dados em memória para agilidade nos testes e desenvolvimento. |
| **RestTemplate** | Cliente HTTP síncrono para integração com APIs externas. |
| **Project Lombok** | Redução de código boilerplate (`@Getter`, `@Setter`, `@NoArgsConstructor`, etc.). |
| **Maven** | Gerenciador de dependências e automação de build do projeto. |

---

## 5. Documentação dos Endpoints (API REST)

### 5.1 Usuários (`/users`)

#### • Criar Usuário
* **Método:** `POST /users`
* **Corpo da Requisição (Body):**
```json
{
  "firstName": "João",
  "lastName": "Silva",
  "document": "12345678900",
  "email": "joao.silva@email.com",
  "password": "senhaSegura123",
  "balance": 500.00,
  "userType": "COMUM"
}
```
* **Status de Retorno:** `201 Created`

#### • Listar Todos os Usuários
* **Método:** `GET /users`
* **Status de Retorno:** `200 OK`

---

### 5.2 Transações (`/transactions`)

#### • Efetuar Transferência
* **Método:** `POST /transactions`
* **Corpo da Requisição (Body):**
```json
{
  "senderId": 1,
  "receiverId": 2,
  "value": 150.00
}
```
* **Status de Retorno:** `201 Created`
* **Exemplo de Resposta:**
```json
{
  "id": 1,
  "amount": 150.00,
  "sender": {
    "id": 1,
    "firstName": "João",
    "balance": 350.00,
    "userType": "COMUM"
  },
  "receiver": {
    "id": 2,
    "firstName": "Maria",
    "balance": 150.00,
    "userType": "MERCHANT"
  },
  "timestamp": "2026-10-08T13:30:00.000"
}
```

---

### 5.3 Tratamento Global de Exceções (`ControllerExceptionHandler`)

A API padroniza as respostas de erro através do formato `ExceptionDto`:

| Código HTTP | Cenário | Formato de Resposta |
| :--- | :--- | :--- |
| **`400 Bad Request`** | Violação de integridade (CPF/E-mail duplicado) | `{"message": "Usuário já cadastrado ou dado duplicado", "statusCode": "400"}` |
| **`404 Not Found`** | Usuário pagador ou recebedor não encontrado | Corpo vazio |
| **`500 Internal Server Error`** | Regras violadas (saldo insuficiente, lojista pagador, autorização recusada) | `{"message": "Transaction not authorized", "statusCode": "500"}` |

---

## 6. Como Executar o Projeto

### Pré-requisitos
* Java Development Kit (**JDK 17** ou superior).
* Apache Maven (ou utilizar o wrapper `./mvnw` incluso).

### Passo a Passo

1. **Clonar o Repositório:**
   ```bash
   git clone https://github.com/SEU_USUARIO/NOME_DO_REPOSITORIO.git
   cd NOME_DO_REPOSITORIO/api
   ```

2. **Compilar e Executar:**
   ```bash
   # Utilizando o Maven Wrapper (Windows PowerShell/CMD):
   .\mvnw.cmd spring-boot:run

   # Ou utilizando o Maven instalado:
   mvn spring-boot:run
   ```

3. **Acessar a Aplicação:**
   * A API estará disponível em: `http://localhost:8080`
   * Console do Banco H2: `http://localhost:8080/h2-console`
     * **JDBC URL:** `jdbc:h2:mem:testdb`
     * **User Name:** `sa`
     * **Password:** *(vazio)*

---

## 7. Considerações Acadêmicas e Aprendizados

Durante o desenvolvimento deste projeto, foram aplicados e consolidados os seguintes conceitos de Engenharia de Software e Desenvolvimento Backend:

1. **Separação de Preocupações (SoC):** Desacoplamento entre controladores, serviços de domínio e camadas de persistência.
2. **Uso de DTOs e Records:** Imutabilidade e segurança no tráfego de dados entre cliente e servidor, evitando a exposição indevida de entidades JPA.
3. **Consistência em Transações Financeiras:** Aplicação de `@Transactional` para evitar estados inconsistentes em cenários de falha parcial.
4. **Resiliência e Tolerância a Falhas:** Tratamento com blocos `try-catch` em chamadas HTTP remotas (`RestTemplate`), garantindo que instabilidades em serviços de terceiros sejam devidamente manipuladas.
5. **Padronização de Erros REST:** Utilização de `@RestControllerAdvice` e `@ExceptionHandler` para centralizar e formatar respostas de erro da API.
