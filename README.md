# Customers API

A RESTful API for managing customers, built with **Spring Boot 3** and secured with **HTTPS**.

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.2.3 |
| Language | Java 17 |
| Persistence | Spring Data JPA + H2 (in-memory) |
| Validation | Jakarta Bean Validation |
| Docs | SpringDoc OpenAPI (Swagger UI) |
| Build | Maven |

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+

### Run the application

```bash
./mvnw spring-boot:run
```

The server starts on **`https://localhost:8443`**.

> **Note:** The app uses a self-signed certificate. You'll need to use `-k` / `--insecure` with `curl`, or accept the certificate warning in your browser.

---

## API Reference

Base URL: `https://localhost:8443/api/customers`

### Customer Object

```json
{
  "id":        1,
  "firstName": "Alice",
  "lastName":  "Smith",
  "email":     "alice@example.com"
}
```

| Field | Type | Required | Notes |
|---|---|---|---|
| `id` | Long | auto | Set by server; ignored on create/update |
| `firstName` | String | yes | Must not be blank |
| `lastName` | String | yes | Must not be blank |
| `email` | String | yes | Must be a valid email address |

---

### Endpoints

#### Get all customers

```
GET /api/customers
```

**Response `200 OK`**
```json
[
  { "id": 1, "firstName": "Alice", "lastName": "Smith",  "email": "alice@example.com" },
  { "id": 2, "firstName": "Bob",   "lastName": "Jones",  "email": "bob@example.com"   },
  { "id": 3, "firstName": "Carol", "lastName": "White",  "email": "carol@example.com" }
]
```

```bash
curl -k https://localhost:8443/api/customers
```

---

#### Get a customer by ID

```
GET /api/customers/{id}
```

**Response `200 OK`**
```json
{ "id": 1, "firstName": "Alice", "lastName": "Smith", "email": "alice@example.com" }
```

**Response `404 Not Found`** — when the ID does not exist.

```bash
curl -k https://localhost:8443/api/customers/1
```

---

#### Create a customer

```
POST /api/customers
Content-Type: application/json
```

**Request body**
```json
{
  "firstName": "John",
  "lastName":  "Doe",
  "email":     "john.doe@example.com"
}
```

**Response `201 Created`**
```json
{ "id": 4, "firstName": "John", "lastName": "Doe", "email": "john.doe@example.com" }
```

**Response `400 Bad Request`** — when validation fails (blank fields or invalid email).

```bash
curl -k -X POST https://localhost:8443/api/customers \
  -H "Content-Type: application/json" \
  -d '{"firstName":"John","lastName":"Doe","email":"john.doe@example.com"}'
```

---

#### Update a customer

```
PUT /api/customers/{id}
Content-Type: application/json
```

**Request body**
```json
{
  "firstName": "Johnathan",
  "lastName":  "Doe",
  "email":     "johnathan.doe@example.com"
}
```

**Response `200 OK`** — returns the updated customer.
**Response `400 Bad Request`** — when validation fails.
**Response `404 Not Found`** — when the ID does not exist.

```bash
curl -k -X PUT https://localhost:8443/api/customers/4 \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Johnathan","lastName":"Doe","email":"johnathan.doe@example.com"}'
```

---

#### Delete a customer

```
DELETE /api/customers/{id}
```

**Response `204 No Content`** — customer deleted successfully.
**Response `404 Not Found`** — when the ID does not exist.

```bash
curl -k -X DELETE https://localhost:8443/api/customers/4
```

---

## Seed Data

On startup, the application automatically loads three sample customers:

| ID | First Name | Last Name | Email |
|---|---|---|---|
| 1 | Alice | Smith | alice@example.com |
| 2 | Bob | Jones | bob@example.com |
| 3 | Carol | White | carol@example.com |

---

## Swagger UI

Interactive API documentation is available at:

```
https://localhost:8443/swagger-ui.html
```

The raw OpenAPI spec (JSON) is at:

```
https://localhost:8443/v3/api-docs
```

---

## H2 Console

The in-memory database console is available at:

```
https://localhost:8443/h2-console
```

| Setting | Value |
|---|---|
| JDBC URL | `jdbc:h2:mem:customersdb` |
| Username | `sa` |
| Password | *(leave blank)* |

---

## Project Structure

```
src/
├── main/
│   ├── java/com/example/customers/
│   │   ├── CustomersApplication.java       # Entry point
│   │   ├── DataLoader.java                 # Seed data on startup
│   │   ├── config/
│   │   │   └── HttpsRedirectConfig.java    # HTTP → HTTPS redirect
│   │   ├── controller/
│   │   │   └── CustomerController.java     # REST endpoints
│   │   ├── exception/
│   │   │   ├── CustomerNotFoundException.java
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── model/
│   │   │   └── Customer.java               # JPA entity
│   │   ├── repository/
│   │   │   └── CustomerRepository.java     # Spring Data JPA
│   │   └── service/
│   │       └── CustomerService.java        # Business logic
│   └── resources/
│       ├── application.properties
│       └── keystore.p12                    # Self-signed TLS certificate
└── test/
    └── java/com/example/customers/
        ├── controller/CustomerControllerTest.java
        └── service/CustomerServiceTest.java
```

---

## Running Tests

```bash
./mvnw test
```