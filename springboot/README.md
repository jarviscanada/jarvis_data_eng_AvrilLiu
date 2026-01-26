# Spring Boot Trading Application

---

## 🚀 Overview

A Spring Boot–based backend trading application providing RESTful APIs for:

- Managing traders and their accounts
- Performing basic account operations (deposit and withdrawal)
- Retrieving stock market quotes from an external data provider (EODHD)

The application models core trading-related entities such as **Trader**, **Account**, and **Market Quote**, and exposes HTTP endpoints to support account management and market data access.

It is built with a layered Spring Boot architecture, focusing on clean API design, request handling, and persistence.

---

## 🏗 Architecture

The application follows a layered Spring Boot architecture, separating responsibilities across controllers, services, and data access layers.

```text
Controller (REST API)
  |
  v
Service (Business Logic)
  |
  v
DAO (Persistence)
  |
  v
Database

```
- **Controller layer**  
  Handles HTTP request mapping, parameter binding, and response semantics.

- **Service layer**  
  Contains business logic, validation rules, and orchestration between domain entities.

- **DAO layer**  
  Responsible for database access and persistence using relational data models.

This structure keeps controllers lightweight, centralizes domain logic in services, and promotes clear separation of concerns.

---

## 📁 Project Structure

```text
src/main/java/ca/jrvs/apps/trading
├── controller
│   ├── EodhdQuoteController
│   └── TraderAccountController
│
├── service
│   ├── EodhdQuoteService
│   └── TraderAccountService
│
├── dao
│   ├── TraderDao
│   ├── AccountDao
│   ├── QuoteDao
│   ├── PositionDao
│   └── SecurityOrderDao
│
├── model
│   ├── Trader
│   ├── Account
│   ├── Position
│   ├── SecurityOrder
│   └── EodhdQuote
│
├── view
│   └── TraderAccountView
│
├── config
│   └── SwaggerConfig
│
└── ApplicationConfig
```

---

## ⚙️ Core Features

### Trader & Account Management
- Create traders and automatically initialize associated accounts
- Delete traders with validation on account state and positions
- Deposit and withdraw funds with balance checks and business rule enforcement

### Market Data Access
- Retrieve real-time stock quotes from an external market data provider (EODHD)
- Persist retrieved market quotes for downstream access
- Expose quote retrieval and update operations via REST endpoints

### RESTful API Design
- Resource-oriented endpoints following REST conventions
- Proper use of HTTP methods (`GET`, `POST`, `PUT`, `DELETE`)
- Clear separation between request models, domain models, and response views

### Backend Architecture
- Thin controller layer focused on request handling and HTTP semantics
- Centralized business logic in the service layer
- DAO-based persistence with relational data modeling

---

## 🔌 API Endpoints

### Trader APIs

| Method | Endpoint                                                                 | Description |
|-------:|--------------------------------------------------------------------------|-------------|
| POST   | `/trader/`                                                               | Create a trader using request body |
| POST   | `/trader/firstname/{firstname}/lastname/{lastname}/dob/{dob}/country/{country}/email/{email}` | Create a trader using path parameters |
| DELETE | `/trader/traderId/{traderId}`                                             | Delete a trader by ID |
| PUT    | `/trader/deposit/traderId/{traderId}/amount/{amount}`                    | Deposit funds into a trader's account |
| PUT    | `/trader/withdraw/traderId/{traderId}/amount/{amount}`                   | Withdraw funds from a trader's account |

### Market Data APIs

| Method | Endpoint                           | Description |
|-------:|------------------------------------|-------------|
| GET    | `/quote/eodhd/ticker/{ticker}`     | Retrieve latest market quote for a ticker |
| PUT    | `/quote/eodhd/ticker/{ticker}`     | Fetch and persist latest quote into database |

---

## 🧠 Design Notes

- **Trader as the aggregate root**  
  Trader is treated as the primary aggregate root. Account-related operations (deposit and withdrawal) are scoped under trader-level APIs, reflecting the domain relationship where an account does not exist independently of a trader.

- **Thin controllers, service-driven logic**  
  Controllers are kept lightweight and focused on HTTP concerns, while validation and business rules are centralized in the service layer.

- **View models for aggregated responses**  
  Composite response objects (e.g. `TraderAccountView`) are used to return logically related domain entities in a single API response, avoiding overexposure of internal models.

---

## ▶️ How to Run

1. Clone the repository
2. Configure database and application properties as needed
3. Start the application:
   ```bash
   mvn spring-boot:run
