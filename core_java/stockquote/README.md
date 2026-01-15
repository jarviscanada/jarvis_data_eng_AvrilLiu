# Stock Quote Application

The **Stock Quote Application** is a console-based Java application that simulates a simple stock trading system.

The application allows users to:
- Retrieve real-time stock quotes from an external API
- Buy and sell stocks with unlimited virtual cash
- Track portfolio value, cost basis, and profit/loss
- Persist data using a PostgreSQL database
- Run consistently across environments using Docker

This project is designed to demonstrate **core Java backend engineering concepts**, including layered architecture, JDBC data access, external API integration, logging best practices, and containerization.

## 1. Application Architecture

The Stock Quote Application follows a **layered architecture** to ensure separation of concerns, maintainability, and testability.

The application is structured into the following layers:
````
Controller Layer
↓
Service Layer
↓
DAO Layer
↓
PostgreSQL Database
````

### Controller Layer
The controller acts as the **user interface** of the application.  
It is responsible for:
- Parsing user input from the command line
- Orchestrating calls to the service layer
- Handling invalid input and unexpected runtime errors
- Displaying output to the user

The controller contains **no business logic** and serves purely as an entry point.

---

### Service Layer
The service layer contains the **core business logic**, including:
- Fetching the latest stock quotes before any transaction
- Calculating portfolio value and profit/loss
- Enforcing business rules such as:
    - Unlimited virtual cash
    - No partial stock liquidation

This layer coordinates between the DAO layer and external APIs.

---

### DAO Layer
The DAO (Data Access Object) layer is responsible for:
- Interacting with the PostgreSQL database using JDBC
- Persisting and retrieving stock quotes and positions
- Encapsulating SQL logic from higher layers

Each DAO operates on a single domain entity and exposes a clean CRUD interface.

---

### External API Integration
The application integrates with the **Alpha Vantage API** to retrieve real-time stock market data.  
All HTTP communication and JSON parsing are isolated from the service layer to keep responsibilities clear.

## 2. Key Features

### Real-Time Stock Quotes
- Retrieves up-to-date stock price data from the Alpha Vantage API
- Ensures all transactions use the latest available market data
- Supports querying stock information before making any purchase decisions

---

### Stock Trading Simulation
- Allows users to buy stocks with unlimited virtual cash
- Prevents partial liquidation of positions (all shares of a symbol must be sold together)
- Transactions are executed instantly without order matching delays

---

### Portfolio Management
- Persists user positions in a PostgreSQL database
- Displays current holdings including:
    - Number of shares
    - Total cost basis
    - Current market value
    - Profit and loss (P/L)
- Portfolio values are recalculated using the most recent market prices

---

### Robust Input Handling
- Validates user commands and arguments
- Prevents application crashes due to invalid input
- Keeps the application running even when unexpected errors occur

---

### Persistent Storage
- Uses JDBC to interact with a PostgreSQL database
- Stores stock quotes and positions for reliable data persistence
- Ensures data consistency across application restarts

## 3. Technology Stack

### Programming Language
- **Java 17**
- Object-oriented design with clear separation of responsibilities

---

### Data Access
- **JDBC** for database connectivity
- **PostgreSQL** as the relational database
- DAO pattern used to abstract persistence logic

---

### External API
- **Alpha Vantage API** for real-time stock market data
- HTTP communication handled via **OkHttp**
- JSON parsing handled using **Jackson**

---

### Logging
- **SLF4J** as the logging facade
- **Logback** as the logging implementation
- Separate log files configured for:
    - Application flow
    - Error-level events

---

### Containerization
- **Docker** used to containerize the Java application
- **Dockerfile** created to build a runnable application image
- **docker-compose** used to manage PostgreSQL as a dependent service

---

### Build & Dependency Management
- **Maven** for project build lifecycle and dependency management

---

## 4. Project Scope & Learning Outcomes

This project was developed incrementally following a structured, multi-day workflow.

Key learning outcomes include:
- Understanding JDBC-based data access and SQL persistence
- Applying layered architecture (Controller, Service, DAO)
- Integrating external HTTP APIs and parsing JSON responses
- Implementing logging using SLF4J and Logback
- Managing application dependencies and configuration
- Containerizing a Java application using Docker and Docker Compose

The project emphasizes clean architecture, separation of concerns, and real-world backend development practices.