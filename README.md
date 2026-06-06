# E-Commerce API Backend

Welcome to the E-Commerce platform backend! 
This is a robust and scalable REST API developed in Java using the Spring Boot ecosystem, designed following clean architecture practices and advanced JWT-based security.

## Tech Stack

* **Java 17** (Eclipse Temurin)
* **Spring Boot 3.x**
  * Spring Security (JWT Authentication & Authorization)
  * Spring Data JPA
* **PostgreSQL 15** (Relational Database)
* **Maven** (Dependency Management)
* **Docker & Docker Compose** (Environment Containerization)

---

## Prerequisites

Before getting started, make sure you have the following installed on your local machine:
* [Docker Desktop](https://www.docker.com/products/docker-desktop/) active and running.
* [Git](https://git-scm.com/) for version control.
* An API client to test HTTP requests like [Postman](https://www.postman.com/) or Insomnia.

---

## Local Setup

### 1. Clone the repository
Clone the project to your local machine using the terminal:
```bash
git clone [https://github.com/JuanCG115/e-comerce.git](https://github.com/JuanCG115/e-comerce.git)
cd e-comerce
```

### 2. Configure Environment Variables
Create a file named `.env` in the root directory of the project (this file is ignored by Git via `.gitignore` and must never be committed). 
You can see an example in the `.env.example` file to configure your variables.

### 3. Run the application with Docker
To compile the application, download dependencies, and start both the database and the API, run:
```bash
docker-compose up --build
```

Once you see the log message `Tomcat started on port 8080`, the API will be live and listening at `http://localhost:8080`.

To stop the services safely without losing your database records, run:
```bash
docker-compose down
```

---

## Testing HTTP Requests

Since the containerized environment initializes with an empty database, Hibernate automatically generates the required table structures (`update` mode) upon startup. 
You can test and populate the API using HTTP clients like Postman.

## Authentication Endpoints (Public)
### Register a New User
* **HTTP Method:** `POST`
* **URL:** `http://localhost:8080/api/auth/register` (or your specific auth route)
* **Body (JSON):**
  ```json
  {
    "firstName": "",
    "lastName": "",
    "email": "",
    "password": ""
  }
  ```

### Get All Products (Public)
* **HTTP Method:** `GET`
* **URL:** `http://localhost:8080/api/products` in case you have published one or more products
* **Response:**
  ```json
  [
    {
        "id": 1,
        "name": "Teclado Mecánico RGB",
        "description": "Teclado con switches mecánicos configurables, ideal para desarrollo y gaming.",
        "price": 89.99,
        "stock": 15,
        "averageRating": 4.5,
        "createdAt": "2026-06-01T16:08:22.573668"
    }
  ]
  ```

### Get Review (Public)
* **HTTP Method:** `GET`
* **URL:** `http://localhost:8080/api/products/1` in case you already have more than one review
* **Response:**
  ```json
  {
    "id": 1,
    "name": "Teclado Mecánico RGB",
    "description": "Teclado con switches mecánicos configurables, ideal para desarrollo y gaming.",
    "price": 89.99,
    "stock": 15,
    "averageRating": 4.5,
    "createdAt": "2026-06-01T16:08:22.573668"
  }
  ```

### User Login (Obtain JWT Token)
* **HTTP Method:** `POST`
* **URL:** `http://localhost:8080/api/auth/login`
* **Body (JSON):**
  ```json
  {
  "email": "",
  "password": ""
  }
  ```
* **Response:** Copy the generated `token` string from the JSON response to access protected routes.

---

## E-Commerce Protected Endpoints
To interact with these resources, you must include the JWT token in your HTTP headers as a **Bearer Token**:

* **Header Key:** `Authorization`
* **Header Value:** `Bearer <your_copied_jwt_token>`

### Publish a product
* **HTTP Method:** `POST`
* **URL:** `http://localhost:8080/api/products`
* **Body (JSON):**
  ```json
  {
  "name": "",
  "description": "",
  "price": ,
  "stock": 
  }
  ```

### Publish a review
* **HTTP Method:** `POST`
* **URL:** `http://localhost:8080/api/products/1/reviews`
* **Body (JSON):**
  ```json
  {
  "rating": ,
  "comment": ""
  }
  ```

---

***Actually, I work on the frontend development of this project***
