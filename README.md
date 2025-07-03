# Eagle Bank

## Project Overview
Eagle Bank is a modular, feature-based banking system designed with domain-driven design (DDD) principles. It supports user management, bank accounts, transactions, and authentication, and is structured for easy scaling and microservice migration.

## Architecture Overview
- **Feature-based structure:** Code is organized by business domain (user, account, transaction, auth).
- **Main modules:**
  - User: Registration, profile, authentication
  - Account: Bank account management
  - Transaction: Deposits, withdrawals, transfers
  - Auth: JWT-based authentication
- **DDD & Microservice-ready:** Each feature can be split into a microservice.

## Setup Instructions
1. **Prerequisites:**
   - Java 17+
   - Maven 3.8+
   - PostgreSQL or compatible DB
2. **Build:**
   ```sh
   ./mvnw clean install
   ```
3. **Run:**
   ```sh
   ./mvnw spring-boot:run
   ```
4. **Test:**
   ```sh
   ./mvnw test
   ```

## API Documentation
- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- OpenAPI spec: `/v3/api-docs`
- Example endpoints:
  - `POST /v1/users` - Create user
  - `POST /v1/auth/login` - Authenticate user
  - `POST /v1/accounts/user/{userId}` - Create account
  - `GET /v1/accounts/{accountId}` - Get account (JWT required)
  - `POST /v1/accounts/{accountId}/transactions` - Create transaction

## Security
- **Authentication:** JWT Bearer tokens required for most endpoints.
- **How to obtain a token:** Use `/v1/auth/login` with valid credentials.
- **Authorization:** Endpoints validate user ownership for sensitive operations.

## Testing
- **Unit tests:** Run with `./mvnw test`.
- **Integration tests:** Included for key flows.
- **Coverage:** All main business scenarios are covered.

## Database
- **Schema:** See `src/main/resources/schema.sql`.
- **Default DB:** Uses [H2 Database](https://www.h2database.com/) for local development and testing (in-memory by default).
- **Production DB:** Configure PostgreSQL or another RDBMS in `application.yml`.
- **Data Initialization
- **Configuration:** See `src/main/resources/application.yml` for H2 settings.
