# Card Tokenization API

A secure and scalable card tokenization service built with **Spring Boot 3.2.6**, **Spring Security**, and **JPA**.  
This API allows hashing, storing, validating, and retrieving tokenized card information using HMAC-SHA256.

---

## 🚀 Features

- Tokenization using **HMAC-SHA256**
- Secure lookup key from environment variables
- Validation endpoint for card existence + caching with redis
- Retrieval of card ID by hash
- Spring Boot 3.2.6 modern architecture
- JPA + MySQL
- Clear separation of **Controller**, **Service**, and **Repository**

---

## 📂 Project Structure

```
src/main/java/br.com.hyperativa
 ├── config
 ├── controller
 ├── exception
 ├── model
 ├── repository
 ├── service
 └── util
    
```

---

## 🔧 Technologies

- Java 17+
- Spring Boot 3.2.6
- Spring Web
- Spring Data JPA
- Spring Security (minimal)
- HMAC-SHA256 (Java Crypto)
- Redis
- MySql / H2

---

## 🔑 Environment Variables

This project uses environment variables to manage sensitive information like database credentials, JWT secrets, and encryption keys. This ensures that no sensitive data is hardcoded in the application.properties file.

Create a .env file in the root of the project with the following variables:

| Variable      | Value                                                                     |
|---------------|---------------------------------------------------------------------------|
| `DB_URL`      | jdbc:mysql://localhost:3306/challangetest?useSSL=false&serverTimezone=UTC |
| `DB_USER`     | root                                                                      |
| `DB_PASSWORD` | root                                                                      |
| `SERVER_PORT` | 8081                                                                      |
| `JWT_SECRET`  | jwt_secret_here                                                           |
| `JWT_EXP_MS`  | 3600000                                                                   |
| `KEY_LOOKUP`  | _lookup_key_here                                                          |
| `KEY_ENC`     | your_encryption_key_here                                                  |

Note: This `.env` file should never be committed to version control.

---

## ▶️ How to Run

### **1. Clone the repository**
```bash
git clone https://github.com/your/repo.git
cd repo
```

### **2. Run with Maven**
```bash
mvn spring-boot:run
```

Default port: **8081**

---

## 📘 Endpoints

### **Tokenize Card**
`POST /api/tokenize`

Body:
```json
{
  "cardNumber": "4111111111111111"
}
```

Response:
```json
{
  "hash": "a94c...f02"
}
```

---

### **Check if Card Exists**
`GET /api/card/{hash}`

Response:
```json
{
  "exists": true,
  "id": "uuid-here"
}
```

---

### **Retrieve Card ID**
`GET /api/card/id/{hash}`

Response:
```json
{
  "id": "uuid-here"
}
```

---

## 🧪 Tests

H2 is used for in-memory tests.  
Run:

```bash
mvn test
```

---
## 🧪 Redis Setup

Option 1 — Using Docker (recommended)
Run:

```bash
docker run --name redis-hyperativa -p 6379:6379 -d redis
```

Option 2 — Local installation (Linux/Mac)
Run:

```bash
sudo apt update
sudo apt install redis-server
redis-server
```

---

## 📄 License

MIT License — Feel free to modify and use.

---

## ✨ Author

Developed by **Jonathan Denis Rufino da Costa**
